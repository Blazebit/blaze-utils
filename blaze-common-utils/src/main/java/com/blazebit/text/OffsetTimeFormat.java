/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class OffsetTimeFormat extends AbstractDateTimeFormatterFormat {

    private static final long serialVersionUID = 1L;
    private static final Class<?> OFFSET_TIME_CLASS;

    static {
        try {
            OFFSET_TIME_CLASS = Class.forName("java.time.OffsetTime");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public OffsetTimeFormat() {
        super(OFFSET_TIME_CLASS, "offsetTimeFormatter", "ISO_OFFSET_TIME");
    }
}
