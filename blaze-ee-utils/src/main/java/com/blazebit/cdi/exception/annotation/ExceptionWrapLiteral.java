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
 * @see ExceptionWrap
 * @since 0.1.2
 */
public class ExceptionWrapLiteral extends AnnotationLiteral<ExceptionWrap>
        implements ExceptionWrap {

    private static final long serialVersionUID = 1L;
    private Class<? extends Throwable>[] sources;
    private Class<? extends Exception> wrapper;

    @SuppressWarnings("unchecked")
    public ExceptionWrapLiteral() {
        this(new Class[]{java.lang.Throwable.class},
                java.lang.Exception.class);
    }

    public ExceptionWrapLiteral(Class<? extends Throwable>... sources) {
        this(sources, java.lang.Exception.class);
    }

    @SuppressWarnings("unchecked")
    public ExceptionWrapLiteral(Class<? extends Exception> wrapper) {
        this(new Class[]{java.lang.Throwable.class}, wrapper);
    }

    public ExceptionWrapLiteral(Class<? extends Throwable>[] sources,
                                Class<? extends Exception> wrapper) {
        this.sources = sources;
        this.wrapper = wrapper;
    }

    @Override
    public Class<? extends Throwable>[] sources() {
        return sources == null ? null : Arrays.copyOf(sources, sources.length);
    }

    @Override
    public Class<? extends Exception> wrapper() {
        return wrapper;
    }
}
