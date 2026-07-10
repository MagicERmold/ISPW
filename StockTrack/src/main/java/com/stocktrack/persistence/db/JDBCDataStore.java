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
    private static final String EMAIL_COLUMN = "email";
    private static final String PASSWORD_HASH_COLUMN = "password_hash";
    private static final String USER_COLUMNS = "id, nome, cognome, " + EMAIL_COLUMN + ", " + PASSWORD_HASH_COLUMN;
    private static final String SUPPLIER_COLUMNS = "id, nome, " + EMAIL_COLUMN
            + ", api_endpoint, disponibile, " + PASSWORD_HASH_COLUMN;
    private static final String PRODUCT_COLUMNS = "id, nome, categoria, quantita, soglia_minima, prezzo_unitario";
    private static final String ORDER_COLUMNS = "id, totale, stato";
    private static final String MOVEMENT_COLUMNS = "id, id_prodotto, nome_prodotto, tipo, quantita, "
            + "valore_unitario, data_movimento, origine";
    private static final String DUPLICATE_COLUMN_SQL_STATE = "42S21";
    private static final String SELECT = "select ";
    private static final String WHERE_ID = " where id = ?";
    private static final String PRODUCT_MERGE_SQL = "merge into prodotti key(id) values (?, ?, ?, ?, ?, ?)";
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
        return queryTitolari(SELECT_TITOLARI + " where lower(" + EMAIL_COLUMN + ") = lower(?)", email)
                .stream()
                .findFirst();
    }

    static List<Titolare> loadTitolari() throws PersistenceException {
        return queryTitolari(SELECT_TITOLARI, null);
    }

    static void saveTitolare(Titolare titolare) throws PersistenceException {
        executeMerge("merge into titolari key(id) values (?, ?, ?, ?, ?)", titolare.getId(), titolare.getNome(),
                titolare.getCognome(), titolare.getEmail(), titolare.getPasswordHash());
    }

    static Optional<Commesso> findCommessoById(String id) throws PersistenceException {
        return queryCommessi(SELECT_COMMESSI + WHERE_ID, id).stream().findFirst();
    }

    static Optional<Commesso> findCommessoByEmail(String email) throws PersistenceException {
        return queryCommessi(SELECT_COMMESSI + " where lower(" + EMAIL_COLUMN + ") = lower(?)", email)
                .stream()
                .findFirst();
    }

    static List<Commesso> loadCommessi() throws PersistenceException {
        return queryCommessi(SELECT_COMMESSI, null);
    }

    static void saveCommesso(Commesso commesso) throws PersistenceException {
        executeMerge("merge into commessi key(id) values (?, ?, ?, ?, ?)", commesso.getId(), commesso.getNome(),
                commesso.getCognome(), commesso.getEmail(), commesso.getPasswordHash());
    }

    static List<Fornitore> loadFornitori() throws PersistenceException {
        return queryFornitori(SELECT_FORNITORI, null);
    }

    static Optional<Fornitore> findFornitoreById(String id) throws PersistenceException {
        return queryFornitori(SELECT_FORNITORI + WHERE_ID, id).stream().findFirst();
    }

    static Optional<Fornitore> findFornitoreByEmail(String email) throws PersistenceException {
        return queryFornitori(SELECT_FORNITORI + " where lower(" + EMAIL_COLUMN + ") = lower(?)", email)
                .stream()
                .findFirst();
    }

    static void saveFornitore(Fornitore fornitore) throws PersistenceException {
        executeMerge("merge into fornitori key(id) values (?, ?, ?, ?, ?, ?)", fornitore.getId(), fornitore.getNome(),
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
        executeMerge("merge into ordini key(id) values (?, ?, ?)", ordine.getId(), ordine.getTotale(),
                ordine.getStato());
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
        executeMerge("merge into movimenti_inventario key(id) values (?, ?, ?, ?, ?, ?, ?, ?)",
                movimento.getId(), movimento.getIdProdotto(), movimento.getNomeProdotto(),
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
                    titolari.add(new Titolare(resultSet.getString("id"), resultSet.getString("nome"),
                            resultSet.getString("cognome"), resultSet.getString(EMAIL_COLUMN),
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
                    commessi.add(new Commesso(resultSet.getString("id"), resultSet.getString("nome"),
                            resultSet.getString("cognome"), resultSet.getString(EMAIL_COLUMN),
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
                    fornitori.add(new Fornitore(resultSet.getString("id"), resultSet.getString("nome"),
                            resultSet.getString(EMAIL_COLUMN), resultSet.getString("api_endpoint"),
                            resultSet.getBoolean("disponibile"), resultSet.getString(PASSWORD_HASH_COLUMN)));
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
                    prodotti.add(new Prodotto(resultSet.getString("id"), resultSet.getString("nome"),
                            resultSet.getString("categoria"), resultSet.getInt("quantita"),
                            resultSet.getInt("soglia_minima"), resultSet.getBigDecimal("prezzo_unitario")));
                }
                return prodotti;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore lettura prodotti da database", e);
        }
    }

    private static Ordine toOrdine(ResultSet resultSet) throws SQLException {
        Ordine ordine = new Ordine();
        ordine.setId(resultSet.getString("id"));
        ordine.setTotale(resultSet.getBigDecimal("totale"));
        ordine.setStato(resultSet.getString("stato"));
        return ordine;
    }

    private static MovimentoInventario toMovimentoInventario(ResultSet resultSet) throws SQLException {
        MovimentoInventario movimento = new MovimentoInventario();
        movimento.setId(resultSet.getString("id"));
        movimento.setIdProdotto(resultSet.getString("id_prodotto"));
        movimento.setNomeProdotto(resultSet.getString("nome_prodotto"));
        movimento.setTipo(TipoMovimentoInventario.valueOf(resultSet.getString("tipo")));
        movimento.setQuantita(resultSet.getInt("quantita"));
        movimento.setValoreUnitario(resultSet.getBigDecimal("valore_unitario"));
        movimento.setDataMovimento(resultSet.getObject("data_movimento", LocalDateTime.class));
        movimento.setOrigine(resultSet.getString("origine"));
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
                    + " varchar, " + PASSWORD_HASH_COLUMN + " varchar)");
            statement.execute("create table if not exists commessi "
                    + "(id varchar primary key, nome varchar, cognome varchar, " + EMAIL_COLUMN
                    + " varchar, " + PASSWORD_HASH_COLUMN + " varchar)");
            addTitolarePasswordHashColumnIfMissing(statement);
            addCommessoPasswordHashColumnIfMissing(statement);
            statement.execute("create table if not exists fornitori "
                    + "(id varchar primary key, nome varchar, " + EMAIL_COLUMN
                    + " varchar, api_endpoint varchar, disponibile boolean, " + PASSWORD_HASH_COLUMN + " varchar)");
            addFornitorePasswordHashColumnIfMissing(statement);
            statement.execute("create table if not exists prodotti "
                    + "(id varchar primary key, nome varchar, categoria varchar, quantita int, "
                    + "soglia_minima int, prezzo_unitario decimal)");
            statement.execute("create table if not exists ordini "
                    + "(id varchar primary key, totale decimal, stato varchar)");
            statement.execute("create table if not exists movimenti_inventario "
                    + "(id varchar primary key, id_prodotto varchar, nome_prodotto varchar, tipo varchar, "
                    + "quantita int, valore_unitario decimal, data_movimento timestamp, origine varchar)");
        }
        seedDemoData(connection);
    }

    private static void addTitolarePasswordHashColumnIfMissing(Statement statement) throws SQLException {
        try {
            statement.execute("alter table titolari add column " + PASSWORD_HASH_COLUMN + " varchar");
        } catch (SQLException e) {
            if (!DUPLICATE_COLUMN_SQL_STATE.equals(e.getSQLState())) {
                throw e;
            }
        }
    }

    private static void addCommessoPasswordHashColumnIfMissing(Statement statement) throws SQLException {
        try {
            statement.execute("alter table commessi add column " + PASSWORD_HASH_COLUMN + " varchar");
        } catch (SQLException e) {
            if (!DUPLICATE_COLUMN_SQL_STATE.equals(e.getSQLState())) {
                throw e;
            }
        }
    }

    private static void addFornitorePasswordHashColumnIfMissing(Statement statement) throws SQLException {
        try {
            statement.execute("alter table fornitori add column " + PASSWORD_HASH_COLUMN + " varchar");
        } catch (SQLException e) {
            if (!DUPLICATE_COLUMN_SQL_STATE.equals(e.getSQLState())) {
                throw e;
            }
        }
    }

    private static void seedDemoData(Connection connection) throws SQLException {
        executeMerge(connection, "merge into titolari key(id) values (?, ?, ?, ?, ?)", "TIT-1", "Andrea",
                "Titolare", "titolare@stocktrack.local", PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        executeMerge(connection, "merge into commessi key(id) values (?, ?, ?, ?, ?)", "COM-1", "Mario",
                "Commesso", "commesso@stocktrack.local", PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        executeMerge(connection, "merge into fornitori key(id) values (?, ?, ?, ?, ?, ?)", "APPLE-2026", "APPLE",
                "business@apple.example", "simulated://fornitori/apple", true,
                PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        executeMerge(connection, "merge into fornitori key(id) values (?, ?, ?, ?, ?, ?)", "SAMSUNG-2026", "SAMSUNG",
                "business@samsung.example", "simulated://fornitori/samsung", true,
                PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        executeMerge(connection, "merge into fornitori key(id) values (?, ?, ?, ?, ?, ?)", "HUAWEI-2026", "HUAWEI",
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
