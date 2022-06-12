package io.github.org.programming.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static final HikariConfig config;
    private static final HikariDataSource dataSource;

    static {
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        props.setProperty("dataSource.databaseName", "postgres");
        props.setProperty("dataSource.portNumber", String.valueOf(5432));
        props.setProperty("dataSource.serverName", "localhost");
        props.put("dataSource.logWriter", new PrintWriter(System.out));
        config = new HikariConfig(props);
        dataSource = new HikariDataSource(config);
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
