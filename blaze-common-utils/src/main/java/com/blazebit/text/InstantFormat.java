/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class InstantFormat extends AbstractDateTimeFormatterFormat {

    private static final long serialVersionUID = 1L;
    private static final Class<?> INSTANT_CLASS;

    static {
        try {
            INSTANT_CLASS = Class.forName("java.time.Instant");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InstantFormat() {
        super(INSTANT_CLASS, "instantFormatter", "ISO_INSTANT");
    }
}
