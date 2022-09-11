/*
 * Copyright 2022 Programming Org and other Programming Org contributors
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package io.github.org.programming.bot;

import io.github.org.programming.backend.extension.SlashCommandExtender;
import io.github.org.programming.backend.handler.SlashCommandHandler;
import io.github.org.programming.bot.commands.ExampleCommand;
import io.github.org.programming.bot.commands.help.HelpCommand;
import io.github.org.programming.bot.commands.info.BotInfoCommand;
import io.github.org.programming.bot.commands.info.ServerInfoCommand;
import io.github.org.programming.bot.commands.info.UserInfoCommand;
import io.github.org.programming.bot.commands.moderation.*;
import io.github.org.programming.bot.commands.owner.RestartCommand;
import io.github.org.programming.bot.commands.thread.AskCommand;
import io.github.org.programming.bot.commands.thread.CloseAskThread;
import io.github.org.programming.bot.commands.thread.EditAskThread;
import io.github.org.programming.bot.config.BotConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class RegisterSlashCommands extends SlashCommandHandler {
    protected RegisterSlashCommands(@NotNull JDA jda, @NotNull Guild guild) {
        super(jda, guild);

        List<SlashCommandExtender> extenders = new ArrayList<>();

        extenders.add(new ExampleCommand());
        extenders.add(new KickCommand());
        extenders.add(new BanCommand());
        extenders.add(new UnBanCommand());
        extenders.add(new AskCommand());
        extenders.add(new CloseAskThread());
        extenders.add(new EditAskThread());
        extenders.add(new WarnCommand());
        extenders.add(new TempBanCommand());
        extenders.add(new TimeOutCommand());
        extenders.add(new AuditCommand());
        extenders.add(new DeleteMessagesCommand());
        extenders.add(new UserInfoCommand());
        extenders.add(new BotInfoCommand());
        extenders.add(new ServerInfoCommand());
        extenders.add(new RestartCommand());

        // Always keep at the end
        extenders.add(new HelpCommand(extenders));
        queueAndRegisterSlashCommands(extenders);
    }

    @Override
    protected long botOwnerId() {
        return BotConfig.getOwnerId();
    }
}
