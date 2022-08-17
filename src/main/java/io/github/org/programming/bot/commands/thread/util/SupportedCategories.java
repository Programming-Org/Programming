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
package io.github.org.programming.bot.commands.thread.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.org.programming.bot.commands.thread.AskCommand.categoryChoices;

public class SupportedCategories {
    public SupportedCategories() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static List<String> categoryChoicesString = List.of("java", "c++", "c#", "python", "js",
            "php", "c", "go", "rust", "swift", "kotlin", "scala", "ts", "other");

    public static @NotNull String messageToSend() {
        StringBuilder sb = new StringBuilder();
        sb.append("***Active questions***:").append("\n");
        categoryChoices.forEach(category -> {
            sb.append("**").append(category).append("**: ").append("\n");
        });
        return sb.toString();
    }
}
