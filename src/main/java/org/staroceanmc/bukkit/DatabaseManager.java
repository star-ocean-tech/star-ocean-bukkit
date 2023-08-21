package org.staroceanmc.bukkit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Logger;

public class DatabaseManager {
    private ExecutorService watchdogThreadPool = Executors.newFixedThreadPool(2);
    private Database database;
    private HikariConfig prevConfig;
    private HikariDataSource dataSource;
    private AbstractPlugin plugin;
    private Logger logger;

    private boolean willReconnect = true;
    private boolean isConnected = false;

    public DatabaseManager(AbstractPlugin plugin) {
        this.plugin = plugin;
        this.logger = Logger.getLogger(plugin.getName() + "-DatabaseManager");
    }

    public void connect(HikariConfig config) throws SQLException {
        logger.info("Connecting to database");
        if (dataSource != null && !dataSource.isClosed())
            dataSource.close();
        prevConfig = config;
        dataSource = new HikariDataSource(config);
        Connection connection = dataSource.getConnection();
        database = new Database(connection);
        isConnected = true;
        willReconnect = true;
        WatchdogThread watchdog = new WatchdogThread(plugin, connection);
        watchdog.onExit((o) -> {
            onWatchdogExit();
            return null;
        });
        watchdogThreadPool.execute(watchdog);
        DatabaseConnectedEvent event = new DatabaseConnectedEvent(database);
        plugin.getServer().getPluginManager().callEvent(event);
    }

    public void reconnect() throws SQLException {
        logger.info("Reconnecting to database");
        connect(prevConfig);
    }

    public void disconnect() throws SQLException {
        isConnected = false;
        willReconnect = false;
        if (database != null && database.connection != null && !database.connection.isClosed())
            this.database.connection.close();
        if (dataSource != null && dataSource.isClosed())
            this.dataSource.close();
        this.watchdogThreadPool.shutdown();
    }

    public Database getDatabase() {
        return database;
    }

    public void onWatchdogExit() {
        try {
            if (willReconnect)
                reconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class Database {
        private Connection connection;

        public Database(Connection connection) {
            this.connection = connection;
        }

        public Connection getConnection() {
            return connection;
        }
    }

    private static class WatchdogThread implements Runnable {
        private Logger logger;

        private Connection connection;
        private PreparedStatement checkStatement;
        private Function<Object, Object> onExit = (o) -> null;

        public WatchdogThread(AbstractPlugin plugin, Connection connection) {
            this.connection = connection;
            this.logger = Logger.getLogger(plugin.getName() + "-DatabaseWatchdog");
            logger.info("Watchdog is watching connection");
            try {
                checkStatement = connection.prepareStatement("SELECT CURRENT_USER;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (!connection.isClosed()) {
                    checkStatement.execute();
                    Thread.sleep(30000);
                }
                exit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void exit() {
            logger.info("Watchdog is exiting");
            onExit.apply(this);
        }

        public void onExit(Function<Object, Object> func) {
            this.onExit = func;
        }
    }
}
