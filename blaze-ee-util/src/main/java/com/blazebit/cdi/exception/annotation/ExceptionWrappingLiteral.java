/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.exception.annotation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import javax.enterprise.util.AnnotationLiteral;

/**
 * Literal for annotation, that can be used for extensions.
 *
 * @author Christian Beikov
 * @since 0.1.2
 * @see ExceptionWrapping
 */
public class ExceptionWrappingLiteral extends AnnotationLiteral<ExceptionWrapping> implements ExceptionWrapping {
    

    private static final long serialVersionUID = 1L;
    private ExceptionWrap[] value;

    public ExceptionWrappingLiteral() {
        this(new ExceptionWrap[0]);
    }

    public ExceptionWrappingLiteral(ExceptionWrap[] value) {
        this.value = value;
    }

    @Override
    public ExceptionWrap[] value() {
        return value == null ? null : Arrays.copyOf(value, value.length);
    }
}
