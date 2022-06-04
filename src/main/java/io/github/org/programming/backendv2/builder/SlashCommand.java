package io.github.org.programming.backendv2.builder;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.github.org.programming.backendv2.type.CommandType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;


public class SlashCommand {
    private final SlashCommandData commandData;
    private Permission[] userPerms = null;
    private Permission[] botPerms = null;
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

    public SlashCommand setCommandType(final CommandType commandType) {
        this.commandType = commandType;
        return this;
    }

    public Permission[] getBotPerms() {
        return botPerms;
    }

    public Permission[] getUserPerms() {
        return userPerms;
    }

    public ArrayNode userPermsToArrayNode() {
        //change it from Permission[] to ArrayNode
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Permission perm : userPerms) {
            arrayNode.add(perm.getName());
        }
        return arrayNode;
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
