/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class YearFormat extends AbstractFormat {
    private static final long serialVersionUID = 1L;
    private static final Class<?> YEAR_CLASS;
    private static final Method PARSE_METHOD;

    static {
        try {
            YEAR_CLASS = Class.forName("java.time.Year");
            PARSE_METHOD = YEAR_CLASS.getMethod("parse", CharSequence.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public YearFormat() {
        super(YEAR_CLASS);
    }

    public Serializable parse(String value, ParserContext context) {
        try {
            return (Serializable) PARSE_METHOD.invoke(null, value);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
