package io.github.org.programming.backend.builder.message;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class MessageCommandBuilder {
    private final String name;

    /**
     * Creates a new MessageCommandBuilder
     * @param name The name of the message command
     */
    public MessageCommandBuilder(String name) {
        this.name = name;
    }

    public @NotNull String getName() {
        return name;
    }

    public MessageCommand build() {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        var cd = Commands.message(name);

        return new MessageCommand(cd);
    }
}
