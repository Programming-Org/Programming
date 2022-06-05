package io.github.org.programming.backend.builder.user;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class UserCommandBuilder {
    private final String name;

    /**
     * Creates a new UserCommandBuilder
     * 
     * @param name The name of the user command
     */
    public UserCommandBuilder(String name) {
        this.name = name;
    }

    public @NotNull String getName() {
        return name;
    }


    public UserCommand build() {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        var cd = Commands.user(name);

        return new UserCommand(cd);
    }
}
