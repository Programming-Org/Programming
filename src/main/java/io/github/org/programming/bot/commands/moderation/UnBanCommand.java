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
import org.jetbrains.annotations.NotNull;


public class UnBanCommand implements SlashCommandExtender {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        User user = event.getOption("member", OptionMapping::getAsUser);
        Member moderator = event.getMember();
        String reason = event.getOption("reason", OptionMapping::getAsString);

        int id = ModerationDatabase.updateModerationDataBase(event.getGuild().getId(), user.getId(),
                moderator.getId(), reason, "unban");

        event.getGuild()
            .unban(user)
            .flatMap(channel -> event.getGuild()
                .getChannelById(TextChannel.class, BotConfig.getAuditLogChannelId())
                .sendMessageEmbeds(unbanEmbed(user, moderator, reason, id)))
            .flatMap(message -> event.reply("Unbanned " + user.getAsMention() + " for " + reason))
            .queue();

    }

    private MessageEmbed unbanEmbed(User member, @NotNull Member moderator, @NotNull String reason,
            int caseId) {
        return new EmbedBuilder()
            .setTitle("The member + " + member.getAsMention() + " has been unbanned")
            .setDescription("The member has been unbanned by " + moderator.getAsMention() + " for "
                    + reason)
            .setFooter("Your case number is " + caseId, null)
            .setColor(0x00FF00)
            .build();

    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("unban", "The user to unban")
            .addOption(OptionType.USER, "member", "The user to unban", true)
            .addOption(OptionType.STRING, "reason", "The reason for the unban", true)
            .build()
            .setBotPerms(Permission.BAN_MEMBERS)
            .setUserPerms(Permission.BAN_MEMBERS);
    }
}
