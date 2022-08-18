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
import io.github.org.programming.database.moderation.ModerationDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Map;

public class AuditCommand implements SlashCommandExtender {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "get_case" -> getCase(event);
            case "get_amount_warns" -> getAmountWarns(event);
            default -> event.reply("Unknown subcommand").setEphemeral(true).queue();
        }
    }

    private void getCase(@NotNull SlashCommandInteractionEvent event) {
        Integer caseId = event.getOption("case_id", OptionMapping::getAsInt);

        Map<String, String> userIdAndReason = ModerationDatabase.getUserIdAndReason(caseId);
        Map<String, String> typeAndModeratorId = ModerationDatabase.getTypeAndModeratorId(caseId);
        Instant timeStamp = ModerationDatabase.getTimeStamp(caseId);
        Integer amountOfWarns = ModerationDatabase.getAmountOfWarnings(caseId);

        String reason = userIdAndReason.get("reason");
        User user = event.getJDA().retrieveUserById(userIdAndReason.get("user_id")).complete();
        User moderator =
                event.getJDA().retrieveUserById(typeAndModeratorId.get("moderator_id")).complete();
        String type = typeAndModeratorId.get("type");

        event
            .replyEmbeds(
                    getCaseEmbed(caseId, user, reason, moderator, type, timeStamp, amountOfWarns))
            .queue();
    }

    private MessageEmbed getCaseEmbed(Integer caseId, User user, String reason, User moderator,
            String type, Instant timeStamp, Integer amountOfWarns) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Case #" + caseId);
        builder.setDescription("The user " + user.getAsMention() + " was " + type + " by "
                + moderator.getAsMention() + " for " + reason);
        builder.setFooter("Case was created at " + timeStamp);
        if (amountOfWarns != null) {
            builder.addField("Amount of warnings", amountOfWarns.toString(), false);
        }
        return builder.build();
    }

    private void getAmountWarns(@NotNull SlashCommandInteractionEvent event) {
        User user = event.getOption("user", OptionMapping::getAsUser);

        Integer amountOfWarns =
                ModerationDatabase.getAmountOfWarns(user.getId(), event.getGuild().getId());

        event.reply("The user " + user.getAsMention() + " has " + amountOfWarns + " warnings")
            .queue();
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("audit",
                "Used to get information about a specific audit log entry or any moderation info.")
                    .addSubcommands(new SubcommandData("get_case",
                            "Used to get information about a specific audit log entry with case.")
                                .addOption(OptionType.INTEGER, "case_id",
                                        "The case number of the audit log entry to get information about.",
                                        true),
                            new SubcommandData("get_amount_warns",
                                    "Used to get the amount of warns a user has.").addOption(
                                            OptionType.USER, "user",
                                            "The user to get the amount of warns of.", true))
                    .build()
                    .setUserPerms(Permission.ADMINISTRATOR)
                    .setBotPerms(Permission.ADMINISTRATOR)
                    .setToGuildOnly();
    }
}
