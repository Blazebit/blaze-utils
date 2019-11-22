/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class TimestampFormat extends AbstractFormat<Timestamp> {

    private static final long serialVersionUID = 1L;

    public TimestampFormat() {
        super(Timestamp.class);
    }

    @SuppressWarnings("unused")
    public Timestamp parse(String value, ParserContext context) throws ParseException {
        Object o = null;

        if (context != null) {
            o = context.getAttribute("format");

            if (o != null && !(o instanceof java.text.DateFormat)) {
                throw new IllegalArgumentException(
                        "Illegal format object in context");
            }
        }

        if (o == null) {
            o = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }

        return new Timestamp(((java.text.DateFormat) o).parse(value).getTime());
    }

    @Override
    public String format(Timestamp value, ParserContext context) {
        Object o = null;

        if (context != null) {
            o = context.getAttribute("format");

            if (o != null && !(o instanceof java.text.DateFormat)) {
                throw new IllegalArgumentException(
                        "Illegal format object in context");
            }
        }

        if (o == null) {
            o = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }

        return ((java.text.DateFormat) o).format(value.getTime());
    }
}
