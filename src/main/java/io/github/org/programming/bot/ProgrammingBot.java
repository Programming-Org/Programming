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
package io.github.org.programming.bot;

import io.github.org.programming.bot.commands.thread.ActiveQuestionsHandler;
import io.github.org.programming.bot.commands.thread.AskThreadStatus;
import io.github.org.programming.bot.config.BotConfig;
import io.github.org.programming.database.Database;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.pagination.ThreadChannelPaginationAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.github.org.programming.bot.commands.thread.ActiveQuestionsHandler.*;
import static io.github.org.programming.database.thread.AskDatabase.deleteAskDatabaseWithTime;
import static io.github.org.programming.database.thread.AskDatabase.getAskTimeStamps;

public class ProgrammingBot extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger(ProgrammingBot.class);

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final ScheduledExecutorService scheduledExecutor =
            Executors.newScheduledThreadPool(BotConfig.getCorePoolSize());

    private static DSLContext context;

    public ProgrammingBot(String[] args) throws Exception {
        onDatabase();

        JDA jda = JDABuilder
            .createDefault(BotConfig.getToken(), GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS,
                    GatewayIntent.GUILD_PRESENCES)
            .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS)
            .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.MEMBER_OVERRIDES)
            .setActivity(Activity.watching("for misbehaving users"))
            .setStatus(OnlineStatus.ONLINE)
            .build();


        Guild guild = jda.awaitReady().getGuildById(BotConfig.getGuildId());

        jda.awaitReady().addEventListener(new SlashCommandReg(jda, guild), this);

        logger.info("Bot is ready in guild {}", guild.getName());

        scheduledExecutor.scheduleAtFixedRate(() -> {
            checkIfAskActiveQuestionMessageExists(guild);
        }, 0, 1, TimeUnit.DAYS);

        // need to check this every minute
        scheduledExecutor.scheduleAtFixedRate(() -> {
            checkIfAskThreadTimeNeedsToBeRest(jda);
        }, 0, 1, TimeUnit.SECONDS);

        scheduledExecutor.scheduleAtFixedRate(() -> {
            checkIfAskHelpThreadArchived(guild);
        }, 0, 1, TimeUnit.SECONDS);
    }

    public synchronized void checkIfAskActiveQuestionMessageExists(Guild guild) {
        TextChannel activeQuestionsChannel =
                guild.getTextChannelById(BotConfig.getActiveQuestionChannelId());

        if (activeQuestionsChannel == null) {
            throw new IllegalStateException("Active questions channel not found");
        }

        String messageId = getActiveQuestionMessage(guild.getId());

        if (messageId == null) {
            Message message = activeQuestionsChannel.sendMessage(messageToSend()).complete();
            updateActiveQuestionMessage(guild.getId(), message.getId());
            ActiveQuestionsHandler.setMessage(message);
        } else {
            // TODO: nneed to add a way to check for erorr in message
            activeQuestionsChannel.retrieveMessageById(messageId)
                .queue(this::dealWithSuccess,
                        e -> dealWithError(e, guild, messageId, activeQuestionsChannel));
        }
    }

    private void dealWithError(Throwable error, Guild guild, String messageId,
            TextChannel activeQuestionsChannel) {
        if (Objects.equals(error.getMessage(), "10008: Unknown Message")) {
            deleteActiveQuestionMessageId(guild.getId(), messageId);
            activeQuestionsChannel.sendMessage(messageToSend()).queue(success -> {
                updateActiveQuestionMessage(guild.getId(), success.getId());
                ActiveQuestionsHandler.setMessage(success);
            });
        }
    }

    private void dealWithSuccess(Message message) {
        ActiveQuestionsHandler.setMessage(message);
    }

    public void onDatabase() {
        context = DSL.using(Database.getConnection(), SQLDialect.POSTGRES);

        if (Database.isConnected()) {
            logger.info("Connected to database");
        } else {
            logger.error("Failed to connect to database");
        }

        // closes the database when the bot is shut down
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Database.disconnect();
            } catch (Exception e) {
                logger.error("Failed to close database", e);
            }
        }));
    }

    public static DSLContext getContext() {
        return context;
    }

    public synchronized void checkIfAskThreadTimeNeedsToBeRest(JDA jda) {
        jda.getGuilds().forEach(c -> {
            List<Instant> oldTimeInstant = getAskTimeStamps(c.getId());
            // need to check if between 24 hours since last time asked
            oldTimeInstant.forEach(i -> {
                if (i.isAfter(Instant.now().minusSeconds(86400))) {
                    deleteAskDatabaseWithTime(i, c.getId());
                }
            });
        });
    }

    public synchronized void checkIfAskHelpThreadArchived(Guild guild) {
        TextChannel channel = guild.getTextChannelById(BotConfig.getActiveQuestionChannelId());
        ThreadChannelPaginationAction threadChannelPaginationAction =
                channel != null ? channel.retrieveArchivedPublicThreadChannels() : null;

        if (threadChannelPaginationAction == null) {
            logger.debug("No archived thread channels found");
            return;
        }

        List<ThreadChannel> archivedThreadChannels = threadChannelPaginationAction.complete();

        for (ThreadChannel c : archivedThreadChannels) {
            String name = c.getName();
            // name is [Java] Test22 need to get Java
            String category = name.substring(1, name.indexOf("]")).toLowerCase();
            updateActiveQuestions(c, AskThreadStatus.CLOSED, category);
        }
    }
}
