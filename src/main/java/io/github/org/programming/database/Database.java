package io.github.org.programming.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource dataSource;

    static {
        config.setJdbcUrl(DatabaseConfig.getJDBCUrl());
        config.setUsername(DatabaseConfig.getUserName());
        config.setPassword(DatabaseConfig.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);
        // check if the database programming_bot exists

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE DATABASE IF NOT EXISTS programming_bot");
            }
        } catch (SQLException e) {
            logger.error("Failed to create database programming_bot", e);
        }

        // use the database programming_bot
        config.setJdbcUrl(DatabaseConfig.getJDBCUrl() + "programming_bot");

        executeTableUpdate();
    }

    private static void executeTableUpdate() {
        try (final Statement statement =
                Objects.requireNonNull(getConnection(), "Connection is null").createStatement()) {
            File folder = new File("src/main/resources/sql");
            File[] listOfFiles = folder.listFiles();

            logger.info("Executing table updates...");

            for (File file : Objects.requireNonNull(listOfFiles, "List of files is null")) {
                if (file.isFile()) {
                    logger.info("Executing table update: {}", file.getName());
                    statement.executeUpdate(Files.readString(Path.of(file.getPath())));
                }
            }
        } catch (SQLException | IOException e) {
            logger.error("Error executing table update", e);
        }
    }

    public static void disconnect() {
        dataSource.close();
    }

    public static boolean isConnected() {
        return (dataSource != null);
    }


    /**
     * @return The connection to the database
     */
    public static @Nullable Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Error while getting connection", e);
            return null;
        }
    }
}