package io.github.org.programming.backend.builder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;


public class SlashCommand {
    private final SlashCommandData commandData;
    private Permission[] userPerms = null;
    private Permission[] botPerms = null;
    private boolean isGuildOnly = false;
    private boolean isOwnerOnly = false;

    protected SlashCommand(final SlashCommandData commandData) {
        this.commandData = commandData;
        // if null then it is a normal command
    }

    public SlashCommandData getSlashCommandData() {
        return commandData;
    }

    public SlashCommand setBotPerms(final Permission... perms) {
        this.botPerms = perms;
        return this;
    }

    public SlashCommand setUserPerms(final Permission... perms) {
        this.userPerms = perms;
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
}
