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
import io.github.org.programming.bot.commands.util.GuildOnlyCommand;
import io.github.org.programming.database.tag.TagDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public class TagCommand extends SlashCommandExtender {
    @Override
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        switch (Objects.requireNonNull(event.getSubcommandName())) {
            case "get" -> onTagGet(event);
            case "list" -> onTagList(event);
            case "create" -> onTagCreate(event);
            case "edit" -> onTagEdit(event);
            case "delete" -> onTagDelete(event);
            default -> event.reply("Unknown subcommand").setEphemeral(true).queue();
        }
    }

    private void onTagGet(@NotNull SlashCommandInteractionEvent event) {
        String tagId = Objects.requireNonNull(event.getOption("tag_id")).getAsString();

        if (!TagDatabase.checkIfTagIdExists(tagId)) {
            event.reply("Tag with id " + tagId + " does not exist").setEphemeral(true).queue();
            return;
        }

        String tagName = TagDatabase.getName(tagId);
        String tagDescription = TagDatabase.getDescription(tagId);

        var embedBuilder = new EmbedBuilder().setTitle(tagName)
            .setDescription(tagDescription)
            .setColor(Color.GRAY)
            .setFooter("Tag ID: " + tagId)
            .setTimestamp(event.getTimeCreated())
            .build();

        event.replyEmbeds(embedBuilder).setEphemeral(true).queue();
    }

    private void onTagList(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();

        if (guild == null || member == null) {
            event.reply("This command can only be used in a guild").setEphemeral(true).queue();
            return;
        }

        if (!member.hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("You need the Manage Server permission to use this command")
                .setEphemeral(true)
                .queue();
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        TagDatabase.getTags().forEach((id, name) -> {
            stringBuilder.append("Id: ").append(id).append(" | Name: ").append(name);
        });

        if (stringBuilder.isEmpty()) {
            event.reply("There are no tags").setEphemeral(true).queue();
            return;
        }

        var embedBuilder = new EmbedBuilder().setTitle("Tags")
            .setDescription(stringBuilder.toString())
            .setColor(Color.GRAY)
            .setTimestamp(event.getTimeCreated())
            .build();

        event.replyEmbeds(embedBuilder).setEphemeral(true).queue();
    }

    private void onTagCreate(@NotNull SlashCommandInteractionEvent event) {
        GuildOnlyCommand.guildOnlyCommand(event);
        checkForPerms(event);


        TextInput tagId = TextInput.create("tag-id", "Tag ID", TextInputStyle.SHORT)
            .setPlaceholder("Enter the new tag ID")
            .setMinLength(1)
            .setMaxLength(10)
            .setRequired(true)
            .build();

        TextInput tagName = TextInput.create("tag-name", "Tag Name", TextInputStyle.SHORT)
            .setPlaceholder("Enter the name of the tag")
            .setMinLength(1)
            .setMaxLength(10)
            .setRequired(true)
            .build();

        TextInput tagDescription =
                TextInput.create("tag-description", "Tag Description", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Enter the description of the tag")
                    .setMinLength(30)
                    .setMaxLength(999)
                    .setRequired(true)
                    .build();

        Modal modal = Modal.create("tag-create", "Create A Tag")
            .addActionRows(ActionRow.of(tagId), ActionRow.of(tagName), ActionRow.of(tagDescription))
            .build();

        event.replyModal(modal).queue();
    }

    private void onTagEdit(@NotNull SlashCommandInteractionEvent event) {
        GuildOnlyCommand.guildOnlyCommand(event);
        checkForPerms(event);

        TextInput tagId = TextInput.create("tag-id", "Tag ID", TextInputStyle.SHORT)
            .setPlaceholder("Enter the ID of the tag you want to edit")
            .setMinLength(1)
            .setMaxLength(10)
            .setRequired(true)
            .build();

        TextInput tagName = TextInput.create("tag-name", "New Tag Name", TextInputStyle.SHORT)
            .setPlaceholder("Enter the new name of the tag")
            .setMinLength(1)
            .setMaxLength(32)
            .setRequired(false)
            .build();

        TextInput tagDescription =
                TextInput.create("tag-description", "New Tag Description", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Enter the new description of the tag")
                    .setMinLength(10)
                    .setMaxLength(999)
                    .setRequired(false)
                    .build();

        Modal modal = Modal.create("tag-edit", "Edit A Tag")
            .addActionRows(ActionRow.of(tagId), ActionRow.of(tagName), ActionRow.of(tagDescription))
            .build();

        event.replyModal(modal).queue();
    }

    private void onTagDelete(@NotNull SlashCommandInteractionEvent event) {
        GuildOnlyCommand.guildOnlyCommand(event);
        checkForPerms(event);
        var tagId = event.getOption("tag_id");

        if (tagId == null) {
            event.reply("Please provide a tag ID").setEphemeral(true).queue();
            return;
        }

        if (!TagDatabase.checkIfTagIdExists(tagId.getAsString())) {
            event.reply("Tag ID does not exist").setEphemeral(true).queue();
            return;
        }

        TagDatabase.deleteTag(tagId.getAsString());

        event.reply("Tag deleted").setEphemeral(true).queue();
    }

    private void checkForPerms(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();

        if (guild == null) {
            event.reply("I am not in a guild").setEphemeral(true).queue();
            return;
        }

        if (member == null) {
            event.reply("You are not in a guild").setEphemeral(true).queue();
            return;
        }

        if (!member.hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("You do not have permission to use this command")
                .setEphemeral(true)
                .queue();
            return;
        }

        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("I do not have permission to use this command").setEphemeral(true).queue();
            return;
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        switch (event.getModalId()) {
            case "tag-create" -> {
                var tagId = event.getValue("tag-id");

                if (tagId != null) {
                    if (!TagDatabase.checkIfTagIdExists(tagId.getAsString())) {
                        var tagName = event.getValue("tag-name");
                        var tagDescription = event.getValue("tag-description");

                        if (tagName != null && tagDescription != null) {
                            TagDatabase.createTag(tagId.getAsString(), tagName.getAsString(),
                                    tagDescription.getAsString());
                            event.reply("Tag created").setEphemeral(true).queue();
                        } else {
                            event.reply("You must provide a name and description for the tag")
                                .setEphemeral(true)
                                .queue();
                        }
                    }
                }
            }
            case "tag-edit" -> {
                var tagId = event.getValue("tag-id");

                if (tagId != null) {
                    if (TagDatabase.checkIfTagIdExists(tagId.getAsString())) {
                        var tagName = event.getValue("tag-name");
                        var tagDescription = event.getValue("tag-description");


                        if (tagName != null) {
                            TagDatabase.editTagName(tagId.getAsString(), tagName.getAsString());
                        }

                        if (tagDescription != null) {
                            TagDatabase.editTagDescription(tagId.getAsString(),
                                    tagDescription.getAsString());
                        }

                        if (tagName == null || tagDescription == null) {
                            event.getInteraction()
                                .reply("You must enter a new name and description")
                                .setEphemeral(true)
                                .queue();
                            return;
                        }

                        event.getInteraction().reply("Tag edited").setEphemeral(true).queue();
                    } else {
                        event.getInteraction()
                            .reply("Tag ID does not exist")
                            .setEphemeral(true)
                            .queue();
                    }
                } else {
                    event.getInteraction().reply("Tag ID not found").setEphemeral(true).queue();
                }
            }
        }
    }


    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("tag", "Used to get/create/edit/delete tags")
            .addSubcommands(
                    new SubcommandData("get", "Get a tag").addOption(OptionType.STRING, "tag_id",
                            "The id of the tag you want to get", true),
                    new SubcommandData("list", "List all tags"),
                    new SubcommandData("create", "Create a tag"),
                    new SubcommandData("edit", "Edit a tag"),
                    new SubcommandData("delete", "Delete a tag").addOption(OptionType.STRING,
                            "tag_id", "The id of the tag you wish to delete", true))
            .build()
            .setCommandType(CommandType.INFO);
    }
}
