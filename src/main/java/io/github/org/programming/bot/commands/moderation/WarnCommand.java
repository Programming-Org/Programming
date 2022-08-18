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
import io.github.org.programming.bot.commands.moderation.util.ModerationType;
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
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import static io.github.org.programming.bot.commands.moderation.util.ModerationUtil.sendMessageToAuditLog;

public class WarnCommand implements SlashCommandExtender {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "add" -> addWarn(event);
            case "remove" -> removeWarn(event);
            default -> event.reply("Unknown subcommand").setEphemeral(true).queue();
        }
    }

    private void addWarn(@NotNull SlashCommandInteractionEvent event) {
        User user = event.getOption("user", OptionMapping::getAsUser);
        String reason = event.getOption("reason", OptionMapping::getAsString);
        Member moderator = event.getMember();

        int id = ModerationDatabase.updateModerationDataBase(event.getGuild().getId(), user.getId(),
                moderator.getId(), reason, ModerationType.WARN);

        int amountOfWarnings =
                ModerationDatabase.getAmountOfWarns(user.getId(), event.getGuild().getId());

        user.openPrivateChannel()
            .flatMap(channel -> channel
                .sendMessageEmbeds(warnEmbed(moderator, reason, id, amountOfWarnings)))
            .mapToResult()
            .flatMap(message -> event.getGuild()
                .getChannelById(TextChannel.class, BotConfig.getAuditLogChannelId())
                .sendMessageEmbeds(sendMessageToAuditLog(user, "warned", moderator, id, reason,
                        new MessageEmbed.Field("Amount of warnings", amountOfWarnings + "",
                                false))))
            .mapToResult()
            .flatMap(m -> event.reply("Successfully warned " + user.getAsMention()))
            .queue();
    }

    private void removeWarn(@NotNull SlashCommandInteractionEvent event) {
        User user = event.getOption("user", OptionMapping::getAsUser);
        Member moderator = event.getMember();
        String reason = event.getOption("reason", OptionMapping::getAsString);

        int amountOfWarnings =
                ModerationDatabase.getAmountOfWarns(user.getId(), event.getGuild().getId());

        if (amountOfWarnings == 0) {
            event.reply("User has no warnings").setEphemeral(true).queue();
            return;
        }

        ModerationDatabase.removeWarning(user.getId(), event.getGuild().getId());

        user.openPrivateChannel()
            .flatMap(channel -> channel
                .sendMessageEmbeds(removeWarnEmbed(moderator, reason, amountOfWarnings)))
            .flatMap(m -> event.reply("Successfully removed a warning from " + user.getAsMention()))
            .queue();
    }

    private MessageEmbed warnEmbed(Member moderator, String reason, int id, int amountOfWarnings) {
        return new EmbedBuilder().setTitle("Warn")
            .setDescription(
                    "You have been warned by " + moderator.getEffectiveName() + " for " + reason)
            .addField("Case id", String.valueOf(id), false)
            .addField("Amount of warnings", String.valueOf(amountOfWarnings), false)
            .setColor(0xFF0000)
            .build();
    }

    private MessageEmbed removeWarnEmbed(Member moderator, String reason, int amountOfWarnings) {
        return new EmbedBuilder().setTitle("Warn removed")
            .setDescription(
                    moderator.getEffectiveName() + " has removed a warning from you for " + reason)
            .addField("Amount of warnings", String.valueOf(amountOfWarnings - 1), false)
            .setColor(0xFF0000)
            .build();
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("warn", "Used to add or remove a warning to/from a user")
            .addSubcommands(
                    new SubcommandData("add", "Adds a warning to a user")
                        .addOption(OptionType.USER, "user", "The user who you want to warn", true)
                        .addOption(OptionType.STRING, "reason",
                                "Thr reason you want to warn this user", true),
                    new SubcommandData("remove", "Removes a warning from a user")
                        .addOption(OptionType.USER, "user",
                                "The user who you want to remove a warning from", true)
                        .addOption(OptionType.STRING, "reason",
                                "The reason you want to remove this warning", true))
            .build()
            .setBotPerms(Permission.BAN_MEMBERS)
            .setUserPerms(Permission.BAN_MEMBERS)
            .setToGuildOnly();
    }
}
