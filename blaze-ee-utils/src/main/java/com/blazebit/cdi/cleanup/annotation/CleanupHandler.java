/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.cleanup.annotation;

import com.blazebit.apt.validation.constraint.ConstraintScope;
import com.blazebit.apt.validation.constraint.ReferenceValueConstraint;
import com.blazebit.cdi.cleanup.CleanupHandlerInterceptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

/**
 * This is an annotation for marking methods to invoke a cleanup methods if
 * exceptions occur.
 * 
 * For further information look at {@link CleanupHandlerInterceptor}
 * 
 * @author Christian Beikov
 * @since 0.1.2
 * @see CleanupHandlerInterceptor
 * @see CleanupHandling
 * @see Cleanup
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface CleanupHandler {

	/**
	 * The cleanup handlings that should be considered when intercepting a
	 * method.
	 * 
	 * @return The cleanup handlings that should be considered when intercepting
	 *         a method.
	 */
	@Nonbinding
	CleanupHandling[] value() default {};

	/**
	 * Specifies a valid cleanup name of a method annotated with the cleanup
	 * annotation.
	 * 
	 * @return The name of the cleanup method which should be invoked.
	 */
	@ReferenceValueConstraint(referencedAnnotationClass = Cleanup.class, nullable = true, errorMessage = "There is no method annotated with the cleanup annotation that has the specified name", scope = ConstraintScope.CLASS)
	@Nonbinding
	Class<?> cleanup();
}
