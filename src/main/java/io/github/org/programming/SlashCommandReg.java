package io.github.org.programming;

import io.github.org.programming.backend.handler.SlashCommandHandler;
import io.github.org.programming.config.BotConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;


public class SlashCommandReg extends SlashCommandHandler {
    protected SlashCommandReg(@NotNull JDA jda, @NotNull Guild guild) {
        super(jda, guild);


    }

    @Override
    protected long botOwnerId() {
        return BotConfig.getOwnerId();
    }
}
