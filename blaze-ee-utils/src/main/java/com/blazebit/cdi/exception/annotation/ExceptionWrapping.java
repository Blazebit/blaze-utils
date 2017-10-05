/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.exception.annotation;

import com.blazebit.cdi.exception.ExceptionWrappingInterceptor;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a basic container annotation for ExceptionWrap annotations and the
 * annotation for the CDI interceptor binding.
 * <p>
 * For further information look at {@link ExceptionWrappingInterceptor}
 *
 * @author Christian Beikov
 * @see ExceptionWrappingInterceptor
 * @see ExceptionWrap
 * @since 0.1.2
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ExceptionWrapping {

    /**
     * The exception wraps that should be considered when intercepting a method.
     *
     * @return The exception wraps that should be considered when intercepting a
     * method.
     */
    @Nonbinding
    ExceptionWrap[] value();
}
