package io.github.org.programming.backendv1.dashboard;

import io.github.org.programming.backendv1.wrapper.ProgrammingWrapper;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Dashboard {

    public static final HashMap<String, Method> setters = new HashMap<>();
    private static final List<Method> nullableSetters = new ArrayList<>();
    public static final HashMap<String, Method> getters = new HashMap<>();

    public static void init() {
        getters.clear();
        setters.clear();

        final Reflections reflections = new Reflections(ProgrammingWrapper.class.getPackage().getName());
        final Set<Class<? extends ProgrammingWrapper>> annotated = reflections.getSubTypesOf(ProgrammingWrapper.class);
        for (final Class<?> programmingObject : annotated) {
            try {
                final Class<?> objectClass = Class.forName(programmingObject.getName());

                for (final Method method : objectClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(DashboardSetter.class)) {
                        final DashboardSetter annotation = method.getAnnotation(DashboardSetter.class);
                        setters.put(annotation.value(), method);
                        if (annotation.nullable()) {
                            nullableSetters.add(method);
                        }
                    }
                    if (method.isAnnotationPresent(DashboardGetter.class)) {
                        final DashboardGetter annotation = method.getAnnotation(DashboardGetter.class);
                        getters.put(annotation.value(), method);
                    }
                }
            } catch (final ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
