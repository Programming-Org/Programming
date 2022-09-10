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
package io.github.org.programming.bot.commands.thread;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.builder.slash.SlashCommandBuilder;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import io.github.org.programming.backend.type.CommandType;
import io.github.org.programming.bot.commands.util.GuildOnlyCommand;
import io.github.org.programming.bot.config.BotConfig;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import static io.github.org.programming.bot.commands.thread.ActiveQuestionsHandler.updateActiveQuestions;

public class CloseAskThread extends SlashCommandExtender {

    @Override
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        GuildOnlyCommand.guildOnlyCommand(event);
        ThreadChannel threadChannel = event.getGuildChannel().asThreadChannel();

        if (!threadChannel.getParentChannel()
            .asTextChannel()
            .getId()
            .equals(BotConfig.getActiveQuestionChannelId())) {
            event.reply(
                    "You can only use this command for threads which are associated with the active question channel")
                .setEphemeral(true)
                .queue();
            return;
        }

        if (threadChannel.isArchived()) {
            event.reply("This thread is already closed").setEphemeral(true).queue();
            return;
        }

        String name = threadChannel.getName();
        String category = name.substring(1, name.indexOf("]")).toLowerCase();

        threadChannel.getManager().setArchived(true).queue();
        updateActiveQuestions(threadChannel, AskThreadStatus.CLOSED, category);

        event.reply("Thread closed").setEphemeral(true).queue();
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("close",
                "Used to close an thread in the active question channel").build()
                    .setCommandType(CommandType.SUPPORT)
                    .setToGuildOnly();
    }
}