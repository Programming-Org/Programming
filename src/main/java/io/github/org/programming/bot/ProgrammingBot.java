package io.github.org.programming.bot;

import io.github.org.programming.bot.config.BotConfig;
import io.github.org.programming.database.Database;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ProgrammingBot extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger(ProgrammingBot.class);

    private static ProgrammingBot instance;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final ScheduledExecutorService scheduledExecutor =
            Executors.newScheduledThreadPool(BotConfig.getCorePoolSize());

    public ProgrammingBot(String[] args) throws Exception {

        try {
            Database.openDatabase();
        } finally {
            Database.closeDatabase();
        }

        JDA jda = JDABuilder
            .createDefault(BotConfig.getToken(), GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS,
                    GatewayIntent.GUILD_PRESENCES)
            .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS)
            .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.MEMBER_OVERRIDES)
            .setActivity(Activity.watching("for misbehaving users"))
            .setStatus(OnlineStatus.ONLINE)
            .build();

        Guild guild = jda.awaitReady().getGuildById(BotConfig.getGuildId());

        jda.awaitReady().addEventListener(new SlashCommandReg(jda, guild), this);
    }

    public static ProgrammingBot getInstance() {
        return instance;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public void onReady(@NotNull ReadyEvent readyEvent) {
        logger.info("Bot is ready!");
    }
}
