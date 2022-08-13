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
package io.github.org.programming.backend.builder.user;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class UserCommandBuilder {
    private final String name;

    /**
     * Creates a new UserCommandBuilder
     * 
     * @param name The name of the user command
     */
    public UserCommandBuilder(String name) {
        this.name = name;
    }

    public @NotNull String getName() {
        return name;
    }


    public UserCommand build() {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        var cd = Commands.user(name);

        return new UserCommand(cd);
    }
}
