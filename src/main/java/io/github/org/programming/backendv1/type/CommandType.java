package io.github.org.programming.backendv1.type;

import org.jetbrains.annotations.Nullable;

public enum CommandType {
    /**
     * This command is a moderation command.
     */
    MODERATION,
    /**
     * This command is a fun command.
     */
    FUN,
    /**
     * This command is a utility command.
     */
    UTILITY,
    /**
     * This command is a music command.
     */
    MUSIC,
    /**
     * This command is owner only.
     */
    OWNER_ONLY,
    /**
     * This command is used to provide info.
     */
    INFO,
    /**
     * This command is used to provide support
     */
    SUPPORT,
    /**
     * This command is used to set up something
     */
    SETUP,
    /**
     * This command is under development.
     */
    DEVELOPMENT,
    /**
     * This command is an example command.
     */
    EXAMPLE,
    /**
     * A normal command.
     */
    NORMAL,
    /**
     * No yet determined.
     */
    UNKNOWN;

    public static @Nullable CommandType parse(final String input) {
        try {
            return valueOf(input.toUpperCase());
        } catch (final Exception e) {
            return null;
        }
    }
}