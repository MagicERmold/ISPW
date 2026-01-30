package com.stocktrack;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;
import com.stocktrack.engineering.exception.InvalidProductDataException;
import com.stocktrack.engineering.exception.StorageException;
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

/**
 * Test class for ManageStockController.
 * Author: [Il Tuo Nome] [La Tua Matricola]
 */
class ManageStockControllerTest {

    private ManageStockController stockController;
    private final String TEST_GROUP = "GROUP_TEST";

    @BeforeEach
    void setUp() {
        stockController = new ManageStockController();
        // Setup: Logghiamo un utente con privilegi ADMIN e un gruppo valido
        String TEST_USER_NAME = "stockTester";
        User user = new User(TEST_USER_NAME, "pass", Role.ADMIN, TEST_GROUP);
        SessionManager.getInstance().login(user);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Pulizia: Rimuoviamo tutti gli stock creati durante i test per il gruppo di test
        StockDAO dao = DAOFactory.getStockDAO();
        var stocks = dao.getAllStocks(TEST_GROUP);
        for (var s : stocks) {
            dao.deleteStock(s.getName(), TEST_GROUP);
        }
        SessionManager.getInstance().logout();
    }

    /**
     * Test Req: Aggiunta Prodotto con successo.
     * Verifica che il prodotto venga salvato con i dati corretti (incluse le categorie).
     */
    @Test
    void testAddStockSuccess() throws Exception {
        StockBean newStock = new StockBean("Pasta", 10, 5, "Cibo");

        assertDoesNotThrow(() -> stockController.addStock(newStock),
                "L'aggiunta di un prodotto valido non deve lanciare eccezioni.");

        List<StockBean> products = stockController.showAllStocks();

        StockBean result = products.stream()
                .filter(p -> p.getNome().equals("Pasta"))
                .findFirst()
                .orElse(null);

        assertNotNull(result, "Il prodotto 'Pasta' dovrebbe essere presente.");
        assertEquals(10, result.getQuantity());
        assertEquals("Cibo", result.getCategory());
    }

    /**
     * Test Req: Validazione Dati Input.
     * Verifica che vengano lanciate eccezioni per nomi vuoti o valori negativi.
     */
    @Test
    void testAddStockValidationErrors() {
        // 1. Nome Vuoto
        StockBean emptyName = new StockBean("", 10, 5);
        assertThrows(InvalidProductDataException.class, () -> stockController.addStock(emptyName),
                "Dovrebbe lanciare eccezione per nome vuoto.");

        // 2. Quantità Negativa
        StockBean negativeQty = new StockBean("Latte", -1, 5);
        assertThrows(InvalidProductDataException.class, () -> stockController.addStock(negativeQty),
                "Dovrebbe lanciare eccezione per quantità negativa.");

        // 3. Soglia Negativa
        StockBean negativeThreshold = new StockBean("Pane", 10, -5);
        assertThrows(InvalidProductDataException.class, () -> stockController.addStock(negativeThreshold),
                "Dovrebbe lanciare eccezione per soglia negativa.");
    }

    /**
     * Test Req: Duplicati.
     * Verifica che non sia possibile aggiungere due prodotti con lo stesso nome.
     */
    @Test
    void testAddStockDuplicate() throws Exception {
        StockBean stock1 = new StockBean("Riso", 10, 2);
        stockController.addStock(stock1);

        StockBean stock2 = new StockBean("Riso", 5, 5); // Stesso nome

        Exception exception = assertThrows(InvalidProductDataException.class, () -> stockController.addStock(stock2));

        assertTrue(exception.getMessage().contains("esiste già"), "Il messaggio di errore deve indicare il duplicato.");
    }

    /**
     * Test Req: Sicurezza Gruppo.
     * Verifica che un utente senza gruppo non possa aggiungere stock.
     */
    @Test
    void testAddStockNoGroup() {
        // Logout utente con gruppo e login utente senza gruppo
        SessionManager.getInstance().logout();
        User lonelyUser = new User("lonely", "pass", Role.USER, null); // Gruppo null
        SessionManager.getInstance().login(lonelyUser);

        StockBean bean = new StockBean("Test", 1, 1);

        assertThrows(StorageException.class, () -> stockController.addStock(bean),
                "Un utente senza gruppo non dovrebbe poter aggiungere prodotti.");
    }

