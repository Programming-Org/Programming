package io.github.org.programming.database;

import io.github.yusufsdiscordbot.config.Config;

public class DatabaseConfig {

    public static String getDatabaseUser() {
        return Config.getString("DB_USER");
    }

    public static String getDatabasePassword() {
        return Config.getString("DB_PASSWORD");
    }

    public static String getDatabaseName() {
        return Config.getString("DATABASE_NAME");
    }

    public static int getPortNumber() {
        return Config.getInt("PORT_NUMBER");
    }

    public static String getServerName() {
        return Config.getString("SERVER_NAME");
    }
}
