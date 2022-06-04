package io.github.org.programming.backend.handler;

import io.github.org.programming.backend.builder.SlashCommand;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    private static final Map<String, Pair<Long, SlashCommandExtender>> commands = new HashMap<>();

    // private final Map<String, SlashCommandExtender> slashCommand = new HashMap<>();


    private static final Map<Class<? extends SlashCommandExtender>, SlashCommand> commandInstances =
            new HashMap<>();


    private static int numberOfCommands = 0;

    private CommandListUpdateAction commandListUpdateAction;


    /**
     * Used to determine whether the commands should be global or guild only.
     */
    private final @NotNull CommandListUpdateAction globalCommandsData;
    private final @NotNull CommandListUpdateAction guildCommandsData;
    private final @NotNull JDA jda;
    private final long ownerId;

    /**
     * Creates a new SlashCommandHandler
     *
     * @param jda The JDA instance. Also used to register global commands.
     * @param guild The guild instance. Also used to register guild commands.
     * @param ownerId The owner id.
     */
    public SlashCommandHandler(@NotNull JDA jda, @NotNull Guild guild, long ownerId) {
        globalCommandsData = jda.updateCommands();
        guildCommandsData = guild.updateCommands();
        this.jda = jda;
        this.ownerId = ownerId;
    }

    public void addSlashCommand() {
        numberOfCommands = 0;
        commands.clear();
        commandInstances.clear();
        final Reflections reflections =
                new Reflections(SlashCommandExtender.class.getPackage().getName());
        final Set<Class<? extends SlashCommandExtender>> annotated =
                reflections.getSubTypesOf(SlashCommandExtender.class);
        for (final Class<?> command : annotated) {
            try {
                final SlashCommandExtender newSlashCommand =
                        (SlashCommandExtender) command.getConstructor().newInstance();

                SlashCommandData data = newSlashCommand.getSlashCommandData();
                jda.addEventListener(newSlashCommand);

                commandListUpdateAction =
                        newSlashCommand.isGuildOnly() ? guildCommandsData.addCommands(data)
                                : globalCommandsData.addCommands(data);
            } catch (Exception e) {
                logger.error("Failed to add command {}", command.getName(), e);
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        final SlashCommandExtender cmd = commands.get(event.getName()).getRight();

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
        final SlashCommandExtender cmd = commands.get(event.getComponentId()).getRight();
        cmd.onButtonClick(event);
    }

    @Override
    public void onSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event) {
        final SlashCommandExtender cmd = commands.get(event.getComponentId()).getRight();
        cmd.onSelectMenu(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(
            @Nonnull CommandAutoCompleteInteractionEvent event) {
        final SlashCommandExtender cmd = commands.get(event.getName()).getRight();
        cmd.onCommandAutoComplete(event);
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        final SlashCommandExtender cmd = commands.get(event.getModalId()).getRight();
        cmd.onModalInteraction(event);
    }


    public void queueSlashCommand() throws InterruptedException {
        // if it is null then try again
        if (commandListUpdateAction != null) {
            commandListUpdateAction.queue();
        } else {
            wait(1000);
            queueSlashCommand();
        }
    }
}

