package io.github.org.programming.backend.handler;

import io.github.org.programming.backend.extension.SlashCommandExtender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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
     *        The boolean {@link SlashCommandExtender#isGuildOnly()} ()} ()} is used to determine
     *        whether the command should be global or guild only. determines whether the command
     *        should be Global or Guild only.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void addSlashCommand(@NotNull SlashCommandExtender command) {
        jda.addEventListener(command);
        slashCommand.put(command.getSlashCommandData().getName(), command);
        if (command.isGuildOnly()) {
            guildCommandsData.addCommands(command.getSlashCommandData());
        } else {
            globalCommandsData.addCommands(command.getSlashCommandData());
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

        if (cmd.isOwnerOnly() && event.getUser().getIdLong() != botOwnerId()) {
            event.reply("You do not have permission to use this command.")
                .setEphemeral(true)
                .queue();
            return;
        }

        if (Objects.requireNonNull(event.getMember()).hasPermission(cmd.getUserPerms())
                || Objects.requireNonNull(event.getGuild())
                    .getSelfMember()
                    .hasPermission(cmd.getBotPerms())) {
            cmd.onSlashCommandInteraction(event);
        } else {
            event.reply("You do not have permission to use this command.")
                .setEphemeral(true)
                .queue();
        }
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        final SlashCommandExtender cmd = slashCommand.get(event.getComponentId());
        cmd.onButtonClick(event);
    }

    @Override
    public void onSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event) {
        final SlashCommandExtender cmd = slashCommand.get(event.getComponentId());
        cmd.onSelectMenu(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(
            @Nonnull CommandAutoCompleteInteractionEvent event) {
        final SlashCommandExtender cmd = slashCommand.get(event.getName());
        cmd.onCommandAutoComplete(event);
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        final SlashCommandExtender cmd = slashCommand.get(event.getModalId());
        cmd.onModalInteraction(event);
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
