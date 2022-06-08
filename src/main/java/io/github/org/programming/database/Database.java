package io.github.org.programming.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;
import org.mariadb.jdbc.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    protected static Connection connection;

    static {
        config.setJdbcUrl(DatabaseConfig.getJDBCUrl());
        config.setUsername(DatabaseConfig.getUserName());
        config.setPassword(DatabaseConfig.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    /**
     * Creates a new data for a programming_bot table
     * 
     * @param name the name of the data
     * @param rating the rating of the data
     * @throws SQLException if the data could not be created This is an example of inserting data.
     */
    public static void createData(String name, int rating) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                    INSERT INTO programming_bot (name, rating)
                    VALUES (?, ?)
                """)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, rating);
            preparedStatement.executeUpdate();
        }

    }

    public static void openDatabase() throws SQLException {
        logger.info("Connecting to database...");
        connection = DriverManager.getConnection(config.getJdbcUrl(), config.getUsername(),
                config.getPassword());

        logger.info("The connection to the database was '{}'", connection.isValid(5));
    }

    public static void closeDatabase() throws SQLException {
        logger.info("Closing database connection...");
        connection.close();
        logger.info("The disconnection to the database was '{}'", connection.isClosed());
    }
}
