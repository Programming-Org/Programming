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
package io.github.org.programming.backend.handler;

import io.github.org.programming.backend.extension.SlashCommandExtender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * For register the commands make sure to set it to awaitReady as seen here
 *
 * <pre>
 * jda.awaitReady()
 *     .addEventListener(new CommandHandler(jda, jda.getGuildById(872494635757473932L)));
 * </pre>
 * <p>
 * The is class which process the registration of the commands. <br>
 * <br>
 * Commands are register by using a List with an example for registering slash commands being
 *
 * <pre>
 *     <code>
 *       List<SlashCommand> handler = new ArrayList<>(); <br>
 *        handler.add(new ExampleCommand()); <br>
 *        queueAndRegisterSlashCommands(handler);
 *        </code>
 * </pre>
 */
public abstract class SlashCommandHandler extends BaseHandler {
    private static final Logger logger = LoggerFactory.getLogger(SlashCommandHandler.class);
    private final Map<String, SlashCommandExtender> slashCommand = new HashMap<>();

    private static final String COMMAND_ERROR = "The command {} is not registered";

    /**
     * Used to determine whether the commands should be global or guild only.
     */
    private final @NotNull CommandListUpdateAction globalCommandsData;
    private final @NotNull CommandListUpdateAction guildCommandsData;
    private final JDA jda;


    protected SlashCommandHandler(@NotNull JDA jda, @NotNull Guild guild) {
        globalCommandsData = jda.updateCommands();
        guildCommandsData = guild.updateCommands();
        this.jda = jda;
    }


    /**
     * Used to register slash commands. when the developer types slashCommand.add(new
     * ExampleCommand());. The addCommand will retrieve the commandData which includes
     * name,description,options,sub commands, etc
     *
     * @param command <br>
     *        The Command class is an interface class which contains all the need methods for the
     *        making of the command. <br>
     *        <br>
     *        The boolean {@link SlashCommandExtender#build()#isGuildOnly()} is used to determine
     *        whether the command should be global or guild only. determines whether the command
     *        should be Global or Guild only.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void addSlashCommand(@NotNull SlashCommandExtender command) {
        BaseHandler.checkIfBuildIsNull(command.build(), command.getClass().getSimpleName());
        jda.addEventListener(command);
        slashCommand.put(command.build().getSlashCommandData().getName(), command);
        if (command.build().isGuildOnly()) {
            guildCommandsData.addCommands(command.build().getSlashCommandData());
        } else {
            globalCommandsData.addCommands(command.build().getSlashCommandData());
        }
    }

    /**
     * Used to register the slash commands.
     *
     * @param slashCommands the slash commands.
     */
    public void queueAndRegisterSlashCommands(
            @NotNull Collection<SlashCommandExtender> slashCommands) {
        slashCommands.forEach(this::addSlashCommand);
        onFinishedRegistration();
    }

    /**
     * Queues the command after the command has been registered.
     */
    private void onFinishedRegistration() {
        globalCommandsData.queue();
        guildCommandsData.queue();
    }


    /**
     * Handles the slash command event.
     *
     * @param event The original slash command event,
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        final SlashCommandExtender cmd = slashCommand.get(event.getName());

        if (cmd.build().isOwnerOnly() && event.getUser().getIdLong() != botOwnerId()) {
            event.reply("You do not have permission to use this command.")
                .setEphemeral(true)
                .queue();
            return;
        }

        // check if does not have user perms and bot perms

        if (!cmd.build().getUserPerms().isEmpty()
                && !event.getMember().hasPermission(cmd.build().getUserPerms())
                && !cmd.build().getBotPerms().isEmpty()
                && !event.getGuild().getSelfMember().hasPermission(cmd.build().getBotPerms())) {
            event.reply("You and the bot do not have permission to use this command.")
                .setEphemeral(true)
                .queue();
            return;
        }

        if (!cmd.build().getUserPerms().isEmpty()
                && !event.getMember().hasPermission(cmd.build().getUserPerms())) {
            event.reply("You do not have permission to use this command.")
                .setEphemeral(true)
                .queue();
        } else if (!cmd.build().getBotPerms().isEmpty()
                && !event.getGuild().getSelfMember().hasPermission(cmd.build().getBotPerms())) {
            event.reply("I do not have permission to use this command.").setEphemeral(true).queue();
        } else {
            cmd.onSlashCommand(event);
        }


    }

    /**
     * Gets slash commands as a list.
     *
     * @return retrieves the commands as a list.
     */
    @NotNull
    public List<SlashCommandExtender> getSlashCommands() {
        return new ArrayList<>(this.slashCommand.values());
    }
}
