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
package io.github.org.programming.backend.builder.slash;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SlashCommandBuilder {
    private final String name;
    private final String description;
    private List<OptionData> options = new ArrayList<>();
    private List<SubcommandData> subcommands = new ArrayList<>();;
    private List<SubcommandGroupData> subcommandGroups = new ArrayList<>();;

    /**
     * Creates a new SlashCommandBuilder
     * 
     * @param name The name of the command
     * @param description The description of the command
     */
    public SlashCommandBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String getDescription() {
        return description;
    }

    @NotNull
    public List<SubcommandData> getSubcommands() {
        return subcommands == null ? List.of() : subcommands;
    }

    @NotNull
    public List<SubcommandGroupData> getSubcommandGroups() {
        return subcommandGroups == null ? List.of() : subcommandGroups;
    }

    @NotNull
    public List<OptionData> getOptions() {
        return options == null ? List.of() : options;
    }

    public SlashCommandBuilder addOptions(@Nonnull OptionData... options) {
        this.options.addAll(List.of(options));
        return this;
    }

    public SlashCommandBuilder addOptions(@Nonnull List<OptionData> options) {
        this.options.addAll(options);
        return this;
    }

    public SlashCommandBuilder addOptions(@Nonnull Collection<? extends OptionData> options) {
        this.options.addAll(options);
        return this;
    }

    @NotNull
    public SlashCommandBuilder addOption(@NotNull OptionType type, @NotNull String name,
            @NotNull String description) {
        this.options.add(new OptionData(type, name, description));
        return this;
    }


    @NotNull
    public SlashCommandBuilder addOption(@NotNull OptionType type, @NotNull String name,
            @NotNull String description, boolean required) {
        this.options.add(new OptionData(type, name, description, required));
        return this;
    }

    public SlashCommandBuilder addOption(@Nonnull OptionType type, @Nonnull String name,
            @Nonnull String description, boolean required, boolean autoComplete) {
        this.options.add(new OptionData(type, name, description, required, autoComplete));
        return this;
    }

    public SlashCommandBuilder addSubcommands(@NotNull SubcommandData... subcommands) {
        this.subcommands.addAll(List.of(subcommands));
        return this;
    }

    @NotNull
    public SlashCommandBuilder addSubcommands(@NotNull List<SubcommandData> subcommands) {
        this.subcommands.addAll(subcommands);
        return this;
    }

    @NotNull
    public SlashCommandBuilder addSubcommands(
            @NotNull Collection<? extends SubcommandData> subcommands) {
        this.subcommands.addAll(subcommands);
        return this;
    }

    @NotNull
    public SlashCommandBuilder addSubcommandGroups(@NotNull List<SubcommandGroupData> groups) {
        this.subcommandGroups.addAll(groups);
        return this;
    }

    @NotNull
    public SlashCommandBuilder addSubcommandGroups(
            @NotNull Collection<? extends SubcommandGroupData> groups) {
        this.subcommandGroups.addAll(groups);
        return this;
    }

    public SlashCommand build() {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        if (options.isEmpty()) {
            options = List.of();
        }

        if (subcommands.isEmpty()) {
            subcommands = List.of();
        }

        if (subcommandGroups.isEmpty()) {
            subcommandGroups = List.of();
        }

        var cm = Commands.slash(name, description)
            .addOptions(options)
            .addSubcommands(subcommands)
            .addSubcommandGroups(subcommandGroups);

        return new SlashCommand(cm);
    }
}
