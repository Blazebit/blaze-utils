/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.transaction.annotation;

import javax.enterprise.util.AnnotationLiteral;

/**
 * Literal for annotation, that can be used for extensions.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 * @see Transactional
 */
public class TransactionalLiteral extends AnnotationLiteral<Transactional> implements Transactional {
    private static final long serialVersionUID = 1L;
    private boolean requiresNew;

    public TransactionalLiteral() {
        this(false);
    }

    public TransactionalLiteral(boolean requiresNew) {
        this.requiresNew = requiresNew;
    }

    @Override
    public boolean requiresNew() {
        return requiresNew;
    }
    
}
