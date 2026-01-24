package com.stocktrack;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.StockDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Person in charge: [Student Name]

class ManageStockControllerTest {

    private ManageStockController stockController;
    private final String TEST_GROUP = "GROUP_TEST";

    @BeforeEach
    void setUp() {
        stockController = new ManageStockController();

        // Setup: Simuliamo un utente loggato con un gruppo assegnato.
        // Senza gruppo (groupUid null), il controller lancerebbe un'eccezione immediata.
        String TEST_USER = "stockTester";
        User user = new User(TEST_USER, "pass", Role.ADMIN, TEST_GROUP);
        SessionManager.getInstance().login(user);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Pulizia: Rimuoviamo tutti i prodotti creati nel gruppo di test per non sporcare InMemoryStockDAO
        StockDAO dao = DAOFactory.getStockDAO();
        // Usiamo una copia della lista o iteriamo con cautela
        var stocks = dao.getAllStocks(TEST_GROUP);
        for (var s : stocks) {
            dao.deleteStock(s.getNome(), TEST_GROUP);
        }

        SessionManager.getInstance().logout();
    }

    /**
     * Test Req: Aggiunta Prodotto.
     * Verifica che un prodotto venga aggiunto correttamente alla lista del gruppo.
     */
    @Test
    void testAddStockSuccess() throws Exception {
        StockBean newStock = new StockBean("Pasta", 10, 5);

        assertDoesNotThrow(() -> stockController.addStock(newStock),
                "L'aggiunta di un prodotto valido non deve lanciare eccezioni.");

        // Verifica: Il prodotto deve essere presente nella lista restituita dal controller
        List<StockBean> products = stockController.showAllProducts();
        boolean found = products.stream()
                .anyMatch(p -> p.getNome().equals("Pasta") && p.getQuantity() == 10);

        assertTrue(found, "Il prodotto 'Pasta' dovrebbe essere presente nella lista prodotti.");
    }

    /**
     * Test Req: Modifica Quantità (Eccezione Logica).
     * Verifica che il sistema impedisca di avere una quantità negativa (Business Logic).
     */
    @Test
    void testModifyQuantityNegativeException() throws Exception {
        // Setup: Aggiungo un prodotto con quantità 5
        stockController.addStock(new StockBean("Latte", 5, 2));

        // Azione: Provo a rimuovere 10 unità (5 - 10 = -5) -> Deve fallire
        Exception exception = assertThrows(Exception.class, () -> {stockController.modifyQuantity("Latte", -10);}, "Dovrebbe lanciare eccezione se la quantità risultante è negativa.");

        // Verifica del messaggio d'errore specifico
        assertEquals("Non puoi avere quantità negativa!", exception.getMessage());
    }

    /**
     * Test Req: Lista Spesa (Calcolo Sottoscorta).
     * Verifica che la lista della spesa filtri correttamente solo i prodotti sotto la soglia.
     */
    @Test
    void testGetShoppingList() throws Exception {
        // Setup:
        // 1. Acqua: Qta 2, Soglia 5 -> SOTTOSCORTA (Deve apparire nella lista spesa)
        stockController.addStock(new StockBean("Acqua", 2, 5));

        // 2. Pane: Qta 10, Soglia 2 -> OK (Non deve apparire)
        stockController.addStock(new StockBean("Pane", 10, 2));

        // Azione: Recupero lista spesa
        List<StockBean> shoppingList = stockController.getShoppingList();

        // Verifica
        assertEquals(1, shoppingList.size(), "La lista della spesa dovrebbe contenere solo 1 elemento (quello sottoscorta).");
        assertEquals("Acqua", shoppingList.get(0).getNome(), "...");
    }
}