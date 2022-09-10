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
import io.github.org.programming.bot.commands.moderation.util.ModerationUtil;
import io.github.org.programming.bot.commands.util.GuildOnlyCommand;
import io.github.org.programming.bot.config.BotConfig;
import io.github.org.programming.database.moderation.ModerationDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

public class TimeOutCommand extends SlashCommandExtender {
    // max time
    private static final int MAX_TIMEOUT_DURATION_MIN = 40320; // 28 days
    private static final int MAX_TIMEOUT_DURATION_HOUR = 672; // 28 days
    private static final int MAX_TIMEOUT_DURATION_DAY = 28; // 28 days
    private static final int MAX_TIMEOUT_DURATION_WEEK = 4; // 28 days

    @Override
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        GuildOnlyCommand.guildOnlyCommand(event);
        switch (event.getSubcommandName()) {
            case "add" -> addTimeOut(event);
            case "remove" -> removeTimeOut(event);
            default -> event.reply("Invalid subcommand").setEphemeral(true).queue();
        }
    }

    private void addTimeOut(SlashCommandInteractionEvent event) {
        Member user = event.getOption("user", OptionMapping::getAsMember);
        Member moderator = event.getMember();
        String reason = event.getOption("reason", OptionMapping::getAsString);

        // duration
        Long hours = event.getOption("hours", OptionMapping::getAsLong);
        Long days = event.getOption("days", OptionMapping::getAsLong);
        Long weeks = event.getOption("weeks", OptionMapping::getAsLong);

        Duration duration = Duration.ofDays(0);

        if (hours != null) {
            duration = Duration.ofHours(hours);
        }

        if (days != null) {
            duration = Duration.ofDays(days);
        }

        if (weeks != null) {
            duration = Duration.ofDays(weeks * 7);
        }

        if (duration.equals(Duration.ofDays(0)) || duration.isNegative()) {
            event.reply("Invalid duration").setEphemeral(true).queue();
            return;
        }

        if (duration.toMinutes() > MAX_TIMEOUT_DURATION_MIN) {
            event.reply("Duration is too long, can't be longer than 28 days")
                .setEphemeral(true)
                .queue();
            return;
        }

        Integer id = ModerationDatabase.updateModerationDataBase(event.getGuild().getId(),
                user.getId(), moderator.getId(), reason, ModerationType.ADD_TIME_OUT);

        Duration finalDuration = duration;
        user.getUser()
            .openPrivateChannel()
            .flatMap(privateChannel -> privateChannel.sendMessageEmbeds(
                    timeoutEmbed(event.getGuild(), moderator, finalDuration, reason, id)))
            .mapToResult()
            .flatMap(m -> event.getGuild()
                .getChannelById(TextChannel.class, BotConfig.getAuditLogChannelId())
                .sendMessageEmbeds(ModerationUtil.sendMessageToAuditLog(user.getUser(),
                        "added timed out", moderator, id, reason)))
            .mapToResult()
            .flatMap(g -> event.getGuild().timeoutFor(user, finalDuration))
            .flatMap(m -> event.reply("User timed out.").setEphemeral(true))
            .queue();
    }

    private MessageEmbed timeoutEmbed(Guild guild, @NotNull Member moderator,
            @NotNull Duration duration, @NotNull String reason, @NotNull Integer id) {
        return new EmbedBuilder().setTitle("You have been timed out")
            .setDescription("You have been timed out from " + guild.getName() + " for "
                    + duration.toDays() + " days. Reason: " + reason)
            .addField("Case ID", String.valueOf(id), false)
            .setColor(0xFF0000)
            .setFooter("Moderator: " + moderator.getEffectiveName(),
                    moderator.getUser().getAvatarUrl())
            .setTimestamp(Instant.now())
            .build();
    }

    private void removeTimeOut(@NotNull SlashCommandInteractionEvent event) {
        Member user = event.getOption("user", OptionMapping::getAsMember);
        Member moderator = event.getMember();
        String reason = event.getOption("reason", OptionMapping::getAsString);

        Integer id = ModerationDatabase.updateModerationDataBase(event.getGuild().getId(),
                user.getId(), moderator.getId(), reason, ModerationType.REMOVE_TIME_OUT);

        user.getUser()
            .openPrivateChannel()
            .flatMap(privateChannel -> privateChannel
                .sendMessageEmbeds(removeTimeoutEmbed(event.getGuild(), moderator, reason, id)))
            .mapToResult()
            .flatMap(m -> m.get()
                .getGuild()
                .getChannelById(TextChannel.class, BotConfig.getAuditLogChannelId())
                .sendMessageEmbeds(ModerationUtil.sendMessageToAuditLog(user.getUser(),
                        "removed time out", moderator, id, reason)))
            .mapToResult()
            .flatMap(g -> g.get().getGuild().removeTimeout(user))
            .flatMap(m -> event.reply("Removed time out.").setEphemeral(true))
            .queue();

    }

    private MessageEmbed removeTimeoutEmbed(Guild guild, @NotNull Member moderator,
            @NotNull String reason, @NotNull Integer id) {
        return new EmbedBuilder().setTitle("You have been removed from the timeout list")
            .setDescription("You have been removed from the timeout list from " + guild.getName()
                    + " for " + reason)
            .addField("Case ID", String.valueOf(id), false)
            .setColor(0xFF0000)
            .setFooter("Moderator: " + moderator.getEffectiveName(),
                    moderator.getUser().getAvatarUrl())
            .setTimestamp(Instant.now())
            .build();
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("time_out",
                "Used to time out a user for a specified amount of time.")
                    .addSubcommands(new SubcommandData("add", "Used to timeout a user")
                        .addOption(OptionType.USER, "user", "The user to time out.", true)
                        .addOption(OptionType.STRING, "reason", "The reason for the timeout.", true)
                        .addOptions(new OptionData(OptionType.INTEGER, "hours",
                                "The length of the time out in hours", false).setRequiredRange(1,
                                        MAX_TIMEOUT_DURATION_HOUR))
                        .addOptions(new OptionData(OptionType.INTEGER, "days",
                                "The length of the time out in days", false).setRequiredRange(1,
                                        MAX_TIMEOUT_DURATION_DAY))
                        .addOptions(new OptionData(OptionType.INTEGER, "weeks",
                                "The length of the time out in weeks", false).setRequiredRange(1,
                                        MAX_TIMEOUT_DURATION_WEEK)),
                            new SubcommandData("remove", "Used to remove a timeout from a user.")
                                .addOption(OptionType.USER, "user",
                                        "The user to remove the timeout from.", true)
                                .addOption(OptionType.STRING, "reason",
                                        "The reason for the timeout removal.", true))
                    .build()
                    .setBotPerms(Permission.MODERATE_MEMBERS)
                    .setUserPerms(Permission.MODERATE_MEMBERS)
                    .setCommandType(CommandType.MODERATION)
                    .setToGuildOnly();
    }
}
