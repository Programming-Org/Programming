/*
 * This file is generated by jOOQ.
 */
package io.github.org.programming.jooq.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Activequestionmessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String guildId;
    private final String messageId;

    public Activequestionmessage(Activequestionmessage value) {
        this.guildId = value.guildId;
        this.messageId = value.messageId;
    }

    public Activequestionmessage(
        String guildId,
        String messageId
    ) {
        this.guildId = guildId;
        this.messageId = messageId;
    }

    /**
     * Getter for <code>public.activequestionmessage.guild_id</code>.
     */
    public String getGuildId() {
        return this.guildId;
    }

    /**
     * Getter for <code>public.activequestionmessage.message_id</code>.
     */
    public String getMessageId() {
        return this.messageId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Activequestionmessage (");

        sb.append(guildId);
        sb.append(", ").append(messageId);

        sb.append(")");
        return sb.toString();
    }
}