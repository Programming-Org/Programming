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

        if (true)
            throw new RuntimeException("Simulating a database error");
        logger.info("The connection to the database was '{}'", connection.isValid(5));
    }

    public static void closeDatabase() throws SQLException {
        logger.info("Closing database connection...");
        connection.close();
        logger.info("The disconnection to the database was '{}'", connection.isClosed());
    }
}
