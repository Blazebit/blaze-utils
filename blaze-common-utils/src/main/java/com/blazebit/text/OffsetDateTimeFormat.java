/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class OffsetDateTimeFormat extends AbstractDateTimeFormatterFormat {

    private static final long serialVersionUID = 1L;
    private static final Class<?> OFFSET_DATE_TIME_CLASS;

    static {
        try {
            OFFSET_DATE_TIME_CLASS = Class.forName("java.time.OffsetDateTime");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public OffsetDateTimeFormat() {
        super(OFFSET_DATE_TIME_CLASS, "offsetDateTimeFormatter", "ISO_OFFSET_DATE_TIME");
    }
}
