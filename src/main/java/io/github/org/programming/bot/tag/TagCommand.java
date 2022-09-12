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
package io.github.org.programming.bot.tag;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.builder.slash.SlashCommandBuilder;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import io.github.org.programming.backend.type.CommandType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TagCommand extends SlashCommandExtender {
    @Override
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        switch (Objects.requireNonNull(event.getSubcommandName())) {
            case "get" -> onTagGet(event);
            case "create" -> onTagCreate(event);
            case "edit" -> onTagEdit(event);
            case "delete" -> onTagDelete(event);
            default -> event.reply("Unknown subcommand").setEphemeral(true).queue();
        }
    }

    private void onTagGet(@NotNull SlashCommandInteractionEvent event) {}

    private void onTagCreate(@NotNull SlashCommandInteractionEvent event) {
        TextInput tagName = TextInput.create("tag-name", "Tag Name", TextInputStyle.SHORT)
            .setPlaceholder("Enter the name of the tag")
            .setMinLength(1)
            .setMaxLength(32)
            .build();

        TextInput tagDescription =
                TextInput.create("tag-description", "Tag Description", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Enter the description of the tag")
                    .setMinLength(30)
                    .setMaxLength(999)
                    .build();

        Modal modal = Modal.create("tag-create", "Create A Tag")
            .addActionRows(ActionRow.of(tagName), ActionRow.of(tagDescription))
            .build();

        event.replyModal(modal).queue();
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

    }

    private void onTagEdit(@NotNull SlashCommandInteractionEvent event) {}

    private void onTagDelete(@NotNull SlashCommandInteractionEvent event) {}

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("tag", "Used to create/edit/delete tags")
            .addSubcommands(
                    new SubcommandData("get", "Get a tag").addOption(OptionType.STRING, "tag_id",
                            "The id of the tag you want to get", true),
                    new SubcommandData("create", "Create a tag").addOption(OptionType.STRING,
                            "tag_id", "A new tag id", true),
                    new SubcommandData("edit", "Edit a tag").addOption(OptionType.STRING, "tag_id",
                            "The id of the tag you want to edit", true),
                    new SubcommandData("delete", "Delete a tag").addOption(OptionType.STRING,
                            "tag_id", "The id of the tag you wish to delete", true))
            .build()
            .setCommandType(CommandType.INFO);
    }
}
