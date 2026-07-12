package com.stocktrack.persistence.db;

import com.stocktrack.entity.Commesso;
import com.stocktrack.entity.Fornitore;
import com.stocktrack.entity.Inventario;
import com.stocktrack.entity.MovimentoInventario;
import com.stocktrack.entity.Ordine;
import com.stocktrack.entity.Prodotto;
import com.stocktrack.entity.Titolare;
import com.stocktrack.entity.TipoMovimentoInventario;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.security.PasswordHasher;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Componente interno di persistenza che centralizza strutture e operazioni sulla database JDBC della modalità FULL. È usato dai DAO dello stesso package e resta nascosto a boundary, view e controller applicativi.
 */
final class JDBCDataStore {

    private static final String URL = "jdbc:h2:file:./StockTrack/data/stocktrack-db-secure;CIPHER=AES";
    private static final String USER = "stocktrack";
    private static final String DB_FILE_PASSWORD_PROPERTY = "stocktrack.db.file.password";
    private static final String DB_USER_PASSWORD_PROPERTY = "stocktrack.db.user.password";
    private static final String DB_FILE_PASSWORD_ENV = "STOCKTRACK_DB_FILE_PASSWORD";
    private static final String DB_USER_PASSWORD_ENV = "STOCKTRACK_DB_USER_PASSWORD";
    private static final String PASSWORD = configuredSecret(DB_FILE_PASSWORD_PROPERTY, DB_FILE_PASSWORD_ENV)
            + " " + configuredSecret(DB_USER_PASSWORD_PROPERTY, DB_USER_PASSWORD_ENV);
    private static final String DEFAULT_LOGIN_PASSWORD = "password123";
    private static final String ID_COLUMN = "id";
    private static final String NAME_COLUMN = "nome";
    private static final String SURNAME_COLUMN = "cognome";
    private static final String EMAIL_COLUMN = "email";
    private static final String PASSWORD_HASH_COLUMN = "password_hash";
    private static final String API_ENDPOINT_COLUMN = "api_endpoint";
    private static final String AVAILABLE_COLUMN = "disponibile";
    private static final String CATEGORY_COLUMN = "categoria";
    private static final String QUANTITY_COLUMN = "quantita";
    private static final String THRESHOLD_COLUMN = "soglia_minima";
    private static final String UNIT_PRICE_COLUMN = "prezzo_unitario";
    private static final String TOTAL_COLUMN = "totale";
    private static final String STATUS_COLUMN = "stato";
    private static final String MOVEMENT_PRODUCT_ID_COLUMN = "id_prodotto";
    private static final String MOVEMENT_PRODUCT_NAME_COLUMN = "nome_prodotto";
    private static final String MOVEMENT_TYPE_COLUMN = "tipo";
    private static final String MOVEMENT_VALUE_COLUMN = "valore_unitario";
    private static final String MOVEMENT_DATE_COLUMN = "data_movimento";
    private static final String MOVEMENT_ORIGIN_COLUMN = "origine";
    private static final String VARCHAR_TYPE = " varchar";
    private static final String VARCHAR_CLOSE = " varchar)";
    private static final String USER_COLUMNS = ID_COLUMN + ", " + NAME_COLUMN + ", " + SURNAME_COLUMN + ", "
            + EMAIL_COLUMN + ", " + PASSWORD_HASH_COLUMN;
    private static final String SUPPLIER_COLUMNS = ID_COLUMN + ", " + NAME_COLUMN + ", " + EMAIL_COLUMN
            + ", " + API_ENDPOINT_COLUMN + ", " + AVAILABLE_COLUMN + ", " + PASSWORD_HASH_COLUMN;
    private static final String PRODUCT_COLUMNS = ID_COLUMN + ", " + NAME_COLUMN + ", " + CATEGORY_COLUMN + ", "
            + QUANTITY_COLUMN + ", " + THRESHOLD_COLUMN + ", " + UNIT_PRICE_COLUMN;
    private static final String ORDER_COLUMNS = ID_COLUMN + ", " + TOTAL_COLUMN + ", " + STATUS_COLUMN;
    private static final String MOVEMENT_COLUMNS = ID_COLUMN + ", " + MOVEMENT_PRODUCT_ID_COLUMN + ", "
            + MOVEMENT_PRODUCT_NAME_COLUMN + ", " + MOVEMENT_TYPE_COLUMN + ", " + QUANTITY_COLUMN + ", "
            + MOVEMENT_VALUE_COLUMN + ", " + MOVEMENT_DATE_COLUMN + ", " + MOVEMENT_ORIGIN_COLUMN;
    private static final String DUPLICATE_COLUMN_SQL_STATE = "42S21";
    private static final String SELECT = "select ";
    private static final String WHERE_ID = " where id = ?";
    private static final String WHERE_LOWER_EMAIL = " where lower(" + EMAIL_COLUMN + ") = lower(?)";
    private static final String TITOLARE_MERGE_SQL = "merge into titolari key(id) values (?, ?, ?, ?, ?)";
    private static final String COMMESSO_MERGE_SQL = "merge into commessi key(id) values (?, ?, ?, ?, ?)";
    private static final String SUPPLIER_MERGE_SQL = "merge into fornitori key(id) values (?, ?, ?, ?, ?, ?)";
    private static final String PRODUCT_MERGE_SQL = "merge into prodotti key(id) values (?, ?, ?, ?, ?, ?)";
    private static final String ORDER_MERGE_SQL = "merge into ordini key(id) values (?, ?, ?)";
    private static final String MOVEMENT_MERGE_SQL =
            "merge into movimenti_inventario key(id) values (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_PRODUCT_SQL = "delete from prodotti" + WHERE_ID;
    private static final String DELETE_SUPPLIER_SQL = "delete from fornitori" + WHERE_ID;
    private static final String SELECT_TITOLARI = SELECT + USER_COLUMNS + " from titolari";
    private static final String SELECT_COMMESSI = SELECT + USER_COLUMNS + " from commessi";
    private static final String SELECT_FORNITORI = SELECT + SUPPLIER_COLUMNS + " from fornitori";
    private static final String SELECT_PRODOTTI = SELECT + PRODUCT_COLUMNS + " from prodotti";
    private static final String SELECT_ORDINI = SELECT + ORDER_COLUMNS + " from ordini";
    private static final String SELECT_MOVIMENTI_INVENTARIO = SELECT + MOVEMENT_COLUMNS + " from movimenti_inventario";

