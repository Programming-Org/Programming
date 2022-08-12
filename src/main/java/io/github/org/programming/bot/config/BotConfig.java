package io.github.org.programming.bot.config;

import io.github.yusufsdiscordbot.config.Config;
import org.jetbrains.annotations.NotNull;

public class BotConfig {
    public static String getToken() {
        return Config.getString("TOKEN");
    }

    public static long getOwnerId() {
        return Config.getLong("OWNER_ID");
    }

    public static long getGuildId() {
        return Config.getLong("GUILD_ID");
    }

    public static long getAuditLogChannelId() {
        return Config.getLong("AUDIT_LOG_CHANNEL_ID");
    }

    public static @NotNull Integer getCorePoolSize() {
        return Config.getInt("CORE_POOL_SIZE", 1);
    }
}
