package io.github.org.programming.bot.commands.thread;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.builder.slash.SlashCommandBuilder;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AskCommand implements SlashCommandExtender {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        //need to check if memeber has already created 2 threads in server channel
        //if yes, then send message saying they can't create anymore threads
        //if no, then create thread and send message saying thread created

        if (!event.getChannel().getName().equals("help")) {
            event.reply("You can only use this command in the help channel").setEphemeral(true).queue();
            return;
        }

        //event.getTextChannel().createThreadChannel("Ask a question").queue();
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("ask", "Ask a question").build().setToGuildOnly();
    }

    private List<CommandData> getCommandData() {
        return null;
    }
}
