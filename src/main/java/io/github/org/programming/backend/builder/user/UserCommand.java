package io.github.org.programming.backend.builder.user;

import io.github.org.programming.backend.type.CommandType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class UserCommand {
    private final CommandData commandData;
    private Permission[] userPerms = null;
    private Permission[] botPerms = null;
    private boolean isGuildOnly = false;
    private boolean isOwnerOnly = false;
    private CommandType commandType;

    public UserCommand(CommandData commandData) {
        this.commandData = commandData;
    }

    public CommandData getCommandData() {
        return commandData;
    }

    public UserCommand setBotPerms(final Permission... perms) {
        this.botPerms = perms;
        return this;
    }

    public UserCommand setUserPerms(final Permission... perms) {
        this.userPerms = perms;
        return this;
    }

    public UserCommand setToOwnerOnly() {
        this.isOwnerOnly = true;
        return this;
    }

    public UserCommand setToGuildOnly() {
        this.isGuildOnly = true;
        return this;
    }

    public UserCommand setCommandType(final CommandType commandType) {
        this.commandType = commandType;
        return this;
    }

    public Permission[] getBotPerms() {
        return botPerms;
    }

    public Permission[] getUserPerms() {
        return userPerms;
    }

    public boolean isGuildOnly() {
        return isGuildOnly;
    }

    public boolean isOwnerOnly() {
        return isOwnerOnly;
    }

    public CommandType getCommandType() {
        return commandType;
    }
}
