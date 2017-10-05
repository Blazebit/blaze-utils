/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.exception.annotation;

import com.blazebit.apt.validation.constraint.ConstraintScope;
import com.blazebit.apt.validation.constraint.ReferenceValueConstraint;
import com.blazebit.cdi.cleanup.annotation.Cleanup;
import org.apache.deltaspike.core.api.exception.control.ExceptionHandler;

import javax.enterprise.util.Nonbinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used within ExceptionHandler annotation and declares the
 * handling of exceptions for a given type.
 * <p>
 * For further information look at {@link ExceptionHandlerInterceptor}
 *
 * @author Christian Beikov
 * @see ExceptionHandlerInterceptor
 * @see ExceptionHandler
 * @since 0.1.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface CatchHandling {
    /**
     * The exception which should be handeled by this exception handling. Every
     * exception that is instanceof the given exception type, will be handeled
     * by this handling.
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
