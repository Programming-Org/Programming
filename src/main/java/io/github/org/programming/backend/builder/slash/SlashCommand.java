package io.github.org.programming.backend.builder.slash;

import io.github.org.programming.backend.type.CommandType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.List;


public class SlashCommand {
    private final SlashCommandData commandData;
    private final List<Permission> userPerms = new ArrayList<>();
    private final List<Permission> botPerms = new ArrayList<>();
    private boolean isGuildOnly = false;
    private boolean isOwnerOnly = false;
    private CommandType commandType;

    protected SlashCommand(final SlashCommandData commandData) {
        this.commandData = commandData;
    }

    public SlashCommandData getSlashCommandData() {
        return commandData;
    }

    public SlashCommand setBotPerms(final Permission... perms) {
        this.botPerms.addAll(List.of(perms));
        return this;
    }

    public SlashCommand setUserPerms(final Permission... perms) {
        this.userPerms.addAll(List.of(perms));
        return this;
    }

    public SlashCommand setToOwnerOnly() {
        this.isOwnerOnly = true;
        return this;
    }

    public SlashCommand setToGuildOnly() {
        this.isGuildOnly = true;
        return this;
    }

    public SlashCommand setCommandType(final CommandType commandType) {
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
