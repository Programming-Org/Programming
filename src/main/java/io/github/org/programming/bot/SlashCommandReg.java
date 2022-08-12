package io.github.org.programming.bot;

import io.github.org.programming.backend.extension.SlashCommandExtender;
import io.github.org.programming.backend.handler.SlashCommandHandler;
import io.github.org.programming.bot.commands.ExampleCommand;
import io.github.org.programming.bot.commands.moderation.BanCommand;
import io.github.org.programming.bot.commands.moderation.KickCommand;
import io.github.org.programming.bot.commands.moderation.UnBanCommand;
import io.github.org.programming.bot.config.BotConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SlashCommandReg extends SlashCommandHandler {
    protected SlashCommandReg(@NotNull JDA jda, @NotNull Guild guild) {
        super(jda, guild);

        List<SlashCommandExtender> extenders = new ArrayList<>();

        extenders.add(new ExampleCommand());
        extenders.add(new KickCommand());
        extenders.add(new BanCommand());
        extenders.add(new UnBanCommand());

        queueAndRegisterSlashCommands(extenders);
    }

    @Override
    protected long botOwnerId() {
        return BotConfig.getOwnerId();
    }
}
