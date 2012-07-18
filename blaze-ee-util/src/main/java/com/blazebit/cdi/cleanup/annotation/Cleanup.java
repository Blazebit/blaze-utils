/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.cleanup.annotation;

import com.blazebit.annotation.constraint.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is a marker for methods that may do cleanup work.
 *
 * For further information look at {@link CleanupHandlerInterceptor}
 *
 * @author Christian Beikov
 * @since 0.1.2
 * @see CleanupHandlerInterceptor
 * @see CleanupHandler
 * @see CleanupHandling
 */
@ExceptionConstraint(errorMessage = "No exceptions are allowed to be in the throws clause for cleanup methods")
@ParameterConstraint(errorMessage = "Cleanup methods must not have parameters")
@ReturnTypeConstraint(expectedReturnType = void.class, errorMessage = "Cleanup methods must have void return type!")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Cleanup {

    /**
     * The class wide unique name for a cleanup method which can be invoked to
     * do cleanup work.
     * 
     * @return The class wide unique name for a cleanup method
     */
    @UniqueValueConstraint(scope = ConstraintScope.CLASS, errorMessage = "There must not be other cleanup methods with the same name")
    Class<?> value();
}
