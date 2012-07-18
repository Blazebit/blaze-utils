/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.exception.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;

import com.blazebit.cdi.exception.ExceptionWrappingInterceptor;

/**
 * This annotation is used within ExceptionWrapping annotation and declares the
 * wrapping of source annotations to wrapper annotations.
 * 
 * For further information look at {@link ExceptionWrappingInterceptor}
 * 
 * @author Christian Beikov
 * @since 0.1.2
 * @see ExceptionWrappingInterceptor
 * @see ExceptionWrapping
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ExceptionWrap {
	/**
	 * Exception types that should be wrapped.
	 * 
	 * @return Exception types that should be wrapped
	 */
	@Nonbinding
	Class<? extends Throwable>[] sources() default { java.lang.Throwable.class };

	/**
	 * The wrapper type into which the given source types should be wrapped to.
	 * 
	 * @return The wrapper type into which the given source types should be
	 *         wrapped to
	 */
	@Nonbinding
	Class<? extends Exception> wrapper() default java.lang.Exception.class;
}
