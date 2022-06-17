package io.github.org.programming.bot.commands;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.builder.slash.SlashCommandBuilder;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This command was created to serve as an example of how to create a slash command with JDA and my
 * lib. <br>
 * This command basically replays with the gateway ping.
 */
public class ExampleCommand extends SlashCommandExtender {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.reply(event.getJDA().getGatewayPing() + "ms").queue();
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("ping", "Reply with the ping").build().setToGuildOnly();
    }
}
