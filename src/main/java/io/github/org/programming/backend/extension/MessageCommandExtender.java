package io.github.org.programming.backend.extension;

import io.github.org.programming.backend.builder.message.MessageCommand;
import io.github.org.programming.backend.builder.slash.SlashCommand;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public abstract class MessageCommandExtender extends MessageCommand {

    public MessageCommandExtender(CommandData commandData) {
        super(commandData);
    }

    public abstract void onMessageContextInteraction(@Nonnull MessageContextInteractionEvent event);

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
