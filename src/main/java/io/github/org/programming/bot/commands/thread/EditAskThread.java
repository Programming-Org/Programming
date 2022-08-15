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
import io.github.org.programming.bot.config.BotConfig;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static io.github.org.programming.bot.commands.thread.AskCommand.categoryChoices;

public class EditAskThread implements SlashCommandExtender {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
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
            event.reply("This thread is closed").setEphemeral(true).queue();
            return;
        }

        switch (Objects.requireNonNull(event.getSubcommandName())) {
            case "name" -> editName(threadChannel, event);
            case "category" -> editCategory(threadChannel, event);
            default -> event.reply("Invalid subcommand").setEphemeral(true).queue();
        }
    }

    private void editName(ThreadChannel threadChannel, SlashCommandInteractionEvent event) {
        String oldName = threadChannel.getName();
        String oldCategory = oldName.substring(1, oldName.indexOf("]")).toLowerCase();
        String newName = event.getOption("new_name", OptionMapping::getAsString);

        if (newName == null) {
            event.reply("Please provide a new name").setEphemeral(true).queue();
            return;
        }

        if (newName.length() > 100) {
            event.reply("The new name is too long").setEphemeral(true).queue();
            return;
        }

        if (newName.equals(oldName)) {
            event.reply("The new name is the same as the old name").setEphemeral(true).queue();
            return;
        }
    }

    private void editCategory(ThreadChannel threadChannel, SlashCommandInteractionEvent event) {

    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("edit",
                "Used to edit a thread in the active question channel")
                    .addSubcommands(List.of(
                            new SubcommandData("name", "Used to edit the name of a thread")
                                .addOption(OptionType.STRING, "new_name",
                                        "The new name of the thread"),
                            new SubcommandData("category", "Used to edit the category of a thread")
                                .addOptions(new OptionData(OptionType.STRING, "new_category",
                                        "The new category of the thread")
                                            .addChoices(categoryChoices))))
                    .build();
    }
}
