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
package io.github.org.programming.bot.commands.moderation.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModerationUtil {

    public static @NotNull MessageEmbed sendMessageToAuditLog(@NotNull User user, String action,
            @NotNull Member moderator, int caseId, String reason) {
        return sendMessageToAuditLog(user, action, moderator, caseId, reason,
                new MessageEmbed.Field[0]);
    }

    public static @NotNull MessageEmbed sendMessageToAuditLog(@NotNull User user, String action,
            @NotNull Member moderator, int caseId, String reason,
            @Nullable MessageEmbed.Field... fields) {
        var embed = new EmbedBuilder().setTitle(user.getAsMention() + " has been " + action)
            .setDescription(user.getAsMention() + " has been " + action + " by "
                    + moderator.getEffectiveName() + " for " + reason)
            .addField("Case id", String.valueOf(caseId), false)
            .setColor(0xFF0000);

        if (fields.length > 1) {
            for (var field : fields) {
                embed.addField(field);
            }
        }

        return embed.build();
    }
}
