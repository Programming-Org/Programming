package io.github.org.programming.backendv1.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.org.programming.backendv1.builder.SlashCommand;
import io.github.org.programming.backendv1.dashboard.Dashboard;
import io.github.org.programming.backendv1.extension.SlashCommandExtender;
import io.github.org.programming.backendv1.type.CommandType;
import io.github.org.programming.backendv1.util.JsonUtil;
import io.github.org.programming.backendv1.util.Pair;
import io.github.org.programming.bot.ProgrammingBot;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * For register the commands make sure to set it to awaitReady as seen here
 * <p>
 * The is class which process the registration of the commands. <br>
 * <br>
 * This handler only needs to be registered once and this makes sure all the commands are
 * registered.
 * <p>
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
    private static final Map<CommandType, List<SlashCommandExtender>> commandsInCategory = new EnumMap<>(CommandType.class);
    private static final Map<String, Pair<Long, SlashCommandExtender>> commands = new HashMap<>();

    private static final Map<Class<? extends SlashCommandExtender>, SlashCommandExtender> commandInstances = new HashMap<>();
    private static ObjectNode commandsJson;

    private static final ExecutorService commandPool = Executors.newCachedThreadPool();

    private static int numberOfCommands = 0;

    public static Map<CommandType, List<SlashCommandExtender>> getCommandsInCategory() {
        return commandsInCategory;
    }

    public static Map<String, Pair<Long, SlashCommandExtender>> getCommands() {
        return commands;
    }

    public static JsonNode getCommandsJson() {
        return commandsJson;
    }

    public static Integer ownerId;

    public static void addSlashCommands() {
        numberOfCommands = 0;
        commands.clear();
        commandsInCategory.clear();
        commandInstances.clear();
        commandsJson = JsonNodeFactory.instance.objectNode();
        final ArrayNode commandsArray = JsonNodeFactory.instance.arrayNode();
        final Reflections reflections = new Reflections(SlashCommandExtender.class.getPackage().getName());
        final Set<Class<? extends SlashCommand>> annotated = reflections.getSubTypesOf(SlashCommand.class);

        for (final Class<?> command : annotated) {
            try {
                final SlashCommandExtender newInstance = (SlashCommandExtender) command.getConstructor().newInstance();
                final String[] packageName = command.getPackage().getName().split("\\.");
                final CommandType parseCommandType = CommandType.parse(packageName[packageName.length - 1]);
                newInstance.setCommandType(parseCommandType != null ? parseCommandType : newInstance.getCommandType());

                SlashCommandData data = newInstance.getSlashCommandData();

                final ObjectNode commandNode = JsonNodeFactory.instance.objectNode()
                        .put("name", data.getName())
                        .put("description", data.getDescription())
                        .set("permissions", newInstance.userPermsToArrayNode());
                commandNode.set("options", JsonUtil.optionsToJson(data.getOptions()));

                ArrayNode groupJson = JsonNodeFactory.instance.arrayNode();

                for (SubcommandGroupData subcommandGroupData : data.getSubcommandGroups()) {
                    final ObjectNode subcommandGroupJson = JsonNodeFactory.instance.objectNode()
                            .put("name", subcommandGroupData.getName())
                            .put("description", subcommandGroupData.getDescription());

                    final ArrayNode subcommandJson = JsonNodeFactory.instance.arrayNode();
                    for (SubcommandData subcommandData : subcommandGroupData.getSubcommands()) {
                        ObjectNode subcommandObjJson = JsonNodeFactory.instance.objectNode()
                                .put("name", subcommandData.getName())
                                .put("description", subcommandData.getDescription());
                        subcommandObjJson.set("options", JsonUtil.optionsToJson(subcommandData.getOptions()));
                        subcommandJson.add(subcommandObjJson);
                    }
                    subcommandGroupJson.set("subcommands", subcommandJson);
                    groupJson.add(subcommandGroupJson);
                }

                commandNode.set("subcommand_groups", groupJson);

                ObjectNode subcommandJson = JsonNodeFactory.instance.objectNode();
                for (SubcommandData subcommandData : data.getSubcommands()) {
                    subcommandJson.put(subcommandData.getName(), JsonNodeFactory.instance.objectNode()
                            .put("name", subcommandData.getName())
                            .put("description", subcommandData.getDescription())
                            .set("options", JsonUtil.optionsToJson(subcommandData.getOptions())));
                }

                commandNode.set("subcommands", subcommandJson);

                commandsArray.add(commandNode);

                addSlashCommand(newInstance);
            } catch (final Exception e) {
                logger.error("Failed to add command", e);
                System.exit(-1);
            }
        }

        commandsJson.set("commands", commandsArray);

        ProgrammingBot.getInstance().getExecutor().submit(Dashboard::init);
    }

    public static void addSlashCommand(SlashCommandExtender command) {

    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        final SlashCommandExtender cmd = commands.get(event.getName()).getValue();

        if (ownerId != null && cmd.isOwnerOnly() && event.getUser().getIdLong() != ownerId) {
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
        final SlashCommandExtender cmd = commands.get(event.getComponentId()).getValue();
        cmd.onButtonClick(event);
    }

    @Override
    public void onSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event) {
        final SlashCommandExtender cmd = commands.get(event.getComponentId()).getValue();
        cmd.onSelectMenu(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(
            @Nonnull CommandAutoCompleteInteractionEvent event) {
        final SlashCommandExtender cmd = commands.get(event.getName()).getValue();
        cmd.onCommandAutoComplete(event);
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        final SlashCommandExtender cmd = commands.get(event.getModalId()).getValue();
        cmd.onModalInteraction(event);
    }

    public static void setOwnerId(@NotNull Integer ownerId) {
        SlashCommandHandler.ownerId = ownerId;
    }
}

