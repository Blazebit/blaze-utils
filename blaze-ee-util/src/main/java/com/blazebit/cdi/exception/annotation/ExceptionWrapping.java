/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.exception.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

import com.blazebit.cdi.exception.ExceptionWrappingInterceptor;

/**
 * This is a basic container annotation for ExceptionWrap annotations and the
 * annotation for the CDI interceptor binding.
 * 
 * For further information look at {@link ExceptionWrappingInterceptor}
 * 
 * @author Christian Beikov
 * @since 0.1.2
 * @see ExceptionWrappingInterceptor
 * @see ExceptionWrap
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface ExceptionWrapping {

	/**
	 * The exception wraps that should be considered when intercepting a method.
	 * 
	 * @return The exception wraps that should be considered when intercepting a
	 *         method.
	 */
	@Nonbinding
	ExceptionWrap[] value();
}
