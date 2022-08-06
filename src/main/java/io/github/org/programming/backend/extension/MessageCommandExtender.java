package io.github.org.programming.backend.extension;

import io.github.org.programming.backend.builder.message.MessageCommand;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;

import javax.annotation.Nonnull;

public interface MessageCommandExtender {

    void onMessageContextInteraction(@Nonnull MessageContextInteractionEvent event);

    default void onButtonClick(ButtonInteractionEvent event) {

    }

    default void onModalInteraction(ModalInteractionEvent event) {

    }

    default void onCommandAutoComplete(@Nonnull CommandAutoCompleteInteractionEvent event) {

    }

    default void onSelectMenu(@Nonnull SelectMenuInteractionEvent event) {

    }

    MessageCommand build();
}
