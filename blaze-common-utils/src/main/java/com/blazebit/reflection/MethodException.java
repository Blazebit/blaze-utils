/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @author Christian Beikov
 */
public class MethodException {

    private Constructor<?> constructor;
    private Method method;
    private int index;

    MethodException(Method method, int index) {
        if (method == null) {
            throw new NullPointerException("Method must not be null");
        }

        this.method = method;
        this.index = index;
    }

    MethodException(Constructor<?> constructor, int index) {
        if (constructor == null) {
            throw new NullPointerException("Method must not be null");
        }

        this.constructor = constructor;
        this.index = index;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public int getIndex() {
        return index;
    }

    public Class<?> getType() {
        return method != null ? method.getExceptionTypes()[index] : constructor
                .getExceptionTypes()[index];
    }

    public Class<?> getResolvedType(Class<?> concreteClass) {
        Type t = method != null ? method.getGenericExceptionTypes()[index]
                : constructor.getGenericExceptionTypes()[index];

        if (t instanceof TypeVariable<?>) {
            return ReflectionUtils.resolveTypeVariable(concreteClass,
                    (TypeVariable<?>) t);
        }

        return getType();
    }

    public Class<?>[] getResolvedTypeParameters(Class<?> concreteClass) {
        Type t = method != null ? method.getGenericExceptionTypes()[index]
                : constructor.getGenericExceptionTypes()[index];
        return ReflectionUtils.resolveTypeArguments(concreteClass, t);
    }

    public Method getMethod() {
        return method;
    }
}
