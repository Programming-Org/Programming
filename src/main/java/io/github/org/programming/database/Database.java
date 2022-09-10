/*
 * Copyright 2022 Programming Org and other Programming Org contributors
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
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
        props.setProperty("dataSource.databaseName", DatabaseConfig.getDatabaseName());
        props.setProperty("dataSource.portNumber", String.valueOf(DatabaseConfig.getPortNumber()));
        props.setProperty("dataSource.serverName", DatabaseConfig.getServerName());
        props.setProperty("dataSource.user", DatabaseConfig.getDatabaseUser());
        props.setProperty("dataSource.password", DatabaseConfig.getDatabasePassword());
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
            assert listOfFiles != null;
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String sql = new String(Files.readAllBytes(file.toPath()));
                    statement.executeUpdate(sql);
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

    public static Logger getLogger() {
        return logger;
    }
}
