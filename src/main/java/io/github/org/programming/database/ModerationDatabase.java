package io.github.org.programming.database;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModerationDatabase {

    private static final Logger logger = Database.logger;

    private static final Connection connection = Database.connection;


    public static void createModerationTable() {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS moderation (
                        id INT NOT NULL AUTO_INCREMENT,
                        guild_id VARCHAR(255) NOT NULL,
                        user_id VARCHAR(255) NOT NULL,
                        moderator_id VARCHAR(255) NOT NULL,
                        reason VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id)
                    )
                """)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error creating moderation table", e);
        }
    }
}
