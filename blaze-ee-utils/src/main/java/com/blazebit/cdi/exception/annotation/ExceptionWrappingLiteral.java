/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.exception.annotation;

import javax.enterprise.util.AnnotationLiteral;
import java.util.Arrays;

/**
 * Literal for annotation, that can be used for extensions.
 *
 * @author Christian Beikov
 * @see ExceptionWrapping
 * @since 0.1.2
 */
public class ExceptionWrappingLiteral extends
        AnnotationLiteral<ExceptionWrapping> implements ExceptionWrapping {

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
