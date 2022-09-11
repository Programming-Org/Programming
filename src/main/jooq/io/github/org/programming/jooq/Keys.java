/*
 * This file is generated by jOOQ.
 */
package io.github.org.programming.jooq;


import io.github.org.programming.jooq.tables.Moderation;
import io.github.org.programming.jooq.tables.Tempban;
import io.github.org.programming.jooq.tables.records.ModerationRecord;
import io.github.org.programming.jooq.tables.records.TempbanRecord;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<ModerationRecord> MODERATION_PKEY = Internal.createUniqueKey(Moderation.MODERATION, DSL.name("moderation_pkey"), new TableField[] { Moderation.MODERATION.ID }, true);
    public static final UniqueKey<TempbanRecord> TEMPBAN_PKEY = Internal.createUniqueKey(Tempban.TEMPBAN, DSL.name("tempban_pkey"), new TableField[] { Tempban.TEMPBAN.ID }, true);
}
