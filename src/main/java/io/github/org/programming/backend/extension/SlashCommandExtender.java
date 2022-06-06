package io.github.org.programming.backend.extension;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public abstract class SlashCommandExtender extends SlashCommand {

    protected SlashCommandExtender(SlashCommandData commandData) {
        super(commandData);
    }

    // This method is called when the command is executed.
    public abstract void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event);

    public void onButtonClick(ButtonInteractionEvent event) {

    }

    public void onModalInteraction(ModalInteractionEvent event) {

    }

    public void onCommandAutoComplete(@Nonnull CommandAutoCompleteInteractionEvent event) {

    }

    public void onSelectMenu(@Nonnull SelectMenuInteractionEvent event) {

    }

    protected abstract SlashCommand build();
}
