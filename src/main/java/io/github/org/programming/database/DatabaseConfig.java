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
package io.github.org.programming.database;

import io.github.realyusufismail.jconfig.JConfigUtils;

public class DatabaseConfig {
    private static final JConfigUtils.Companion CONFIG = JConfigUtils.Companion;

    public static String getDatabaseUser() {
        return CONFIG.getString("DB_USER");
    }

    public static String getDatabasePassword() {
        return CONFIG.getString("DB_PASSWORD");
    }

    public static String getDatabaseName() {
        return CONFIG.getString("DATABASE_NAME");
    }

    public static int getPortNumber() {
        return CONFIG.getInt("PORT_NUMBER");
    }

    public static String getServerName() {
        return CONFIG.getString("SERVER_NAME");
    }
}
