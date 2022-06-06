package io.github.org.programming.backend.builder.slash;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class SlashCommandBuilder {
    private final String name;
    private final String description;
    private OptionData[] options = null;
    private SubcommandData[] subcommands = null;
    private SubcommandGroupData[] subcommandGroups = null;

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
        return subcommands == null ? List.of() : List.of(subcommands);
    }

    @NotNull
    public List<SubcommandGroupData> getSubcommandGroups() {
        return subcommandGroups == null ? List.of() : List.of(subcommandGroups);
    }

    @NotNull
    public List<OptionData> getOptions() {
        return options == null ? List.of() : List.of(options);
    }

    public SlashCommandBuilder addOptions(@Nonnull OptionData... options) {
        this.options = options;
        return this;
    }

    public SlashCommandBuilder addOptions(@Nonnull Collection<? extends OptionData> options) {
        this.options = options.toArray(new OptionData[0]);
        return this;
    }

    public SlashCommandBuilder addOption(@Nonnull OptionType type, @Nonnull String name,
            @Nonnull String description, boolean required, boolean autoComplete) {
        return addOptions(new OptionData(type, name, description).setRequired(required)
            .setAutoComplete(autoComplete));
    }

    @NotNull
    public SlashCommandBuilder addOption(@NotNull OptionType type, @NotNull String name,
            @NotNull String description, boolean required) {
        return addOption(type, name, description, required);
    }

    @NotNull
    public SlashCommandBuilder addOption(@NotNull OptionType type, @NotNull String name,
            @NotNull String description) {
        return addOption(type, name, description);
    }

    @NotNull
    public SlashCommandBuilder addSubcommands(@NotNull SubcommandData... subcommands) {
        this.subcommands = subcommands;
        return this;
    }

    @NotNull
    public SlashCommandBuilder addSubcommands(
            @NotNull Collection<? extends SubcommandData> subcommands) {
        this.subcommands = subcommands.toArray(new SubcommandData[0]);
        return this;
    }

    @NotNull
    public SlashCommandBuilder addSubcommandGroups(@NotNull SubcommandGroupData... groups) {
        this.subcommandGroups = groups;
        return this;
    }

    @NotNull
    public SlashCommandBuilder addSubcommandGroups(
            @NotNull Collection<? extends SubcommandGroupData> groups) {
        this.subcommandGroups = groups.toArray(new SubcommandGroupData[0]);
        return this;
    }

    public SlashCommand build() {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        if (options == null) {
            options = new OptionData[0];
        }

        if (subcommands == null) {
            subcommands = new SubcommandData[0];
        }

        if (subcommandGroups == null) {
            subcommandGroups = new SubcommandGroupData[0];
        }

        var cm = Commands.slash(name, description)
            .addOptions(options)
            .addSubcommands(subcommands)
            .addSubcommandGroups(subcommandGroups);

        return new SlashCommand(cm);
    }
}
