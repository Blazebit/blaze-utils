/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class LocalTimeFormat extends AbstractDateTimeFormatterFormat {

    private static final long serialVersionUID = 1L;
    private static final Class<?> LOCAL_TIME_CLASS;

    static {
        try {
            LOCAL_TIME_CLASS = Class.forName("java.time.LocalTime");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LocalTimeFormat() {
        super(LOCAL_TIME_CLASS, "localTimeFormatter", "ISO_LOCAL_TIME");
    }
}
