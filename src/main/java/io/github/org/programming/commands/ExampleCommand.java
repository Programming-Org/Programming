package io.github.org.programming.commands;

import io.github.org.programming.backendv1.builder.SlashCommand;
import io.github.org.programming.backendv1.builder.SlashCommandBuilder;
import io.github.org.programming.backendv1.extension.SlashCommandExtender;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

/**
 * This command was created to serve as an example of how to create a slash command with JDA and my
 * lib. <br>
 * This command basically replays with the gateway ping.
 */
public class ExampleCommand extends SlashCommandExtender {
    protected ExampleCommand(SlashCommandData commandData) {
        super(commandData);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.reply(event.getJDA().getGatewayPing() + "ms").queue();
    }

    @Override
    protected SlashCommand build() {
        return new SlashCommandBuilder("ping", "Reply with the ping").build().setToGuildOnly();
    }
}
