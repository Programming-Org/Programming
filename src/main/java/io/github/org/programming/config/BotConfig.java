package io.github.org.programming.config;

import io.github.yusufsdiscordbot.config.Config;

public class BotConfig {
    public static String getToken() {
        return Config.getString("token");
    }

    public static String getGuildId() {
        return Config.getString("guildId");
    }

    public static long getOwnerId() {
        return Config.getLong("ownerId");
    }
}
