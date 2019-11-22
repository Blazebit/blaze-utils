/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class LocalDateFormat extends AbstractDateTimeFormatterFormat {

    private static final long serialVersionUID = 1L;
    private static final Class<?> LOCAL_DATE_CLASS;

    static {
        try {
            LOCAL_DATE_CLASS = Class.forName("java.time.LocalDate");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LocalDateFormat() {
        super(LOCAL_DATE_CLASS, "localDateFormatter", "ISO_LOCAL_DATE");
    }
}
