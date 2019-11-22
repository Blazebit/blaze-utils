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
public class PeriodFormat extends AbstractFormat {
    private static final long serialVersionUID = 1L;
    private static final Class<?> PERIOD_CLASS;
    private static final Method PARSE_METHOD;

    static {
        try {
            PERIOD_CLASS = Class.forName("java.time.Period");
            PARSE_METHOD = PERIOD_CLASS.getMethod("parse", CharSequence.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PeriodFormat() {
        super(PERIOD_CLASS);
    }

    public Serializable parse(String value, ParserContext context) {
        try {
            return (Serializable) PARSE_METHOD.invoke(null, value);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
