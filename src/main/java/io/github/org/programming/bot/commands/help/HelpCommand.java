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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HelpCommand implements SlashCommandExtender {
    private final List<SlashCommandExtender> commands;
    private List<SlashCommand> moderationCommands;
    private List<SlashCommand> utilityCommands;
    private List<SlashCommand> funCommands;
    private List<SlashCommand> musicCommands;
    private List<SlashCommand> infoCommands;
    private List<SlashCommand> supportCommands;


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
        });
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("help", "Used to get help").build().setToGuildOnly();
    }
}
