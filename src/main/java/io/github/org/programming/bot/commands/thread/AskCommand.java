package io.github.org.programming.bot.commands.thread;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.builder.slash.SlashCommandBuilder;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.util.List;

public class AskCommand implements SlashCommandExtender {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // need to check if memeber has already created 2 threads in server channel
        // if yes, then send message saying they can't create anymore threads
        // if no, then create thread and send message saying thread created

        if (!event.getChannel().getName().equals("help")) {
            event.reply("You can only use this command in the help channel")
                .setEphemeral(true)
                .queue();
            return;
        }

        // event.getTextChannel().createThreadChannel("Ask a question").queue();

        var threadName = event.getOption("name", OptionMapping::getAsString);
        var threadType = event.getOption("type", OptionMapping::getAsString);

        if (threadName == null || threadType == null) {
            event.reply("You must provide a name and a type for the thread")
                .setEphemeral(true)
                .queue();
            return;
        }

        var thread = event.getTextChannel()
            .createThreadChannel("[" + threadType.toUpperCase() + "] " + threadName);

        thread
            .map(c -> c.sendMessage(
                    "Note: That if no one response, it might mean your question is to vague or "
                            + "not clear enough.")
                .setEmbeds(detail()))
            .queue();


    }

    private MessageEmbed detail() {
        return new EmbedBuilder().setTitle("Please provide the following details")
            .addField("Description", "Please provide a description of your problem", false)
            .addField("Code",
                    "Please provide the code where the problem is happening. Seem image below for example",
                    false)
            .setImage(
                    "https://cdn.discordapp.com/attachments/1007751377306533898/1007751744308117524/codeSyntaxExample.png")
            .setColor(Color.GRAY)
            .build();
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("ask", "Ask a question")
            .addOption(OptionType.STRING, "name", "The name of the question", true)
            .addOptions(new OptionData(OptionType.STRING, "type", "The type of question", true)
                .addChoices(choices))
            .build()
            .setToGuildOnly();
    }

    private List<Command.Choice> choices =
            List.of(new Command.Choice("Java", "java"), new Command.Choice("C++", "c++"),
                    new Command.Choice("C#", "c#"), new Command.Choice("Python", "python"),
                    new Command.Choice("JavaScript", "js"), new Command.Choice("PHP", "php"),
                    new Command.Choice("C", "c"), new Command.Choice("Go", "go"),
                    new Command.Choice("Rust", "rust"), new Command.Choice("Swift", "swift"),
                    new Command.Choice("Kotlin", "kotlin"), new Command.Choice("Scala", "scala"),
                    new Command.Choice("TypeScript", "ts"), new Command.Choice("Other", "other"));
}
