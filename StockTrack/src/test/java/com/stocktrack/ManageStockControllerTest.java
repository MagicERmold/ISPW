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

class ManageStockControllerTest {

    private ManageStockController stockController;
    private final String TEST_GROUP = "GROUP_TEST";

    @BeforeEach
    void setUp() {
        stockController = new ManageStockController();
        String TEST_USER = "stockTester";
        User user = new User(TEST_USER, "pass", Role.ADMIN, TEST_GROUP);
        SessionManager.getInstance().login(user);
    }

    @AfterEach
    void tearDown() throws Exception {
        StockDAO dao = DAOFactory.getStockDAO();
        var stocks = dao.getAllStocks(TEST_GROUP);
        for (var s : stocks) {
            dao.deleteStock(s.getName(), TEST_GROUP);
        }
        SessionManager.getInstance().logout();
    }

    @Test
    void testAddStockSuccess() throws Exception {
        // AGGIORNATO: Testiamo anche la categoria
        StockBean newStock = new StockBean("Pasta", 10, 5, "Cibo");

        assertDoesNotThrow(() -> stockController.addStock(newStock),
                "L'aggiunta di un prodotto valido non deve lanciare eccezioni.");

        List<StockBean> products = stockController.showAllProducts();

        // Cerchiamo il prodotto
        StockBean result = products.stream()
                .filter(p -> p.getNome().equals("Pasta"))
                .findFirst()
                .orElse(null);

        assertNotNull(result, "Il prodotto 'Pasta' dovrebbe essere presente.");
        assertEquals(10, result.getQuantity());
        assertEquals("Cibo", result.getCategory(), "La categoria deve essere salvata correttamente.");
    }

    @Test
    void testModifyQuantityNegativeException() throws Exception {
        // Default "Generico" se usiamo costruttore a 3 parametri
        stockController.addStock(new StockBean("Latte", 5, 2));

        Exception exception = assertThrows(Exception.class, () -> {
            stockController.modifyQuantity("Latte", -10);
        });

        assertEquals("Non puoi avere quantità negativa!", exception.getMessage());
    }

    @Test
    void testGetShoppingList() throws Exception {
        // Setup:
        // 1. Acqua: Qta 2, Soglia 5 -> SOTTOSCORTA
        stockController.addStock(new StockBean("Acqua", 2, 5, "Bevande"));

        // 2. Pane: Qta 10, Soglia 2 -> OK
        stockController.addStock(new StockBean("Pane", 10, 2, "Cibo"));

        List<StockBean> shoppingList = stockController.getShoppingList();

        assertEquals(1, shoppingList.size());
        StockBean item = shoppingList.get(0);
        assertEquals("Acqua", item.getNome());
        assertEquals("Bevande", item.getCategory(), "La lista della spesa deve mantenere le categorie.");
    }
}