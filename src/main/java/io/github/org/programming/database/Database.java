package io.github.org.programming.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;
import org.mariadb.jdbc.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    private static Connection connection;

    static {
        config.setJdbcUrl(DatabaseConfig.getJDBCUrl());
        config.setUsername(DatabaseConfig.getUserName());
        config.setPassword(DatabaseConfig.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    public static void openDatabase() throws SQLException {
        logger.info("Connecting to database...");
        connection = DriverManager.getConnection(config.getJdbcUrl(), config.getUsername(),
                config.getPassword());

        if (connection.isValid(5)) {
            logger.info("Connected to database.");
        } else {
            logger.error("Failed to connect to database.");
        }
    }

    public static void closeDatabase() throws SQLException {
        logger.info("Closing database connection...");
        connection.close();
        if (connection.isClosed()) {
            logger.info("Database connection closed.");
        } else {
            logger.error("Failed to close database connection.");
        }
    }
}