    private JDBCDataStore() {
    }

    static Optional<Titolare> findTitolareById(String id) throws PersistenceException {
        return queryTitolari(SELECT_TITOLARI + WHERE_ID, id).stream().findFirst();
    }

    static Optional<Titolare> findTitolareByEmail(String email) throws PersistenceException {
        return queryTitolari(SELECT_TITOLARI + WHERE_LOWER_EMAIL, email)
                .stream()
                .findFirst();
    }

    static List<Titolare> loadTitolari() throws PersistenceException {
        return queryTitolari(SELECT_TITOLARI, null);
    }

    static void saveTitolare(Titolare titolare) throws PersistenceException {
        executeMerge(TITOLARE_MERGE_SQL, titolare.getId(), titolare.getNome(),
                titolare.getCognome(), titolare.getEmail(), titolare.getPasswordHash());
    }

    static Optional<Commesso> findCommessoById(String id) throws PersistenceException {
        return queryCommessi(SELECT_COMMESSI + WHERE_ID, id).stream().findFirst();
    }

    static Optional<Commesso> findCommessoByEmail(String email) throws PersistenceException {
        return queryCommessi(SELECT_COMMESSI + WHERE_LOWER_EMAIL, email)
                .stream()
                .findFirst();
    }

    static List<Commesso> loadCommessi() throws PersistenceException {
        return queryCommessi(SELECT_COMMESSI, null);
    }

    static void saveCommesso(Commesso commesso) throws PersistenceException {
        executeMerge(COMMESSO_MERGE_SQL, commesso.getId(), commesso.getNome(),
                commesso.getCognome(), commesso.getEmail(), commesso.getPasswordHash());
    }

    static List<Fornitore> loadFornitori() throws PersistenceException {
        return queryFornitori(SELECT_FORNITORI, null);
    }

