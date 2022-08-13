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
