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
package io.github.org.programming.bot.commands.moderation;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.builder.slash.SlashCommandBuilder;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import io.github.org.programming.backend.type.CommandType;
import io.github.org.programming.bot.commands.util.GuildOnlyCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class DeleteMessagesCommand extends SlashCommandExtender {

    @Override
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        GuildOnlyCommand.guildOnlyCommand(event);
        switch (event.getSubcommandName()) {
            case "user" -> deleteUserMessages(event);
            case "channel" -> deleteChannelMessages(event);
            default -> event.reply("Unknown subcommand").setEphemeral(true).queue();
        }
    }

    private void deleteUserMessages(SlashCommandInteractionEvent event) {
        User user = event.getOption("user", OptionMapping::getAsUser);
        int amount = event.getOption("amount", OptionMapping::getAsInt);

        // use an iterable history
        Iterator<GuildChannel> channels = event.getGuild().getChannels().iterator();

        try {
            while (channels.hasNext()) {
                GuildChannel channel = channels.next();
                if (channel instanceof MessageChannel messageChannel) {
                    messageChannel.getHistory().retrievePast(amount).queue(messages -> {
                        for (Message message : messages) {
                            if (message.getAuthor().equals(user)) {
                                message.delete().queue();
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            event.reply("Error deleting messages").setEphemeral(true).queue();
        } finally {
            event
                .reply("Deleted messages from " + user.getAsMention() + " in "
                        + event.getGuild().getName())
                .queue();
        }
    }

    private void deleteChannelMessages(SlashCommandInteractionEvent event) {
        GuildChannel channel = event.getOption("channel", OptionMapping::getAsChannel);
        int amount = event.getOption("amount", OptionMapping::getAsInt);

        if (channel == null) {
            event.reply("Please specify a channel").setEphemeral(true).queue();
            return;
        }

        if (channel instanceof MessageChannel) {
            ((MessageChannel) channel).getHistory().retrievePast(amount).queue(messages -> {
                messages.forEach(message -> {
                    message.delete().queue();
                });
            });

            event.reply("Deleted " + amount + " messages from " + channel.getName()).queue();
        } else {
            event.reply("Please specify a text channel").setEphemeral(true).queue();
            return;
        }
    }

    @Override
    public SlashCommand build() {
        int min_amount = 1;
        int max_amount = 200;
        return new SlashCommandBuilder("delete_messages",
                "Used to delete a specific amount of messages for a user or channel")
                    .addSubcommands(
                            new SubcommandData("user",
                                    "delete a specific amount of messages from a user")
                                        .addOption(OptionType.USER, "user",
                                                "The user to delete messages from", true)
                                        .addOptions(new OptionData(OptionType.INTEGER, "amount",
                                                "The amount of messages to delete", true)
                                                    .setRequiredRange(min_amount, max_amount)),
                            new SubcommandData("channel",
                                    "delete a specific amount of messages from a channel")
                                        .addOption(OptionType.CHANNEL, "channel",
                                                "The channel to delete messages from", true)
                                        .addOptions(new OptionData(OptionType.INTEGER, "amount",
                                                "The amount of messages to delete", true)
                                                    .setRequiredRange(min_amount, max_amount)))
                    .build()
                    .setBotPerms(Permission.MESSAGE_MANAGE)
                    .setUserPerms(Permission.MESSAGE_MANAGE)
                    .setCommandType(CommandType.MODERATION)
                    .setToGuildOnly();
    }
}