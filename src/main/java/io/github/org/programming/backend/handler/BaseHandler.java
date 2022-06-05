package io.github.org.programming.backend.handler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

public abstract class BaseHandler extends ListenerAdapter {
    /**
     *
     * @return used to set the bot owner id.
     */
    protected abstract long botOwnerId();


}
