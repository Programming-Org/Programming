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
package io.github.org.programming.bot.config;

import io.github.realyusufismail.jconfig.util.JConfigUtils;
import org.jetbrains.annotations.NotNull;

public class BotConfig {
    public static String getToken() {
        return JConfigUtils.getString("TOKEN");
    }

    public static long getOwnerId() {
        return JConfigUtils.getLong("OWNER_ID");
    }

    public static long getGuildId() {
        return JConfigUtils.getLong("GUILD_ID");
    }

    public static long getAuditLogChannelId() {
        return JConfigUtils.getLong("AUDIT_LOG_CHANNEL_ID");
    }

    public static @NotNull Integer getCorePoolSize() {
        return JConfigUtils.getInt("CORE_POOL_SIZE", 1);
    }

    public static long getHelpChannelId() {
        return JConfigUtils.getLong("HELP_CHANNEL_ID");
    }

    public static long getActiveQuestionChannelId() {
        return JConfigUtils.getLong("ACTIVE_QUESTION_CHANNEL_ID");
    }

    public static int getAskLimit() {
        return JConfigUtils.getInt("ASK_LIMIT", 2);
    }

    public static long getUserAmountChannelId() {
        return JConfigUtils.getLong("USER_AMOUNT_CHANNEL_ID");
    }
}
