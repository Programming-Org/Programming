package io.github.org.programming.database.thread;

import java.time.Instant;
import java.util.List;

import static io.github.org.programming.bot.ProgrammingBot.getContext;
import static io.github.org.programming.jooq.Tables.ASKTHREAD;

public class AskDatabase {

    public static void updateAskDatabase(String memberId, String guildId) {
        getContext()
            .insertInto(ASKTHREAD, ASKTHREAD.GUILD_ID, ASKTHREAD.MEMBER_ID, ASKTHREAD.TIME_STAMP, ASKTHREAD.AMOUNT)
            .values(guildId, memberId, Instant.now(), +1)
            .execute();
    }

    public static void deleteAskDatabaseWithTime(Instant time, String guildId) {
        //find all the member in that guild with time and delete them
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
