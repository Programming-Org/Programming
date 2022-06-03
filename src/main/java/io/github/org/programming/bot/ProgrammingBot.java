package io.github.org.programming.bot;

import io.github.org.programming.config.BotConfig;
import io.github.yusufsdiscordbot.yusufsdiscordcore.bot.handler.SlashCommandHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.Objects;

public class ProgrammingBot {
    public ProgrammingBot(String[] args) throws Exception {
        JDA jda = JDABuilder.createDefault(BotConfig.getToken())
            .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS)
            .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.MEMBER_OVERRIDES)
            .setActivity(Activity.watching("for misbehaving users"))
            .setStatus(OnlineStatus.ONLINE)
            .build();

        SlashCommandHandler handler = new SlashCommandHandler(jda,
                Objects.requireNonNull(jda.getGuildById(BotConfig.getGuildId())),
                BotConfig.getOwnerId());
        jda.awaitReady().addEventListener(handler);
        handler.addSlashCommand();
        handler.queueSlashCommand();
    }
}
