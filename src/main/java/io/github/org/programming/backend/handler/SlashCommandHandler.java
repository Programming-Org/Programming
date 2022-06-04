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
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * For register the commands make sure to set it to awaitReady as seen here
 * <p>
 * The is class which process the registration of the commands. <br>
 * <br>
 * This handler only needs to be registered once and this makes sure all the commands are
 * registered.
 *
 * To do this you need to add the following to your Main class: <br>
 * <br>
 * 
 * <pre>
 *     {@code
 *     public static void main(String[] args) {
 *     //The JDA builder. this is just a demo.
 *     JDA builder = JDABuilder.createDefault("").build();
 *          //The handler.
 *          //You can add your own guild id.
 *          SlashCommandHandler handler = new SlashCommandHandler(builder, builder.getGuildById(""), TheOwnerIdAsLong);
 *          builder.addEventListener(handler);
 *          handler.addSlashCommand();
 *          handler.queueSlashCommand();
 *      }
 *      }
 * </pre>
 */
public class SlashCommandHandler extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SlashCommandHandler.class);
    private final Map<String, SlashCommandExtender> slashCommand = new HashMap<>();

    /**
     * Used to determine whether the commands should be global or guild only.
     */
    private final @NotNull CommandListUpdateAction globalCommandsData;
    private final @NotNull CommandListUpdateAction guildCommandsData;
    private final @NotNull JDA jda;

    private final long ownerId;

    public SlashCommandHandler(@NotNull JDA jda, @NotNull Guild guild, long ownerId) {
        globalCommandsData = jda.updateCommands();
        guildCommandsData = guild.updateCommands();
        this.jda = jda;
        this.ownerId = ownerId;
    }

    public void addSlashCommands() {
        final Reflections reflections =
                new Reflections(SlashCommandExtender.class.getPackage().getName());
        final Collection<Class<? extends SlashCommandExtender>> annotated =
                reflections.getSubTypesOf(SlashCommandExtender.class);
        for (final Class<?> command : annotated) {
            try {
                final SlashCommandExtender newSlashCommand =
                        (SlashCommandExtender) command.getConstructor().newInstance();
                queueSlashCommand(newSlashCommand);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addSlashCommand(SlashCommandExtender command) {
        slashCommand.put(command.getSlashCommandData().getName(), command);
        if (command.isGuildOnly()) {
            guildCommandsData.addCommands(command.getSlashCommandData());
        } else {
            globalCommandsData.addCommands(command.getSlashCommandData());
        }
    }

    private void queueSlashCommand(SlashCommandExtender command) {
        addSlashCommand(command);
        onFinishedRegistration();
    }

    /**
     * Queues the command after the command has been registered.
     */
    private void onFinishedRegistration() {
        globalCommandsData.queue();
        guildCommandsData.queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        final SlashCommandExtender cmd = slashCommand.get(event.getName());

        if (cmd.isOwnerOnly() && event.getUser().getIdLong() != ownerId) {
            event.getChannel()
                .sendMessage("You do not have permission to use this command.")
                .queue();
            return;
        }

        if (Objects.requireNonNull(event.getMember()).hasPermission(cmd.getUserPerms())
                || Objects.requireNonNull(event.getGuild())
                    .getSelfMember()
                    .hasPermission(cmd.getBotPerms())) {
            cmd.onSlashCommandInteraction(event);
        } else {
            event.getChannel()
                .sendMessage("You do not have permission to use this command.")
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
}

