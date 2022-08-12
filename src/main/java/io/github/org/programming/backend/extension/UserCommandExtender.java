package io.github.org.programming.backend.extension;

import io.github.org.programming.backend.builder.user.UserCommand;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public interface UserCommandExtender {

    void onUserContextInteraction(@NotNull UserContextInteractionEvent event);

    default void onButtonClick(ButtonInteractionEvent event) {

    }

    default void onModalInteraction(ModalInteractionEvent event) {

    }

    default void onCommandAutoComplete(@Nonnull CommandAutoCompleteInteractionEvent event) {

    }

    default void onSelectMenu(@Nonnull SelectMenuInteractionEvent event) {

    }

    UserCommand build();
}
