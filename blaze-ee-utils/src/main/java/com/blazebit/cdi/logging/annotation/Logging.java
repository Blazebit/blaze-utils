/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.logging.annotation;

import com.blazebit.cdi.logging.LoggingInterceptor;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a marker annotation for logging method invocation information and the
 * annotation for the CDI interceptor binding.
 * <p>
 * For further information look at {@link LoggingInterceptor}
 *
 * @author Christian Beikov
 * @see LoggingInterceptor
 * @since 0.1.2
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Logging {
}
