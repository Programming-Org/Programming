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

import io.github.org.programming.bot.commands.moderation.util.ModerationType;
import org.jetbrains.annotations.NotNull;
import org.jooq.types.DayToSecond;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static io.github.org.programming.bot.ProgrammingBot.getContext;
import static io.github.org.programming.jooq.tables.Moderation.MODERATION;
import static io.github.org.programming.jooq.tables.Tempban.TEMPBAN;

public class ModerationDatabase {

    public static @Nullable Integer updateModerationDataBase(String guildId, String userId,
            String moderationId, String reason, ModerationType moderationType) {
        if (moderationType.equals(ModerationType.WARN)) {
            return getContext()
                .insertInto(MODERATION, MODERATION.GUILD_ID, MODERATION.USER_ID,
                        MODERATION.MODERATOR_ID, MODERATION.TIME_STAMP, MODERATION.REASON,
                        MODERATION.AMOUNT_OF_WARNINGS, MODERATION.TYPE)
                .values(guildId, userId, moderationId, Instant.now(), reason, 1,
                        moderationType.getType())
                .returning(MODERATION.ID)
                .fetchOne(MODERATION.ID);
        } else {
            return getContext()
                .insertInto(MODERATION, MODERATION.GUILD_ID, MODERATION.USER_ID,
                        MODERATION.MODERATOR_ID, MODERATION.TIME_STAMP, MODERATION.REASON,
                        MODERATION.TYPE)
                .values(guildId, userId, moderationId, Instant.now(), reason,
                        moderationType.getType())
                .returning(MODERATION.ID)
                .fetchOne(MODERATION.ID);
        }
    }

    public static Integer getAmountOfWarns(String userId, String guildId) {
        return getContext().select(MODERATION.AMOUNT_OF_WARNINGS)
            .from(MODERATION)
            .where(MODERATION.USER_ID.eq(userId))
            .and(MODERATION.GUILD_ID.eq(guildId))
            .fetch() // add the results together
            .stream()
            .mapToInt(row -> row.get(MODERATION.AMOUNT_OF_WARNINGS))
            .sum();
    }

    public static void removeWarning(String userId, String guildId) {
        getContext().deleteFrom(MODERATION)
            .where(MODERATION.USER_ID.eq(userId))
            .and(MODERATION.GUILD_ID.eq(guildId))
            .and(MODERATION.AMOUNT_OF_WARNINGS.eq(1))
            .execute();
    }

    public static Map<String, String> getUserIdAndReason(int caseId) {
        return getContext().select(MODERATION.USER_ID, MODERATION.REASON)
            .from(MODERATION)
            .where(MODERATION.ID.eq(caseId))
            .fetchMap(MODERATION.USER_ID, MODERATION.REASON);
    }

    public static Map<String, String> getTypeAndModeratorId(int caseId) {
        return getContext().select(MODERATION.TYPE, MODERATION.MODERATOR_ID)
            .from(MODERATION)
            .where(MODERATION.ID.eq(caseId))
            .fetchMap(MODERATION.TYPE, MODERATION.MODERATOR_ID);
    }

    public static Instant getTimeStamp(int caseId) {
        return getContext().select(MODERATION.TIME_STAMP)
            .from(MODERATION)
            .where(MODERATION.ID.eq(caseId))
            .fetchOne(MODERATION.TIME_STAMP);
    }

    public static Integer getAmountOfWarnings(int caseId) {
        return getContext().select(MODERATION.AMOUNT_OF_WARNINGS)
            .from(MODERATION)
            .where(MODERATION.ID.eq(caseId))
            .fetchOne(MODERATION.AMOUNT_OF_WARNINGS);
    }


    public static void removeCase(int caseId) {
        getContext().deleteFrom(MODERATION).where(MODERATION.ID.eq(caseId)).execute();
    }

    // temp ban
    public static @Nullable Integer updateTempBanDatabase(String guildId, String userId,
            String moderatorId, String reason, Duration duration) {
        return getContext()
            .insertInto(TEMPBAN, TEMPBAN.GUILD_ID, TEMPBAN.USER_ID, TEMPBAN.MODERATOR_ID,
                    TEMPBAN.START, TEMPBAN.REASON, TEMPBAN.DURATION)
            .values(guildId, userId, moderatorId, Instant.now(), reason,
                    DayToSecond.valueOf(duration))
            .returning(MODERATION.ID)
            .fetchOne(MODERATION.ID);
    }

    public static @NotNull List<String> getTempBanUsers(Duration duration, String guildId) {
        return getContext().select(TEMPBAN.USER_ID)
            .from(TEMPBAN)
            .where(TEMPBAN.DURATION.greaterThan(DayToSecond.valueOf(duration)))
            .and(TEMPBAN.GUILD_ID.eq(guildId))
            .fetch(TEMPBAN.USER_ID);
    }
}
