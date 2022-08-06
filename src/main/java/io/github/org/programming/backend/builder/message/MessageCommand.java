package io.github.org.programming.backend.builder.message;

import io.github.org.programming.backend.type.CommandType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;
import java.util.List;

public class MessageCommand {
    private final CommandData commandData;
    private final List<Permission> userPerms = new ArrayList<>();
    private final List<Permission> botPerms = new ArrayList<>();
    private boolean isGuildOnly = false;
    private boolean isOwnerOnly = false;
    private CommandType commandType;

    public MessageCommand(CommandData commandData) {
        this.commandData = commandData;
    }

    public CommandData getCommandData() {
        return commandData;
    }

    public MessageCommand setBotPerms(final Permission... perms) {
        this.botPerms.addAll(List.of(perms));
        return this;
    }

    public MessageCommand setUserPerms(final Permission... perms) {
        this.userPerms.addAll(List.of(perms));
        return this;
    }

    public MessageCommand setToOwnerOnly() {
        this.isOwnerOnly = true;
        return this;
    }

    public MessageCommand setToGuildOnly() {
        this.isGuildOnly = true;
        return this;
    }

    public MessageCommand setCommandType(final CommandType commandType) {
        this.commandType = commandType;
        return this;
    }

    public List<Permission> getBotPerms() {
        return botPerms;
    }

    public List<Permission> getUserPerms() {
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
