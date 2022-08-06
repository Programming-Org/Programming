package io.github.org.programming.bot.commands.moderation;

import io.github.org.programming.backend.builder.slash.SlashCommand;
import io.github.org.programming.backend.extension.SlashCommandExtender;
import io.github.org.programming.bot.config.BotConfig;
import io.github.org.programming.database.moderation.ModerationDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;


public class UnBanCommand implements SlashCommandExtender {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getOption("member", OptionMapping::getAsMember);
        Member moderator = event.getMember();
        String reason = event.getOption("reason", OptionMapping::getAsString);

        int id = ModerationDatabase.updateModerationDataBase(event.getGuild().getId(),
                member.getId(), moderator.getId(), reason, "unban");

        event.getGuild()
            .unban(member.getUser())
            .flatMap(channel -> event.getGuild()
                .getChannelById(TextChannel.class, BotConfig.getAuditLogChannelId())
                .sendMessageEmbeds(unbanEmbed(member, moderator, reason, id)))
            .flatMap(message -> event.reply("Unbanned " + member.getAsMention() + " for " + reason))
            .queue();

    }

    private MessageEmbed unbanEmbed(Member member, @NotNull Member moderator,
            @NotNull String reason, int caseId) {
        return new EmbedBuilder()
            .setTitle("The member + " + member.getAsMention() + " has been unbanned")
            .setDescription("The member has been unbanned by " + moderator.getAsMention() + " for "
                    + reason)
            .setFooter("Your case number is " + caseId, null)
            .setColor(0x00FF00)
            .build();

    }

    @Override
    public SlashCommand build() {
        return null;
    }
}
