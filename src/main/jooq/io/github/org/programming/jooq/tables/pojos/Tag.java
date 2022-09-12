/*
 * This file is generated by jOOQ.
 */
package io.github.org.programming.jooq.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String description;

    public Tag(Tag value) {
        this.id = value.id;
        this.name = value.name;
        this.description = value.description;
    }

    public Tag(
        String id,
        String name,
        String description
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Getter for <code>public.tag.id</code>.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Getter for <code>public.tag.name</code>.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for <code>public.tag.description</code>.
     */
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Tag (");

        sb.append(id);
        sb.append(", ").append(name);
        sb.append(", ").append(description);

        sb.append(")");
        return sb.toString();
    }
}
