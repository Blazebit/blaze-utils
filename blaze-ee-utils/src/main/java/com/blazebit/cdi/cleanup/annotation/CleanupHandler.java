/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.cleanup.annotation;

import com.blazebit.apt.validation.constraint.ConstraintScope;
import com.blazebit.apt.validation.constraint.ReferenceValueConstraint;
import com.blazebit.cdi.cleanup.CleanupHandlerInterceptor;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is an annotation for marking methods to invoke a cleanup methods if
 * exceptions occur.
 * <p>
 * For further information look at {@link CleanupHandlerInterceptor}
 *
 * @author Christian Beikov
 * @see CleanupHandlerInterceptor
 * @see CleanupHandling
 * @see Cleanup
 * @since 0.1.2
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CleanupHandler {

    /**
     * The cleanup handlings that should be considered when intercepting a
     * method.
     *
     * @return The cleanup handlings that should be considered when intercepting
     * a method.
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
