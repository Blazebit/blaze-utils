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

import com.blazebit.apt.validation.constraint.ConstraintScope;
import com.blazebit.apt.validation.constraint.ReferenceValueConstraint;
import com.blazebit.cdi.cleanup.annotation.Cleanup;

/**
 * This is a basic container annotation for ExceptionHandling annotations and
 * the annotation for the CDI interceptor binding.
 * 
 * For further information look at {@link ExceptionHandlerInterceptor}
 * 
 * @author Christian Beikov
 * @since 0.1.2
 * @see ExceptionHandlerInterceptor
 * @see ExceptionHandling
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface CatchHandler {

	/**
	 * The exception handlings that should be considered when intercepting a
	 * method.
	 * 
	 * @return The exception wraps that should be considered when intercepting a
	 *         method.
	 */
	@Nonbinding
	CatchHandling[] value() default {};

	/**
	 * The exception types which should be unwrapped when occurring.
	 * 
	 * @return The types of exceptions which should be unwrapped.
	 */
	@Nonbinding
	Class<? extends Throwable>[] unwrap() default {};

	/**
	 * The exception which should be handeled by this exception handler by
	 * default if no exception handling exists, that would handle an exception
	 * that occured. Every exception that is instanceof the given exception
	 * type, will be handeled by this handler.
	 * 
	 * @return The type of the exception which should be handeled.
	 */
	@Nonbinding
	Class<? extends Throwable> exception() default java.lang.Exception.class;

	/**
	 * The name of a cleanup method that should be invoked when an exception is
	 * handeled by the interceptor.
	 * 
	 * @return The name of the cleanup method.
	 */
	@ReferenceValueConstraint(referencedAnnotationClass = Cleanup.class, nullable = true, scope = ConstraintScope.CLASS, errorMessage = "The given name for a cleanup can not be found within class scope!")
	@Nonbinding
	Class<?> cleanup() default Object.class;
}
