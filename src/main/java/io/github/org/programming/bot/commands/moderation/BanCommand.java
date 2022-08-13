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
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

public class BanCommand implements SlashCommandExtender {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        User user = event.getOption("user", OptionMapping::getAsUser);
        int delDays = event.getOption("days", OptionMapping::getAsInt);
        Member moderator = event.getMember();
        String reason = event.getOption("reason", OptionMapping::getAsString);

        int id = ModerationDatabase.updateModerationDataBase(event.getGuild().getId(), user.getId(),
                moderator.getId(), reason, "ban");

        user.openPrivateChannel()
            .flatMap(channel -> channel.sendMessageEmbeds(banEmbed(moderator, reason, id)))
            .flatMap(message -> event.getGuild()
                .getChannelById(TextChannel.class, BotConfig.getAuditLogChannelId())
                .sendMessageEmbeds(banEmbed(moderator, reason, id)))
            .flatMap(message -> event.getGuild().ban(user, delDays, reason))
            .flatMap(message -> event.reply("Banned " + user.getAsMention() + " for " + reason))
            .queue();
    }

    private @NotNull MessageEmbed banEmbed(@NotNull Member moderator, @NotNull String reason,
            int caseId) {
        return new EmbedBuilder().setTitle("You have been banned from the server")
            .setDescription(
                    "You have been banned by " + moderator.getAsMention() + " for " + reason)
            .setFooter("Your case number is " + caseId, null)
            .setColor(0xFF0000)
            .build();
    }

    @Override
    public SlashCommand build() {
        return new SlashCommandBuilder("ban", "Used to ban a user")
            .addOption(OptionType.USER, "user", "The user to ban", true)
            .addOptions(new OptionData(OptionType.NUMBER, "deldays",
                    "The amount of days of message history you want to delete", true)
                        .setRequiredRange(0, 8))
            .addOption(OptionType.STRING, "reason", "The reason for banning the user", true)
            .build()
            .setBotPerms(Permission.BAN_MEMBERS)
            .setUserPerms(Permission.BAN_MEMBERS);
    }
}