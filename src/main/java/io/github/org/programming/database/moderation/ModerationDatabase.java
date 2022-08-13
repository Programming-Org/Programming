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
package io.github.org.programming.database.moderation;

import javax.annotation.Nullable;
import java.time.Instant;

import static io.github.org.programming.bot.ProgrammingBot.getContext;
import static io.github.org.programming.jooq.tables.Moderation.MODERATION;

public class ModerationDatabase {

    public static @Nullable Integer updateModerationDataBase(String guildId, String userId,
            String moderationId, String reason, String moderationType) {
        return getContext()
            .insertInto(MODERATION, MODERATION.GUILD_ID, MODERATION.USER_ID,
                    MODERATION.MODERATOR_ID, MODERATION.TIME_STAMP, MODERATION.REASON)
            .values(guildId, userId, moderationId, Instant.now(), reason)
            .returning(MODERATION.ID)
            .fetchOne(MODERATION.ID);
    }

}
