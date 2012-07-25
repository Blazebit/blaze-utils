/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.cleanup.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;

import com.blazebit.annotation.constraint.ConstraintScope;
import com.blazebit.annotation.constraint.ReferenceValueConstraint;
import com.blazebit.cdi.cleanup.CleanupHandlerInterceptor;

/**
 * This is annotation is used within #{@link CleanupHandler} and declares
 * methods to invoke a cleanup methods if the specified exception occurs, or if
 * always is true then the cleanups are executed after every method invocation.
 * 
 * For further information look at {@link CleanupHandlerInterceptor}
 * 
 * @author Christian Beikov
 * @since 0.1.2
 * @see CleanupHandlerInterceptor
 * @see CleanupHandler
 * @see Cleanup
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface CleanupHandling {

	/**
	 * The exception type on which, when occurs, the cleanup method with the
	 * specified cleanup name should be invoked.
	 * 
	 * @return The exception type on which the cleanup should be invoked
	 */
	@Nonbinding
	Class<? extends Throwable> exception() default java.lang.Exception.class;

	/**
	 * Specifies a valid cleanup name of a method annotated with the cleanup
	 * annotation.
	 * 
	 * @return The name of the cleanup method which should be invoked.
	 */
	@ReferenceValueConstraint(referencedAnnotationClass = Cleanup.class, nullable = true, errorMessage = "There is no method annotated with the cleanup annotation that has the specified name", scope = ConstraintScope.CLASS)
	@Nonbinding
	Class<?> cleanup();

	/**
	 * If set to true, the cleanup method with the specified name is always
	 * invoked.
	 * 
	 * @return If true, the cleanup method is always invoke, otherwise only when
	 *         the specified exception occured.
	 */
	@Nonbinding
	boolean always() default false;
}
