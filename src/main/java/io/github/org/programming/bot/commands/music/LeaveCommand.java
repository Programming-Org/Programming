package io.github.org.programming.bot.commands.music;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.builder.slash.SlashCommandBuilder;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import io.github.org.programming.backend.type.CommandType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class LeaveCommand extends SlashCommandExtender {
    @Override
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {

    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("leave", "Leaves the voice channel")
                .build()
                .setToGuildOnly()
                .setCommandType(CommandType.MUSIC);
    }
}
