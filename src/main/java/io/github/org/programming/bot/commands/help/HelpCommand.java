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
package io.github.org.programming.bot.commands.help;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.builder.slash.SlashCommandBuilder;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import io.github.org.programming.backend.type.CommandType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements SlashCommandExtender {
    private final List<SlashCommandExtender> commands;
    private final List<SlashCommand> moderationCommands = new ArrayList<>();
    private final List<SlashCommand> utilityCommands = new ArrayList<>();
    private final List<SlashCommand> funCommands = new ArrayList<>();
    private final List<SlashCommand> musicCommands = new ArrayList<>();
    private final List<SlashCommand> infoCommands = new ArrayList<>();
    private final List<SlashCommand> supportCommands = new ArrayList<>();
    private final List<SlashCommand> normalCommands = new ArrayList<>();


    public HelpCommand(List<SlashCommandExtender> commands) {
        this.commands = commands;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commands.forEach(c -> {
            SlashCommand command = c.build();
            CommandType type = command.getCommandType();

            if (type == CommandType.MODERATION)
                moderationCommands.add(command);
            else if (type == CommandType.UTILITY)
                utilityCommands.add(command);
            else if (type == CommandType.FUN)
                funCommands.add(command);
            else if (type == CommandType.MUSIC)
                musicCommands.add(command);
            else if (type == CommandType.INFO)
                infoCommands.add(command);
            else if (type == CommandType.SUPPORT)
                supportCommands.add(command);
            else if (type == CommandType.NORMAL)
                normalCommands.add(command);
        });

        var embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Help");
        embedBuilder.setDescription("Here are all the categories of commands");

        event.replyEmbeds(embedBuilder.build())
            .addActionRow(Button.primary("moderation", "Moderation"),
                    Button.primary("utility", "Utility"), Button.primary("fun", "Fun"),
                    Button.primary("music", "Music"), Button.primary("info", "Info"),
                    Button.primary("support", "Support"), Button.primary("normal", "Normal"))
            .queue();
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        String id = event.getComponentId();

        switch (id) {
            case "moderation" -> event
                .editMessageEmbeds(
                        new HelpEmbed(moderationCommands, CommandType.MODERATION).build())
                .queue();
            case "utility" -> event
                .editMessageEmbeds(new HelpEmbed(utilityCommands, CommandType.UTILITY).build())
                .queue();
            case "fun" -> event
                .editMessageEmbeds(new HelpEmbed(funCommands, CommandType.FUN).build())
                .queue();
            case "music" -> event
                .editMessageEmbeds(new HelpEmbed(musicCommands, CommandType.MUSIC).build())
                .queue();
            case "info" -> event
                .editMessageEmbeds(new HelpEmbed(infoCommands, CommandType.INFO).build())
                .queue();
            case "support" -> event
                .editMessageEmbeds(new HelpEmbed(supportCommands, CommandType.SUPPORT).build())
                .queue();
            case "normal" -> event
                .editMessageEmbeds(new HelpEmbed(normalCommands, CommandType.NORMAL).build())
                .queue();
            default -> event.reply("This category does not exist").setEphemeral(true).queue();
        }
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("help", "Used to get help").build().setToGuildOnly();
    }
}