    /**
     * Test Req: Modifica Quantità (Acquisto e Consumo).
     */
    @Test
    void testModifyQuantitySuccess() throws Exception {
        // Setup
        stockController.addStock(new StockBean("Biscotti", 10, 2));

        // Azione 1: Acquisto (+5)
        stockController.modifyQuantity("Biscotti", 5);

        StockBean updated = stockController.showAllStocks().stream()
                .filter(p -> p.getNome().equals("Biscotti")).findFirst().orElseThrow();
        assertEquals(15, updated.getQuantity(), "10 + 5 dovrebbe fare 15.");

        // Azione 2: Consumo (-3)
        stockController.modifyQuantity("Biscotti", -3);

        updated = stockController.showAllStocks().stream()
                .filter(p -> p.getNome().equals("Biscotti")).findFirst().orElseThrow();
        assertEquals(12, updated.getQuantity(), "15 - 3 dovrebbe fare 12.");
    }

    /**
     * Test Req: Modifica Quantità (Errore Negativo).
     * Verifica che non si possa scendere sotto zero.
     */
    @Test
    void testModifyQuantityNegativeBalance() throws Exception {
        stockController.addStock(new StockBean("Sale", 5, 1));

        // Provo a togliere 10 da una scorta di 5
        Exception exception = assertThrows(InvalidProductDataException.class, () -> stockController.modifyQuantity("Sale", -10));

        assertTrue(exception.getMessage().contains("negativa"), "Il messaggio deve menzionare la quantità negativa.");
    }

    /**
     * Test Req: Modifica Quantità (Prodotto Non Trovato).
     */
    @Test
    void testModifyQuantityNotFound() {
        assertThrows(StorageException.class, () -> stockController.modifyQuantity("Fantasma", 5), "Dovrebbe lanciare StorageException se il prodotto non esiste.");
    }

    /**
     * Test Req: Lista della Spesa (Sottoscorta).
     */
    @Test
    void testGetShoppingList() throws Exception {
        // Setup:
        // 1. Acqua: Qta 2, Soglia 5 -> SOTTOSCORTA (2 < 5)
        stockController.addStock(new StockBean("Acqua", 2, 5, "Bevande"));

        // 2. Pane: Qta 10, Soglia 2 -> OK (10 > 2)
        stockController.addStock(new StockBean("Pane", 10, 2, "Cibo"));

        // 3. Vino: Qta 3, Soglia 3 -> OK (3 == 3, non strettamente minore) - Edge Case
        stockController.addStock(new StockBean("Vino", 3, 3, "Bevande"));

        List<StockBean> shoppingList = stockController.getShoppingList();

        assertEquals(1, shoppingList.size(), "Solo l'Acqua dovrebbe essere in lista.");
        StockBean item = shoppingList.getFirst();
        assertEquals("Acqua", item.getNome());
        assertEquals("Bevande", item.getCategory(), "La lista della spesa deve mantenere le categorie.");
    }

    /**
     * Test Req: Cancellazione Prodotto.
     */
    @Test
    void testDeleteStock() throws Exception {
        stockController.addStock(new StockBean("DaCancellare", 5, 1));

        // Cancellazione
        stockController.deleteStock("DaCancellare");

        // Verifica che non esista più
        List<StockBean> list = stockController.showAllStocks();
        boolean exists = list.stream().anyMatch(p -> p.getNome().equals("DaCancellare"));
        assertFalse(exists, "Il prodotto dovrebbe essere stato rimosso.");

        // Verifica errore su prodotto inesistente
        assertThrows(StorageException.class, () -> stockController.deleteStock("NonEsiste"));
    }

    /**
     * Test Req: Gestione Categorie.
     */
    @Test
    void testCategoriesAndFiltering() throws Exception {
        stockController.addStock(new StockBean("Mela", 10, 2, "Frutta"));
        stockController.addStock(new StockBean("Pera", 5, 2, "Frutta"));
        stockController.addStock(new StockBean("Carota", 5, 2, "Verdura"));

        // 1. Verifica getCategories
        List<String> categories = stockController.getCategories();
        assertTrue(categories.contains("Frutta"));
        assertTrue(categories.contains("Verdura"));

        // 2. Verifica getStocksByCategory
        List<StockBean> fruitOnly = stockController.getStocksByCategory("Frutta");
        assertEquals(2, fruitOnly.size(), "Dovrebbero esserci 2 frutti.");
        assertTrue(fruitOnly.stream().anyMatch(p -> p.getNome().equals("Mela")));
        assertFalse(fruitOnly.stream().anyMatch(p -> p.getNome().equals("Carota")));
    }
}