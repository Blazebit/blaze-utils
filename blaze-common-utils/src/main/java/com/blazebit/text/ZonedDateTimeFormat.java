/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class ZonedDateTimeFormat extends AbstractDateTimeFormatterFormat {

    private static final long serialVersionUID = 1L;
    private static final Class<?> ZONED_DATE_TIME_CLASS;

    static {
        try {
            ZONED_DATE_TIME_CLASS = Class.forName("java.time.ZonedDateTime");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ZonedDateTimeFormat() {
        super(ZONED_DATE_TIME_CLASS, "zonedDateTimeFormatter", "ISO_ZONED_DATE_TIME");
    }
}
