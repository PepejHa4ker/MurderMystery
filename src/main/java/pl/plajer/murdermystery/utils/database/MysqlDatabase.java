package pl.plajer.murdermystery.utils.database;

import com.zaxxer.hikari.HikariDataSource;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.logging.ILogger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlDatabase {

    private HikariDataSource hikariDataSource;
    private final ILogger logger = MurderMystery.getInstance().getPluginLogger();

    public MysqlDatabase(String user, String password, String jdbcUrl) {
        logger.info("Configuring MySQL connection!");
        configureConnPool(user, password, jdbcUrl);

        try (Connection connection = getConnection()) {
            if (connection == null) {
                logger.info("Failed to connect to database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MysqlDatabase(String user, String password, String host, String database, int port) {
        logger.info("Configuring MySQL connection!");
        configureConnPool(user, password, host, database, port);

        try (Connection connection = getConnection()) {
            if (connection == null) {
                logger.severe("Failed to connect to database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configureConnPool(String user, String password, String jdbcUrl) {
        try {
            logger.info("Creating HikariCP Configuration...");
            HikariDataSource config = new HikariDataSource();
            config.setJdbcUrl(jdbcUrl);
            config.addDataSourceProperty("user", user);
            config.addDataSourceProperty("password", password);
            hikariDataSource = config;
            logger.info("Setting up MySQL Connection pool...");
            logger.info("Connection pool successfully configured. ");
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Cannot connect to MySQL database!");
            logger.warn("Check configuration of your database settings!");
        }
    }

    private void configureConnPool(String user, String password, String host, String database, int port) {
        try {
            logger.info("Creating HikariCP Configuration...");
            HikariDataSource config = new HikariDataSource();
            config.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
            config.addDataSourceProperty("serverName", host);
            config.addDataSourceProperty("portNumber", port);
            config.addDataSourceProperty("databaseName", database);
            config.addDataSourceProperty("user", user);
            config.addDataSourceProperty("password", password);
            hikariDataSource = config;
            logger.info("Setting up MySQL Connection pool...");
            logger.info("Connection pool successfully configured. ");
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Cannot connect to MySQL database!");
            logger.warn("Check configuration of your database settings!");
        }
    }

    public void executeUpdate(String query) {
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(query);
            }
        } catch (SQLException e) {
            logger.warn("Failed to execute update: " + query);
        }
    }


    public ResultSet executeQuery(String query) {
        throw new UnsupportedOperationException("Queries should be created with own auto-closeable connection!");
    }

    public void shutdownConnPool() {
        try {
            logger.info("Shutting down connection pool. Trying to close all connections.");
            if (!hikariDataSource.isClosed()) {
                hikariDataSource.close();
                logger.info("Pool successfully shutdown. ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = hikariDataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

}