    static Optional<Fornitore> findFornitoreById(String id) throws PersistenceException {
        return queryFornitori(SELECT_FORNITORI + WHERE_ID, id).stream().findFirst();
    }

    static Optional<Fornitore> findFornitoreByEmail(String email) throws PersistenceException {
        return queryFornitori(SELECT_FORNITORI + WHERE_LOWER_EMAIL, email)
                .stream()
                .findFirst();
    }

    static void saveFornitore(Fornitore fornitore) throws PersistenceException {
        executeMerge(SUPPLIER_MERGE_SQL, fornitore.getId(), fornitore.getNome(),
                fornitore.getEmail(), fornitore.getApiEndpoint(), fornitore.isDisponibile(),
                fornitore.getPasswordHash());
    }

    static void deleteFornitore(String id) throws PersistenceException {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SUPPLIER_SQL)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore eliminazione fornitore su database", e);
        }
    }

    static List<Prodotto> loadProdotti() throws PersistenceException {
        return queryProdotti(SELECT_PRODOTTI, null);
    }

    static Optional<Prodotto> findProdottoById(String id) throws PersistenceException {
        return queryProdotti(SELECT_PRODOTTI + WHERE_ID, id).stream().findFirst();
    }

    static void saveProdotto(Prodotto prodotto) throws PersistenceException {
        executeMerge(PRODUCT_MERGE_SQL, prodotto.getId(), prodotto.getNome(),
                prodotto.getCategoria(), prodotto.getQuantita(), prodotto.getSogliaMinima(),
                prodotto.getPrezzoUnitario());
    }

    static void deleteProdotto(String id) throws PersistenceException {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_PRODUCT_SQL)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore eliminazione prodotto su database", e);
        }
    }

    static Inventario loadInventario() throws PersistenceException {
        return new Inventario("INV-DB", loadProdotti());
    }

    static void saveInventario(Inventario inventario) throws PersistenceException {
        for (Prodotto prodotto : inventario.getProdotti()) {
            saveProdotto(prodotto);
        }
    }

    static List<Ordine> loadOrdini() throws PersistenceException {
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ORDINI)) {
            List<Ordine> ordini = new ArrayList<>();
            while (resultSet.next()) {
                ordini.add(toOrdine(resultSet));
            }
            return ordini;
        } catch (SQLException e) {
            throw new PersistenceException("Errore lettura ordini da database", e);
        }
    }

    static Optional<Ordine> findOrdineById(String id) throws PersistenceException {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ORDINI + WHERE_ID)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(toOrdine(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore ricerca ordine su database", e);
        }
    }

    static void saveOrdine(Ordine ordine) throws PersistenceException {
        executeMerge(ORDER_MERGE_SQL, ordine.getId(), ordine.getTotale(), ordine.getStato());
    }

    static List<MovimentoInventario> loadMovimentiInventario() throws PersistenceException {
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_MOVIMENTI_INVENTARIO)) {
            List<MovimentoInventario> movimenti = new ArrayList<>();
            while (resultSet.next()) {
                movimenti.add(toMovimentoInventario(resultSet));
            }
            return movimenti;
        } catch (SQLException e) {
            throw new PersistenceException("Errore lettura movimenti inventario da database", e);
        }
    }

    static void saveMovimentoInventario(MovimentoInventario movimento) throws PersistenceException {
        executeMerge(MOVEMENT_MERGE_SQL, movimento.getId(), movimento.getIdProdotto(), movimento.getNomeProdotto(),
                movimento.getTipo().name(), movimento.getQuantita(), movimento.getValoreUnitario(),
                movimento.getDataMovimento(), movimento.getOrigine());
    }

    private static List<Titolare> queryTitolari(String sql, String parameter) throws PersistenceException {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindOptionalString(statement, parameter);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Titolare> titolari = new ArrayList<>();
                while (resultSet.next()) {
                    titolari.add(new Titolare(resultSet.getString(ID_COLUMN), resultSet.getString(NAME_COLUMN),
                            resultSet.getString(SURNAME_COLUMN), resultSet.getString(EMAIL_COLUMN),
                            resultSet.getString(PASSWORD_HASH_COLUMN)));
                }
                return titolari;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore lettura titolari da database", e);
        }
    }

    private static List<Commesso> queryCommessi(String sql, String parameter) throws PersistenceException {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindOptionalString(statement, parameter);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Commesso> commessi = new ArrayList<>();
                while (resultSet.next()) {
                    commessi.add(new Commesso(resultSet.getString(ID_COLUMN), resultSet.getString(NAME_COLUMN),
                            resultSet.getString(SURNAME_COLUMN), resultSet.getString(EMAIL_COLUMN),
                            resultSet.getString(PASSWORD_HASH_COLUMN)));
                }
                return commessi;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore lettura commessi da database", e);
        }
    }

    private static List<Fornitore> queryFornitori(String sql, String parameter) throws PersistenceException {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindOptionalString(statement, parameter);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Fornitore> fornitori = new ArrayList<>();
                while (resultSet.next()) {
                    fornitori.add(new Fornitore(resultSet.getString(ID_COLUMN), resultSet.getString(NAME_COLUMN),
                            resultSet.getString(EMAIL_COLUMN), resultSet.getString(API_ENDPOINT_COLUMN),
                            resultSet.getBoolean(AVAILABLE_COLUMN), resultSet.getString(PASSWORD_HASH_COLUMN)));
                }
                return fornitori;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore lettura fornitori da database", e);
        }
    }

    private static List<Prodotto> queryProdotti(String sql, String parameter) throws PersistenceException {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindOptionalString(statement, parameter);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Prodotto> prodotti = new ArrayList<>();
                while (resultSet.next()) {
                    prodotti.add(new Prodotto(resultSet.getString(ID_COLUMN), resultSet.getString(NAME_COLUMN),
                            resultSet.getString(CATEGORY_COLUMN), resultSet.getInt(QUANTITY_COLUMN),
                            resultSet.getInt(THRESHOLD_COLUMN), resultSet.getBigDecimal(UNIT_PRICE_COLUMN)));
                }
                return prodotti;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore lettura prodotti da database", e);
        }
    }

    private static Ordine toOrdine(ResultSet resultSet) throws SQLException {
        Ordine ordine = new Ordine();
        ordine.setId(resultSet.getString(ID_COLUMN));
        ordine.setTotale(resultSet.getBigDecimal(TOTAL_COLUMN));
        ordine.setStato(resultSet.getString(STATUS_COLUMN));
        return ordine;
    }

    private static MovimentoInventario toMovimentoInventario(ResultSet resultSet) throws SQLException {
        MovimentoInventario movimento = new MovimentoInventario();
        movimento.setId(resultSet.getString(ID_COLUMN));
        movimento.setIdProdotto(resultSet.getString(MOVEMENT_PRODUCT_ID_COLUMN));
        movimento.setNomeProdotto(resultSet.getString(MOVEMENT_PRODUCT_NAME_COLUMN));
        movimento.setTipo(TipoMovimentoInventario.valueOf(resultSet.getString(MOVEMENT_TYPE_COLUMN)));
        movimento.setQuantita(resultSet.getInt(QUANTITY_COLUMN));
        movimento.setValoreUnitario(resultSet.getBigDecimal(MOVEMENT_VALUE_COLUMN));
        movimento.setDataMovimento(resultSet.getObject(MOVEMENT_DATE_COLUMN, LocalDateTime.class));
        movimento.setOrigine(resultSet.getString(MOVEMENT_ORIGIN_COLUMN));
        return movimento;
    }

    private static void executeMerge(String sql, Object... values) throws PersistenceException {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 0; index < values.length; index++) {
                statement.setObject(index + 1, values[index]);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore scrittura su database", e);
        }
    }

    private static void bindOptionalString(PreparedStatement statement, String value) throws SQLException {
        if (value != null) {
            statement.setString(1, value);
        }
    }

    private static Connection openConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        initialize(connection);
        return connection;
    }

    private static void initialize(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("create table if not exists titolari "
                    + "(id varchar primary key, nome varchar, cognome varchar, " + EMAIL_COLUMN
                    + " varchar, " + PASSWORD_HASH_COLUMN + VARCHAR_CLOSE);
            statement.execute("create table if not exists commessi "
                    + "(id varchar primary key, nome varchar, cognome varchar, " + EMAIL_COLUMN
                    + " varchar, " + PASSWORD_HASH_COLUMN + VARCHAR_CLOSE);
            addTitolarePasswordHashColumnIfMissing(statement);
            addCommessoPasswordHashColumnIfMissing(statement);
            statement.execute("create table if not exists fornitori "
                    + "(id varchar primary key, nome varchar, " + EMAIL_COLUMN
                    + " varchar, api_endpoint varchar, disponibile boolean, " + PASSWORD_HASH_COLUMN + VARCHAR_CLOSE);
            addFornitorePasswordHashColumnIfMissing(statement);
            statement.execute("create table if not exists prodotti "
                    + "(id varchar primary key, nome varchar, categoria varchar, quantita int, "
                    + "soglia_minima int, prezzo_unitario decimal)");
            statement.execute("create table if not exists ordini "
                    + "(id varchar primary key, totale decimal, " + STATUS_COLUMN + VARCHAR_CLOSE);
            statement.execute("create table if not exists movimenti_inventario "
                    + "(id varchar primary key, id_prodotto varchar, nome_prodotto varchar, tipo varchar, "
                    + "quantita int, valore_unitario decimal, data_movimento timestamp, "
                    + MOVEMENT_ORIGIN_COLUMN + VARCHAR_CLOSE);
        }
        seedDemoData(connection);
    }

    private static void addTitolarePasswordHashColumnIfMissing(Statement statement) throws SQLException {
        try {
            statement.execute("alter table titolari add column " + PASSWORD_HASH_COLUMN + VARCHAR_TYPE);
        } catch (SQLException e) {
            if (!DUPLICATE_COLUMN_SQL_STATE.equals(e.getSQLState())) {
                throw e;
            }
        }
    }

    private static void addCommessoPasswordHashColumnIfMissing(Statement statement) throws SQLException {
        try {
            statement.execute("alter table commessi add column " + PASSWORD_HASH_COLUMN + VARCHAR_TYPE);
        } catch (SQLException e) {
            if (!DUPLICATE_COLUMN_SQL_STATE.equals(e.getSQLState())) {
                throw e;
            }
        }
    }

    private static void addFornitorePasswordHashColumnIfMissing(Statement statement) throws SQLException {
        try {
            statement.execute("alter table fornitori add column " + PASSWORD_HASH_COLUMN + VARCHAR_TYPE);
        } catch (SQLException e) {
            if (!DUPLICATE_COLUMN_SQL_STATE.equals(e.getSQLState())) {
                throw e;
            }
        }
    }

    private static void seedDemoData(Connection connection) throws SQLException {
        executeMerge(connection, TITOLARE_MERGE_SQL, "TIT-1", "Andrea",
                "Titolare", "titolare@stocktrack.local", PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        executeMerge(connection, COMMESSO_MERGE_SQL, "COM-1", "Mario",
                "Commesso", "commesso@stocktrack.local", PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        executeMerge(connection, SUPPLIER_MERGE_SQL, "APPLE-2026", "APPLE",
                "business@apple.example", "simulated://fornitori/apple", true,
                PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        executeMerge(connection, SUPPLIER_MERGE_SQL, "SAMSUNG-2026", "SAMSUNG",
                "business@samsung.example", "simulated://fornitori/samsung", true,
                PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        executeMerge(connection, SUPPLIER_MERGE_SQL, "HUAWEI-2026", "HUAWEI",
                "business@huawei.example", "simulated://fornitori/huawei", true,
                PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        executeMerge(connection, PRODUCT_MERGE_SQL, "PROD-1", "Caffe",
                "Alimentari", 8, 10, new BigDecimal("3.50"));
        executeMerge(connection, PRODUCT_MERGE_SQL, "PROD-2", "Latte",
                "Alimentari", 20, 5, new BigDecimal("1.40"));
    }

    private static void executeMerge(Connection connection, String sql, Object... values) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 0; index < values.length; index++) {
                statement.setObject(index + 1, values[index]);
            }
            statement.executeUpdate();
        }
    }

    private static String configuredSecret(String propertyName, String environmentName) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }
        throw new IllegalStateException("Credenziali database mancanti: configurare " + propertyName
                + " o " + environmentName);
    }
}
