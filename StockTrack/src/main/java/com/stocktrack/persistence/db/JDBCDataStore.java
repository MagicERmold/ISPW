package com.stocktrack.persistence.db;

import com.stocktrack.entity.Commesso;
import com.stocktrack.entity.Fornitore;
import com.stocktrack.entity.Inventario;
import com.stocktrack.entity.Ordine;
import com.stocktrack.entity.Prodotto;
import com.stocktrack.entity.Titolare;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.security.PasswordHasher;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class JDBCDataStore {

    private static final String URL = "jdbc:h2:file:./StockTrack/data/stocktrack-db";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private JDBCDataStore() {
    }

    static Optional<Titolare> findTitolareById(String id) throws PersistenceException {
        return queryTitolari("select * from titolari where id = ?", id).stream().findFirst();
    }

    static Optional<Titolare> findTitolareByEmail(String email) throws PersistenceException {
        return queryTitolari("select * from titolari where lower(email) = lower(?)", email).stream().findFirst();
    }

    static List<Titolare> loadTitolari() throws PersistenceException {
        return queryTitolari("select * from titolari", null);
    }

    static void saveTitolare(Titolare titolare) throws PersistenceException {
        executeMerge("merge into titolari key(id) values (?, ?, ?, ?, ?)", titolare.getId(), titolare.getNome(),
                titolare.getCognome(), titolare.getEmail(), titolare.getPasswordHash());
    }

    static Optional<Commesso> findCommessoById(String id) throws PersistenceException {
        return queryCommessi("select * from commessi where id = ?", id).stream().findFirst();
    }

    static Optional<Commesso> findCommessoByEmail(String email) throws PersistenceException {
        return queryCommessi("select * from commessi where lower(email) = lower(?)", email).stream().findFirst();
    }

    static List<Commesso> loadCommessi() throws PersistenceException {
        return queryCommessi("select * from commessi", null);
    }

    static void saveCommesso(Commesso commesso) throws PersistenceException {
        executeMerge("merge into commessi key(id) values (?, ?, ?, ?, ?)", commesso.getId(), commesso.getNome(),
                commesso.getCognome(), commesso.getEmail(), commesso.getPasswordHash());
    }

    static List<Fornitore> loadFornitori() throws PersistenceException {
        return queryFornitori("select * from fornitori", null);
    }

    static Optional<Fornitore> findFornitoreById(String id) throws PersistenceException {
        return queryFornitori("select * from fornitori where id = ?", id).stream().findFirst();
    }

    static void saveFornitore(Fornitore fornitore) throws PersistenceException {
        executeMerge("merge into fornitori key(id) values (?, ?, ?, ?, ?)", fornitore.getId(), fornitore.getNome(),
                fornitore.getEmail(), fornitore.getApiEndpoint(), fornitore.isDisponibile());
    }

    static List<Prodotto> loadProdotti() throws PersistenceException {
        return queryProdotti("select * from prodotti", null);
    }

    static Optional<Prodotto> findProdottoById(String id) throws PersistenceException {
        return queryProdotti("select * from prodotti where id = ?", id).stream().findFirst();
    }

    static void saveProdotto(Prodotto prodotto) throws PersistenceException {
        executeMerge("merge into prodotti key(id) values (?, ?, ?, ?, ?, ?)", prodotto.getId(), prodotto.getNome(),
                prodotto.getCategoria(), prodotto.getQuantita(), prodotto.getSogliaMinima(),
                prodotto.getPrezzoUnitario());
    }

    static void deleteProdotto(String id) throws PersistenceException {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement("delete from prodotti where id = ?")) {
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
             ResultSet resultSet = statement.executeQuery("select * from ordini")) {
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
             PreparedStatement statement = connection.prepareStatement("select * from ordini where id = ?")) {
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

    private static List<Titolare> queryTitolari(String sql, String parameter) throws PersistenceException {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindOptionalString(statement, parameter);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Titolare> titolari = new ArrayList<>();
                while (resultSet.next()) {
                    titolari.add(new Titolare(resultSet.getString("id"), resultSet.getString("nome"),
                            resultSet.getString("cognome"), resultSet.getString("email"),
                            resultSet.getString("password_hash")));
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
                            resultSet.getString("cognome"), resultSet.getString("email"),
                            resultSet.getString("password_hash")));
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
                            resultSet.getString("email"), resultSet.getString("api_endpoint"),
                            resultSet.getBoolean("disponibile")));
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

    private static Connection openConnection() throws SQLException, PersistenceException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        initialize(connection);
        return connection;
    }

    private static void initialize(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("create table if not exists titolari "
                    + "(id varchar primary key, nome varchar, cognome varchar, email varchar, password_hash varchar)");
            statement.execute("create table if not exists commessi "
                    + "(id varchar primary key, nome varchar, cognome varchar, email varchar, password_hash varchar)");
            addColumnIfMissing(statement, "titolari", "password_hash");
            addColumnIfMissing(statement, "commessi", "password_hash");
            statement.execute("create table if not exists fornitori "
                    + "(id varchar primary key, nome varchar, email varchar, api_endpoint varchar, disponibile boolean)");
            statement.execute("create table if not exists prodotti "
                    + "(id varchar primary key, nome varchar, categoria varchar, quantita int, "
                    + "soglia_minima int, prezzo_unitario decimal)");
            statement.execute("create table if not exists ordini "
                    + "(id varchar primary key, totale decimal, stato varchar)");
            statement.execute("merge into titolari key(id) values "
                    + "('TIT-1', 'Andrea', 'Titolare', 'titolare@stocktrack.local', '"
                    + PasswordHasher.hash("password123") + "')");
            statement.execute("merge into commessi key(id) values "
                    + "('COM-1', 'Mario', 'Commesso', 'commesso@stocktrack.local', '"
                    + PasswordHasher.hash("password123") + "')");
            statement.execute("merge into fornitori key(id) values "
                    + "('FOR-1', 'Forniture Demo', 'fornitore@demo.local', 'simulated://fornitori/demo', true)");
            statement.execute("merge into prodotti key(id) values "
                    + "('PROD-1', 'Caffe', 'Alimentari', 8, 10, 3.50)");
            statement.execute("merge into prodotti key(id) values "
                    + "('PROD-2', 'Latte', 'Alimentari', 20, 5, 1.40)");
        }
    }

    private static void addColumnIfMissing(Statement statement, String table, String column) throws SQLException {
        try {
            statement.execute("alter table " + table + " add column " + column + " varchar");
        } catch (SQLException e) {
            if (!"42S21".equals(e.getSQLState())) {
                throw e;
            }
        }
    }
}
