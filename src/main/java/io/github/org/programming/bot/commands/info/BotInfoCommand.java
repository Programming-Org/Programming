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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.management.ManagementFactory;

import static io.github.org.programming.bot.util.TimeFormatter.formatTime;

public class BotInfoCommand extends SlashCommandExtender {
    @Override
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        GuildOnlyCommand.guildOnlyCommand(event);
        var embedBuilder = new EmbedBuilder();
        var selfUser = event.getJDA().getSelfUser();
        final long duration = ManagementFactory.getRuntimeMXBean().getUptime();
        final String os = System.getProperty("os.name");
        final String javaVersion = System.getProperty("java.version");
        final String jdaVersion = JDAInfo.VERSION_MAJOR + "." + JDAInfo.VERSION_MINOR + "."
                + JDAInfo.VERSION_CLASSIFIER;
        final String memory =
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024
                        / 1024 + "MB";
        final String cpu =
                ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors() + " Cores";

        embedBuilder.setTitle("Info about me :)");
        embedBuilder.addField("Name", selfUser.getName(), true)
            .addField("Tag", selfUser.getAsTag(), true)
            .addField("ID", selfUser.getId(), true)
            .addField("Host OS", os, true)
            .addField("Java Version", javaVersion, true)
            .addField("JDA Version", jdaVersion, true)
            .addField("Memory Usage", memory, true)
            .addField("CPU Cores", cpu, true)
            .addField("Created at", formatTime(selfUser.getTimeCreated()), true)
            .addField("Users", String.valueOf(event.getJDA().getUsers().size()), true)
            .addField("Ping", String.valueOf(event.getJDA().getGatewayPing()), true)
            .addField("Uptime", formatUptime(duration), true)
            .addField("Version", "1.0.0", true)
            .addField("Developer", "Programming Org", true)
            .addField("Github", "https://github.com/Programming-Org", true)
            .setColor(Color.GRAY)
            .setThumbnail(selfUser.getAvatarUrl());

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private String formatUptime(long duration) {
        final long years = duration / 31104000000L;
        final long months = duration / 2592000000L % 12;
        final long days = duration / 86400000L % 30;
        final long hours = duration / 3600000L % 24;
        final long minutes = duration / 60000L % 60;
        final long seconds = duration / 1000L % 60;
        final long milliseconds = duration % 1000;

        String uptime = (years == 0 ? "" : "**" + years + "** Years, ")
                + (months == 0 ? "" : "**" + months + "** Months, ")
                + (days == 0 ? "" : "**" + days + "** Days, ")
                + (hours == 0 ? "" : "**" + hours + "** Hours, ")
                + (minutes == 0 ? "" : "**" + minutes + "** Minutes, ")
                + (seconds == 0 ? "" : "**" + seconds + "** Seconds, ")
                + (milliseconds == 0 ? "" : "**" + milliseconds + "** Milliseconds, ");

        return uptime;
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("bot_info", "Get info about me :)").build()
            .setToGuildOnly()
            .setCommandType(CommandType.INFO);
    }
}
