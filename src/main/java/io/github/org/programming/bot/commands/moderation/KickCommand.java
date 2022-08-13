package io.github.org.programming.bot.commands.moderation;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.builder.slash.SlashCommandBuilder;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import io.github.org.programming.bot.config.BotConfig;
import io.github.org.programming.database.moderation.ModerationDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class KickCommand implements SlashCommandExtender {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getOption("user", OptionMapping::getAsMember);
        Member moderator = event.getMember();
        String reason = event.getOption("reason", OptionMapping::getAsString);

        int id = ModerationDatabase.updateModerationDataBase(event.getGuild().getId(),
                member.getId(), moderator.getId(), reason, "kick");


        User user = Objects
            .requireNonNull(event.getGuild().getMemberById(member.getId()), "Member not found")
            .getUser();

        user.openPrivateChannel()
            .flatMap(channel -> channel.sendMessageEmbeds(kickEmbed(moderator, reason, id)))
            .flatMap(message -> event.getGuild()
                .getChannelById(TextChannel.class, BotConfig.getAuditLogChannelId())
                .sendMessageEmbeds(kickEmbed(moderator, reason, id)))
            .flatMap(message -> member.kick(reason))
            .flatMap(message -> event.reply("Kicked " + member.getAsMention() + " for " + reason))
            .queue();
    }

    private @NotNull MessageEmbed kickEmbed(@NotNull Member moderator, @NotNull String reason,
            int caseId) {
        return new EmbedBuilder().setTitle("You have been kicked from the server")
            .setDescription(
                    "You have been kicked by " + moderator.getAsMention() + " for " + reason)
            .setFooter("Your case number is " + caseId, null)
            .setColor(0xFF0000)
            .build();
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("kick", "Kick a user from the server")
            .addOption(OptionType.USER, "user", "The user to kick", true)
            .addOption(OptionType.STRING, "reason", "The reason for the kick", true)
            .build()
            .setToGuildOnly()
            .setBotPerms(Permission.KICK_MEMBERS)
            .setUserPerms(Permission.KICK_MEMBERS);
    }
}