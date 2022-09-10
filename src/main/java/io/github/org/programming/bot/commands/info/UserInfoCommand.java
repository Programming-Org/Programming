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
package io.github.org.programming.bot.commands.info;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.builder.slash.SlashCommandBuilder;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import io.github.org.programming.backend.type.CommandType;
import io.github.org.programming.bot.commands.util.GuildOnlyCommand;
import io.github.org.programming.bot.util.TimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URL;

public class UserInfoCommand extends SlashCommandExtender {
    @Override
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        GuildOnlyCommand.guildOnlyCommand(event);
        var member = event.getOption("user", OptionMapping::getAsMember);

        if (member == null) {
            if (event.getMember() == null) {
                event.reply("You are not in a guild!").setEphemeral(true).queue();
                return;
            }

            var thisMember = event.getMember();

            getInfo(thisMember, thisMember.getUser(), event);
        } else {
            getInfo(member, member.getUser(), event);
        }
    }

    private void getInfo(@NotNull Member member, @NotNull User user,
            SlashCommandInteractionEvent event) {
        String NAME = member.getEffectiveName();
        String TAG = member.getUser().getName() + "#" + user.getDiscriminator();
        String GUILD_JOIN_DATE = TimeFormatter.formatTime(member.getTimeJoined());
        String DISCORD_JOINED_DATE = TimeFormatter.formatTime(member.getUser().getTimeCreated());
        String ID = member.getUser().getId();
        String STATUS = member.getOnlineStatus().getKey();
        StringBuilder ROLES = new StringBuilder();
        StringBuilder GAMES = new StringBuilder();
        String AVATAR = member.getUser().getAvatarUrl();

        for (Activity activity : member.getActivities()) {
            GAMES.append(activity.getName()).append(", ");
        }

        GAMES = GAMES.length() > 0 ? new StringBuilder(GAMES.substring(0, GAMES.length() - 2))
                : new StringBuilder("None");

        for (Role r : member.getRoles()) {
            ROLES.append(r.getName()).append(", ");
        }

        ROLES = ROLES.length() > 0 ? new StringBuilder(ROLES.substring(0, ROLES.length() - 2))
                : new StringBuilder("No roles on this server.");

        if (AVATAR == null) {
            AVATAR = "No Avatar";
        }

        EmbedBuilder em = new EmbedBuilder().setColor(Color.GRAY);
        em.setDescription("🕵️   **User information for " + member.getUser().getName() + ":**")
            .addField("Name / Nickname", NAME, true)
            .addField("User Tag", TAG, true)
            .addField("ID", ID, true)
            .addField("Current Status", STATUS, true)
            .addField("Current Activities", String.valueOf(GAMES), true)
            .addField("Roles", String.valueOf(ROLES), true)
            .addField("Guild Joined", GUILD_JOIN_DATE, true)
            .addField("Discord Joined", DISCORD_JOINED_DATE, true)
            .addField("Avatar-URL", AVATAR, true);

        if (!AVATAR.equals("No Avatar")) {
            em.setThumbnail(AVATAR);
        }

        event.replyEmbeds(em.build()).queue();
    }


    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("user_info", "Used to obtain information about a user.")
            .addOption(OptionType.USER, "user", "The user to obtain information about.", false)
            .build()
            .setToGuildOnly()
            .setCommandType(CommandType.INFO);
    }
}
