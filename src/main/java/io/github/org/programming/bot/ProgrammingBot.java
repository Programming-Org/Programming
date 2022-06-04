package io.github.org.programming.bot;

import io.github.org.programming.backend.handler.SlashCommandHandler;
import io.github.org.programming.config.BotConfig;
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

import java.util.Objects;

public class ProgrammingBot extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger(ProgrammingBot.class);

    public ProgrammingBot(String[] args) throws Exception {
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

        SlashCommandHandler handler = new SlashCommandHandler(jda, guild, BotConfig.getOwnerId());
        jda.awaitReady().addEventListener(handler, this);
        handler.addSlashCommands();
    }

    @Override
    public void onReady(@NotNull ReadyEvent readyEvent) {
        logger.info("Bot is ready!");
    }
}
