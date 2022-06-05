package io.github.org.programming.backend.handler;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class BaseHandler extends ListenerAdapter {
    /**
     *
     * @return used to set the bot owner id.
     */
    protected abstract long botOwnerId();


}
