/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.logging.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.interceptor.InterceptorBinding;

/**
 * This is a marker annotation for logging method invocation information and the
 * annotation for the CDI interceptor binding.
 * 
 * For further information look at {@link LoggingInterceptor}
 * 
 * @author Christian Beikov
 * @since 0.1.2
 * @see LoggingInterceptor
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Logging {
}
