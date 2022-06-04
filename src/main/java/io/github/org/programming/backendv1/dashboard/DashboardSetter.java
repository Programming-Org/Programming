package io.github.org.programming.backendv1.dashboard;

import io.github.org.programming.backendv1.type.GuildType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public abstract @interface DashboardSetter {
    /**
     * @return The ID of the setter
     */
    public String value();

    public boolean nullable() default false;

    public GuildType requiredGuildType() default GuildType.NORMAL;
}
