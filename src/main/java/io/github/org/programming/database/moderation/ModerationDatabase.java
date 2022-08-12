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
