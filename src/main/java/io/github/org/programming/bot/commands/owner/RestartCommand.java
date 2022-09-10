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
package io.github.org.programming.bot.commands.owner;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.builder.slash.SlashCommandBuilder;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import io.github.org.programming.backend.type.CommandType;
import io.github.org.programming.bot.ProgrammingBot;
import io.github.org.programming.bot.commands.util.GuildOnlyCommand;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class RestartCommand extends SlashCommandExtender {
    private final Logger logger = ProgrammingBot.getLogger();

    @Override
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        GuildOnlyCommand.guildOnlyCommand(event);
        var restarted = false;

        try {
            event.reply("Restarting...").queue();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("Failed to restart bot", e);
        }

        try {
            event.getJDA().shutdownNow();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            logger.info("Restarting bot...");

            try {
                new ProgrammingBot();
                restarted = true;
            } catch (Exception e) {
                logger.error("Failed to restart bot", e);
                System.exit(1);
            } finally {
                if (restarted) {
                    logger.info("Bot restarted successfully");
                } else {
                    logger.error("Bot failed to restart");
                }
            }
        }
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("restart", "Restarts the bot").build()
            .setToGuildOnly()
            .setToOwnerOnly()
            .setCommandType(CommandType.OWNER_ONLY);
    }
}
