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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static io.github.org.programming.bot.commands.thread.ActiveQuestionsHandler.updateActiveQuestions;
import static io.github.org.programming.bot.commands.thread.util.SupportedCategories.categoryChoicesString;
import static io.github.org.programming.database.thread.AskDatabase.getAskAmount;
import static io.github.org.programming.database.thread.AskDatabase.updateAskDatabase;

public class AskCommand extends SlashCommandExtender {
    @Override
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        GuildOnlyCommand.guildOnlyCommand(event);
        // need to check if memeber has already created 2 threads in server channel
        // if yes, then send message saying they can't create anymore threads
        // if no, then create thread and send message saying thread created

        if (!event.getChannel().asTextChannel().getId().equals(BotConfig.getHelpChannelId())) {
            event.reply("You can only use this command in the help channel")
                .setEphemeral(true)
                .queue();
            return;
        }

        if (!getAskAmount(event.getMember().getId(), event.getGuild().getId()).isEmpty()
                && getAskAmount(event.getMember().getId(), event.getGuild().getId())
                    .size() >= BotConfig.getAskLimit()) {
            event.reply("You can only create " + BotConfig.getAskLimit() + " threads per day")
                .setEphemeral(true)
                .queue();
            return;
        }

        // event.getTextChannel().createThreadChannel("Ask a question").queue();

        var threadName = event.getOption("name", OptionMapping::getAsString);
        var threadCategory = event.getOption("category", OptionMapping::getAsString);
        var threadCategoryCapitalised =
                threadCategory.substring(0, 1).toUpperCase() + threadCategory.substring(1);

        if (threadName == null) {
            event.reply("You must provide a name and a type for the thread")
                .setEphemeral(true)
                .queue();
            return;
        }

        var textChannel =
                event.getGuild().getTextChannelById(BotConfig.getActiveQuestionChannelId());

        if (textChannel == null) {
            event.reply("Could not find thread channel").setEphemeral(true).queue();
            return;
        }

        var threadChannel =
                textChannel.createThreadChannel("[" + threadCategoryCapitalised + "] " + threadName)
                    .setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration.TIME_3_DAYS)
                    .complete();

        threadChannel
            .sendMessage(event.getMember().getAsMention() + "\n"
                    + "Note: That if no one response, it might mean your question is too vague or "
                    + "not clear enough.")
            .setEmbeds(detail())
            .queue(m -> m.getChannel()
                .asThreadChannel()
                .getParentChannel()
                .asTextChannel()
                .deleteMessageById(threadChannel.getId())
                .queue());

        event.reply("Your thread " + "<#" + threadChannel.getId() + ">" + " has been created")
            .setEphemeral(true)
            .queue();

        updateAskDatabase(event.getMember().getId(), event.getGuild().getId());
        updateActiveQuestions(threadChannel, AskThreadStatus.OPEN, threadCategory);
    }

    private MessageEmbed detail() {
        return new EmbedBuilder().setTitle("Please provide the following details")
            .addField("Description", "Please provide a description of your problem", false)
            .addField("Code",
                    "Please provide the code where the problem is happening. Seem image below for example",
                    false)
            .setImage(
                    "https://cdn.discordapp.com/attachments/1007751377306533898/1007751744308117524/codeSyntaxExample.png")
            .setColor(Color.GRAY)
            .build();
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("ask", "Ask a question")
            .addOption(OptionType.STRING, "name", "The name of the question", true)
            .addOptions(new OptionData(OptionType.STRING, "category", "The type of question", true)
                .addChoices(categoryChoices))
            .build()
            .setCommandType(CommandType.SUPPORT)
            .setToGuildOnly();
    }

    public static List<Command.Choice> categoryChoices = new ArrayList<>();

    static {
        categoryChoicesString.forEach(s -> categoryChoices
            .add(new Command.Choice(s.substring(0, 1).toUpperCase() + s.substring(1), s)));
    }
}
