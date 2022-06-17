package io.github.org.programming.bot.commands.moderation;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.builder.slash.SlashCommandBuilder;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

public class KickCommand extends SlashCommandExtender {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("kick", "Kick a user from the server")
                .addOption(OptionType.USER, "user", "The user to kick")
                .addOption(OptionType.STRING, "reason", "The reason for the kick")
                .build()
                .setBotPerms(Permission.KICK_MEMBERS)
                .setUserPerms(Permission.KICK_MEMBERS);
    }
}
