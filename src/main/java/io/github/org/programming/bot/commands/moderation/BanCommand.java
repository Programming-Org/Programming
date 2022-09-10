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
import io.github.org.programming.bot.commands.moderation.util.ModerationType;
import io.github.org.programming.bot.commands.util.GuildOnlyCommand;
import io.github.org.programming.bot.config.BotConfig;
import io.github.org.programming.database.moderation.ModerationDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import static io.github.org.programming.bot.commands.moderation.util.ModerationUtil.sendMessageToAuditLog;

public class BanCommand extends SlashCommandExtender {
    @Override
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        GuildOnlyCommand.guildOnlyCommand(event);
        User user = event.getOption("user", OptionMapping::getAsUser);
        int delDays = event.getOption("days", OptionMapping::getAsInt);
        Member moderator = event.getMember();
        String reason = event.getOption("reason", OptionMapping::getAsString);

        Integer id = ModerationDatabase.updateModerationDataBase(event.getGuild().getId(),
                user.getId(), moderator.getId(), reason, ModerationType.BAN);

        user.openPrivateChannel()
            .flatMap(channel -> channel.sendMessageEmbeds(banEmbed(moderator, reason, id)))
            .mapToResult()
            .flatMap(message -> event.getGuild()
                .getChannelById(TextChannel.class, BotConfig.getAuditLogChannelId())
                .sendMessageEmbeds(sendMessageToAuditLog(user, "banned", moderator, id, reason)))
            .mapToResult()
            .flatMap(message -> event.getGuild().ban(user, delDays, reason))
            .flatMap(message -> event.reply("Banned " + user.getAsMention() + " for " + reason))
            .queue();
    }

    private @NotNull MessageEmbed banEmbed(@NotNull Member moderator, @NotNull String reason,
            int caseId) {
        return new EmbedBuilder().setTitle("You have been banned from the server")
            .setDescription(
                    "You have been banned by " + moderator.getAsMention() + " for " + reason)
            .setFooter("Your case number is " + caseId, null)
            .setColor(0xFF0000)
            .build();
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("ban", "Used to ban a user")
            .addOption(OptionType.USER, "user", "The user to ban", true)
            .addOptions(new OptionData(OptionType.NUMBER, "deldays",
                    "The amount of days of message history you want to delete", true)
                        .setRequiredRange(0, 8))
            .addOption(OptionType.STRING, "reason", "The reason for banning the user", true)
            .build()
            .setBotPerms(Permission.BAN_MEMBERS)
            .setUserPerms(Permission.BAN_MEMBERS)
            .setCommandType(CommandType.MODERATION)
            .setToGuildOnly();
    }
}