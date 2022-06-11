package io.github.org.programming.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database1 {
    static final Logger logger = LoggerFactory.getLogger(Database1.class);

    protected static Connection connection;

    public static void main(String[] args) throws Exception {
        openDatabase();
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

    public static void createDataBase() {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                  CREATE DATABASE IF NOT EXISTS programming_bot;
                  USE programming_bot;
                """)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openDatabase() throws SQLException {
        logger.info("Connecting to database...");
        connection = DriverManager.getConnection(DatabaseConfig.getJDBCUrl(),
                DatabaseConfig.getUserName(), DatabaseConfig.getPassword());

        logger.info("The connection to the database was '{}'", connection.isValid(5));
    }

    public static void closeDatabase() throws SQLException {
        logger.info("Closing database connection...");
        connection.close();
        logger.info("The disconnection to the database was '{}'", connection.isClosed());
    }

}
