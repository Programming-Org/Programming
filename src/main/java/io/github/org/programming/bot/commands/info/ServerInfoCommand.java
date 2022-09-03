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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ServerInfoCommand implements SlashCommandExtender {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        var embedBuilder = new EmbedBuilder();
        var guild = event.getGuild();

        if (guild == null) {
            event.reply("This command can only be used in a guild!").setEphemeral(true).queue();
            return;
        }

        embedBuilder.setTitle("Info for " + guild.getName());
        embedBuilder
            .addField("Owner",
                    guild.getOwner() == null ? "Unknown" : guild.getOwner().getUser().getAsTag(),
                    true)
            .addField("Description",
                    guild.getDescription() == null ? "None" : guild.getDescription(), true)
            .addField("Members", String.valueOf(guild.getMemberCount()), true)
            .addField("Channels", String.valueOf(guild.getChannels().size()), true)
            .addField("Roles", String.valueOf(guild.getRoles().size()), true)
            .addField("Emojis", String.valueOf(guild.getEmojis().size()), true)
            .addField("Boosts", String.valueOf(guild.getBoostCount()), true)
            .addField("Boost Tier", String.valueOf(guild.getBoostTier()), true)
            .addField("Created At", guild.getTimeCreated().toString(), true)
            .addField("Verification Level", String.valueOf(guild.getVerificationLevel().getKey()),
                    true)
            .addField("Explicit Content Filter",
                    String.valueOf(guild.getExplicitContentLevel().getKey()), true)
            .addField("MFA Level", String.valueOf(guild.getRequiredMFALevel().getKey()), true)
            .addField("System Channel",
                    guild.getSystemChannel() == null ? "None"
                            : guild.getSystemChannel().getAsMention(),
                    true)
            .addField("Rules Channel",
                    guild.getRulesChannel() == null ? "None"
                            : guild.getRulesChannel().getAsMention(),
                    true)
            .addField("Public Updates Channel",
                    guild.getCommunityUpdatesChannel() == null ? "None"
                            : guild.getCommunityUpdatesChannel().getAsMention(),
                    true)
            .addField("Afk Channel",
                    guild.getAfkChannel() == null ? "None" : guild.getAfkChannel().getAsMention(),
                    true)
            .addField("Afk Timeout", String.valueOf(guild.getAfkTimeout().getSeconds()), true)
            .addField("Default Notification Level",
                    String.valueOf(guild.getDefaultNotificationLevel().getKey()), true)
            .addField("Vanity URL", guild.getVanityUrl() == null ? "None" : guild.getVanityUrl(),
                    true)
            .addField("Banner", guild.getBanner() == null ? "None" : guild.getBanner().toString(),
                    true)
            .addField("Features", String.join(", ", guild.getFeatures()), true)
            .addField("Max Members", String.valueOf(guild.getMaxMembers()), true)
            .addField("Max Presences", String.valueOf(guild.getMaxPresences()), true)
            .setThumbnail(guild.getIconUrl())
            .setColor(Color.GRAY)
            .setFooter("ID: " + guild.getId());

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("server_info", "Get info about the server").build()
            .setCommandType(CommandType.INFO);
    }
}
