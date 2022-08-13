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
package io.github.org.programming.database.thread;

import java.time.Instant;
import java.util.List;

import static io.github.org.programming.bot.ProgrammingBot.getContext;
import static io.github.org.programming.jooq.Tables.ASKTHREAD;

public class AskDatabase {

    public static void updateAskDatabase(String memberId, String guildId) {
        getContext()
            .insertInto(ASKTHREAD, ASKTHREAD.GUILD_ID, ASKTHREAD.MEMBER_ID, ASKTHREAD.TIME_STAMP,
                    ASKTHREAD.AMOUNT)
            .values(guildId, memberId, Instant.now(), +1)
            .execute();
    }

    public static void deleteAskDatabaseWithTime(Instant time, String guildId) {
        // find all the member in that guild with time and delete them
        getContext().deleteFrom(ASKTHREAD)
            .where(ASKTHREAD.GUILD_ID.eq(guildId).and(ASKTHREAD.TIME_STAMP.eq(time)))
            .execute();
    }

    public static Integer getAskAmount(String memberId, String guildId) {
        return getContext().select(ASKTHREAD.AMOUNT)
            .from(ASKTHREAD)
            .where(ASKTHREAD.GUILD_ID.eq(guildId).and(ASKTHREAD.MEMBER_ID.eq(memberId)))
            .fetchOne(ASKTHREAD.AMOUNT);
    }

    public static List<Instant> getAskTimeStamps(String guildId) {
        return getContext().select(ASKTHREAD.TIME_STAMP)
            .from(ASKTHREAD)
            .where(ASKTHREAD.GUILD_ID.eq(guildId))
            .orderBy(ASKTHREAD.TIME_STAMP.desc())
            .limit(2)
            .fetch(ASKTHREAD.TIME_STAMP);
    }
}
