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
package io.github.org.programming.database.tag;

import java.util.Map;

import static io.github.org.programming.bot.ProgrammingBot.getContext;
import static io.github.org.programming.jooq.Tables.TAG;

public class TagDatabase {
    public static void createTag(String tagId, String tagName, String tagDescription) {
        getContext().insertInto(TAG, TAG.ID, TAG.NAME, TAG.DESCRIPTION)
            .values(tagId, tagName, tagDescription)
            .execute();
    }

    public static String getName(String tagId) {
        return getContext().select(TAG.NAME).from(TAG).where(TAG.ID.eq(tagId)).fetchOne(TAG.NAME);
    }

    public static String getDescription(String tagId) {
        return getContext().select(TAG.DESCRIPTION)
            .from(TAG)
            .where(TAG.ID.eq(tagId))
            .fetchOne(TAG.DESCRIPTION);
    }

    public static void editTagName(String tagId, String tagName) {
        getContext().update(TAG).set(TAG.NAME, tagName).where(TAG.ID.eq(tagId)).execute();
    }

    public static void editTagDescription(String tagId, String tagDescription) {
        getContext().update(TAG)
            .set(TAG.DESCRIPTION, tagDescription)
            .where(TAG.ID.eq(tagId))
            .execute();
    }

    public static boolean checkIfTagIdExists(String tagId) {
        return getContext().select(TAG.ID)
            .from(TAG)
            .where(TAG.ID.eq(tagId))
            .fetchOne(TAG.ID) != null;
    }

    public static void deleteTag(String tagId) {
        getContext().deleteFrom(TAG).where(TAG.ID.eq(tagId)).execute();
    }

    public static Map<String, String> getTags() {
        return getContext().select(TAG.ID, TAG.NAME).from(TAG).fetchMap(TAG.ID, TAG.NAME);
    }
}
