package io.github.org.programming.backendv1.dashboard;

import io.github.org.programming.backendv1.type.GuildType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public abstract @interface DashboardGetter {
    /**
     * How this works: Make a DashboardSetter and make properties for it. Then you
     * make a DashboardGetter and set the id to the id of the previously created
     * DashboardSetter
     *
     * @return The id of the Getter
     */
    public String value();

    /**
     * The guildid type required to get a information, will return unauthorized when
     * not allowed to
     */
    public GuildType guildTypeRequired() default GuildType.NORMAL;

    public boolean nullable() default false;
}
