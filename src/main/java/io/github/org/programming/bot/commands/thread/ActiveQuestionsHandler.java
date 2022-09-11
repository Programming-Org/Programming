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
package io.github.org.programming.bot.commands.thread;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadChannel;
import org.jetbrains.annotations.Nullable;

import static io.github.org.programming.bot.ProgrammingBot.getContext;
import static io.github.org.programming.jooq.Tables.ACTIVEQUESTIONMESSAGE;

public class ActiveQuestionsHandler {
    private static Message message;

    public static void updateActiveQuestions(ThreadChannel channel, AskThreadStatus status,
            String category) {

        String rawContent = message.getContentRaw();
        String channelLink = "<#" + channel.getId() + ">";
        if (status.equals(AskThreadStatus.OPEN)) {

            // were the specific category is find it add the channel under the category

            if (rawContent.contains(channelLink)) {
                return;
            }

            // it like this
            // **Java**:
            // \n
            // \n
            // get tha part were Java is. replace second line with the new channel link
            String[] lines = rawContent.split("\n");
            StringBuilder newContent = new StringBuilder();
            String categoryCapitalised =
                    category.substring(0, 1).toUpperCase() + category.substring(1);
            for (String line : lines) {
                if (line.contains(categoryCapitalised)) {
                    newContent.append(line).append("\n").append(format(channelLink)).append("\n");
                } else {
                    newContent.append(line).append("\n");
                }
            }
            message.editMessage(newContent.toString()).queue();
        } else {
            message.editMessage(rawContent.replace(channelLink, "\n")).queue(null, e -> {
                throw new RuntimeException(e);
            });
        }
    }

    public static void editActiveQuestionThreadCategory(ThreadChannel channel, String newCategory) {
        String rawContent = message.getContentRaw();
        String channelLink = "<#" + channel.getId() + ">";

        // find channel link and move it to the new category

        String[] lines = rawContent.split("\n");
        StringBuilder newContent = new StringBuilder();


        // need to remove the link from the old category
        for (String line : lines) {
            if (line.contains(channelLink)) {
                newContent.append(line).append("\n");
            }
        }

        // add the link to the new category
        String categoryCapitalised =
                newCategory.substring(0, 1).toUpperCase() + newCategory.substring(1);


        for (String line : lines) {
            if (line.contains(categoryCapitalised)) {
                newContent.append(line).append("\n").append(format(channelLink)).append("\n");
            } else {
                newContent.append(line).append("\n");
            }
        }

        message.editMessage(newContent.toString()).queue();
    }

    private static String format(String channelLink) {
        // add a bullet point before the link
        return "• " + channelLink;
    }

    public static void updateActiveQuestionMessage(String guildId, String messageId) {
        getContext()
            .insertInto(ACTIVEQUESTIONMESSAGE, ACTIVEQUESTIONMESSAGE.GUILD_ID,
                    ACTIVEQUESTIONMESSAGE.MESSAGE_ID)
            .values(guildId, messageId)
            .execute();
    }

    public static @Nullable String getActiveQuestionMessage(String guildId) {
        return getContext().select(ACTIVEQUESTIONMESSAGE.MESSAGE_ID)
            .from(ACTIVEQUESTIONMESSAGE)
            .where(ACTIVEQUESTIONMESSAGE.GUILD_ID.eq(guildId))
            .fetchOne(ACTIVEQUESTIONMESSAGE.MESSAGE_ID);
    }

    public static void deleteActiveQuestionMessageId(String guildId, String messageId) {
        getContext().deleteFrom(ACTIVEQUESTIONMESSAGE)
            .where(ACTIVEQUESTIONMESSAGE.GUILD_ID.eq(guildId))
            .and(ACTIVEQUESTIONMESSAGE.MESSAGE_ID.eq(messageId))
            .execute();
    }

    public static void setMessage(Message message) {
        ActiveQuestionsHandler.message = message;
    }
}
