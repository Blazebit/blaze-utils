/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.transaction.annotation;

import com.blazebit.annotation.constraint.BooleanValueConstraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

/**
 * This is an annotation for marking methods to be executed within a
 * transaction.
 *
 * For further information look at {@link TransactionalInterceptor}
 *
 * @author Christian Beikov
 * @since 0.1.2
 * @see TransactionalInterceptor
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Transactional {

    /**
     * Specifies that the intercepted method needs to run in a new transaction
     * and may not just join a parent transaction.
     * This feature is not yet implemented because JTA does not provide support
     * for nested transations.
     * 
     * @return If true the intercepted method requires a new transaction, otherwise it may join a parent transaction
     */
    @BooleanValueConstraint(expectedValue = false, errorMessage = "The requiresNew attribute is not yet implemented")
    @Nonbinding
    public boolean requiresNew() default false;
}
