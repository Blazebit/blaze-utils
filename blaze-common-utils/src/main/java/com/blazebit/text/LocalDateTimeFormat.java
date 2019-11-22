/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class LocalDateTimeFormat extends AbstractDateTimeFormatterFormat {

    private static final long serialVersionUID = 1L;
    private static final Class<?> LOCAL_DATE_TIME_CLASS;

    static {
        try {
            LOCAL_DATE_TIME_CLASS = Class.forName("java.time.LocalDateTime");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LocalDateTimeFormat() {
        super(LOCAL_DATE_TIME_CLASS, "localDateTimeFormatter", "ISO_LOCAL_DATE_TIME");
    }
}
