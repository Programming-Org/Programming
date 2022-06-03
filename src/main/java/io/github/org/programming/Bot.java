package io.github.org.programming;

import io.github.org.programming.bot.ProgrammingBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Bot {
    public static void main(String[] args) throws Exception {
        new ProgrammingBot(args);
    }
}
