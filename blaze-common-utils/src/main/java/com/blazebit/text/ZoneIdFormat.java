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
public class ZoneIdFormat extends AbstractFormat {
    private static final long serialVersionUID = 1L;
    private static final Class<?> ZONE_ID_CLASS;
    private static final Method PARSE_METHOD;

    static {
        try {
            ZONE_ID_CLASS = Class.forName("java.time.ZoneId");
            PARSE_METHOD = ZONE_ID_CLASS.getMethod("of", String.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ZoneIdFormat() {
        super(ZONE_ID_CLASS);
    }

    public Serializable parse(String value, ParserContext context) {
        try {
            return (Serializable) PARSE_METHOD.invoke(null, value);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
