package io.github.org.programming.database;

import io.github.yusufsdiscordbot.config.Config;

public class DatabaseConfig {

    public static String getJDBCUrl() {
        return Config.getString("JDBC_URL");
    }

    public static String getUserName() {
        return Config.getString("DATA_BASE_USER_NAME");
    }

    public static String getPassword() {
        return Config.getString("DATA_BASE_PASSWORD");
    }
}
