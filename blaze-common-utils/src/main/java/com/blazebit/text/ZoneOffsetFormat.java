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
public class ZoneOffsetFormat extends AbstractFormat {
    private static final long serialVersionUID = 1L;
    private static final Class<?> ZONE_OFFSET_CLASS;
    private static final Method PARSE_METHOD;

    static {
        try {
            ZONE_OFFSET_CLASS = Class.forName("java.time.ZoneOffset");
            PARSE_METHOD = ZONE_OFFSET_CLASS.getMethod("of", String.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ZoneOffsetFormat() {
        super(ZONE_OFFSET_CLASS);
    }

    public Serializable parse(String value, ParserContext context) {
        try {
            return (Serializable) PARSE_METHOD.invoke(null, value);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
