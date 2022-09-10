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

import java.time.Duration;
import java.time.LocalDateTime;

import static io.github.org.programming.bot.commands.moderation.util.ModerationUtil.sendMessageToAuditLog;

public class TempBanCommand extends SlashCommandExtender {
    @Override
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        GuildOnlyCommand.guildOnlyCommand(event);
        User user = event.getOption("user", OptionMapping::getAsUser);
        int delDays = event.getOption("days", OptionMapping::getAsInt);
        Member moderator = event.getMember();
        String reason = event.getOption("reason", OptionMapping::getAsString);

        // duration
        Long hours = event.getOption("hours", OptionMapping::getAsLong);
        Long days = event.getOption("days", OptionMapping::getAsLong);
        Long weeks = event.getOption("weeks", OptionMapping::getAsLong);
        Long months = event.getOption("months", OptionMapping::getAsLong);
        Long years = event.getOption("years", OptionMapping::getAsLong);

        Duration totalTime = Duration.ZERO;
        if (hours != null) {
            totalTime = totalTime.plusHours(hours);
        }

        if (days != null) {
            totalTime = totalTime.plusDays(days);
        }

        if (weeks != null) {
            totalTime = totalTime.plusDays(weeks * 7);
        }

        if (months != null) {
            totalTime = totalTime.plusDays(months * 30);
        }

        if (years != null) {
            totalTime = totalTime.plusDays(years * 365);
        }

        if (totalTime.isZero()) {
            totalTime = Duration.ofDays(1);
        }

        int id = ModerationDatabase.updateTempBanDatabase(event.getGuild().getId(), user.getId(),
                moderator.getId(), reason, totalTime);

        Duration finalTotalTime = totalTime;
        user.openPrivateChannel()
            .flatMap(channel -> channel
                .sendMessageEmbeds(tempBanEmbed(moderator, reason, id, finalTotalTime)))
            .mapToResult()
            .flatMap(message -> event.getGuild()
                .getChannelById(TextChannel.class, BotConfig.getAuditLogChannelId())
                .sendMessageEmbeds(sendMessageToAuditLog(user, "banned", moderator, id, reason,
                        new MessageEmbed.Field("Duration",
                                formatDuration(finalTotalTime).toString(), false))))
            .mapToResult()
            .flatMap(message -> event.getGuild().ban(user, delDays, reason))
            .flatMap(m -> event
                .reply("User has been banned for " + formatDuration(finalTotalTime) + "."))
            .queue();
    }

    private static MessageEmbed tempBanEmbed(Member moderator, String reason, int id,
            Duration duration) {
        return new EmbedBuilder().setTitle("Temp Ban")
            .setDescription("You have been temporarily banned by " + moderator.getAsMention()
                    + " for " + formatDuration(duration) + " for the following reason: " + reason)
            .setFooter("Case ID: " + id)
            .setTimestamp(LocalDateTime.now())
            .setColor(0xFF0000)
            .build();
    }

    private static LocalDateTime formatDuration(Duration duration) {
        return LocalDateTime.now().plus(duration);
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("temp_ban",
                "Used to temporarily ban a user from the server.")
                    .addOption(OptionType.USER, "user", "The user to ban", true)
                    .addOption(OptionType.STRING, "reason", "The reason for the ban", true)
                    .addOptions(new OptionData(OptionType.NUMBER, "deldays",
                            "The amount of days of message history you want to delete", true)
                                .setRequiredRange(0, 8))
                    .addOption(OptionType.INTEGER, "hours",
                            "The amount of hours to ban the user for")
                    .addOption(OptionType.INTEGER, "days", "The amount of days to ban the user for")
                    .addOption(OptionType.INTEGER, "weeks",
                            "The amount of weeks to ban the user for")
                    .addOption(OptionType.INTEGER, "months",
                            "The amount of months to ban the user for")
                    .addOption(OptionType.INTEGER, "years",
                            "The amount of years to ban the user for")
                    .build()
                    .setBotPerms(Permission.BAN_MEMBERS)
                    .setUserPerms(Permission.BAN_MEMBERS)
                    .setCommandType(CommandType.MODERATION)
                    .setToGuildOnly();
    }
}