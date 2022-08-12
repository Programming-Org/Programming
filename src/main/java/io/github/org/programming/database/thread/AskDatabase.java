package io.github.org.programming.database.thread;

import io.github.org.programming.jooq.tables.Askthread;

import java.time.Instant;

import static io.github.org.programming.bot.ProgrammingBot.getContext;
import static io.github.org.programming.jooq.Tables.ASKTHREAD;

public class AskDatabase {

    public static void updateAskDatabase(String memberId, String guildId) {
        getContext()
            .insertInto(ASKTHREAD, ASKTHREAD.GUILD_ID, ASKTHREAD.MEMBER_ID, ASKTHREAD.TIME_STAMP, ASKTHREAD.AMOUNT)
            .values(guildId, memberId, Instant.now(), +1)
            .execute();
    }

    public static void deleteAskDatabase(String memberId, String guildId) {
        getContext().deleteFrom(ASKTHREAD)
            .where(ASKTHREAD.GUILD_ID.eq(guildId).and(ASKTHREAD.MEMBER_ID.eq(memberId)))
            .execute();
    }

    public static int getAskAmount(String memberId, String guildId) {
        return getContext().select(ASKTHREAD.AMOUNT)
            .from(ASKTHREAD)
            .where(ASKTHREAD.GUILD_ID.eq(guildId).and(ASKTHREAD.MEMBER_ID.eq(memberId)))
            .fetchOne(ASKTHREAD.AMOUNT);

    }
}
