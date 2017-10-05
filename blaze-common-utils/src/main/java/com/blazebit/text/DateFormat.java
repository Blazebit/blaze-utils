/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.text.ParseException;
import java.util.Date;

/**
 * @author Christian Beikov
 * @since 0.1.2
 */
public class DateFormat extends AbstractFormat<Date> {

    private static final long serialVersionUID = 1L;

    public DateFormat() {
        super(Date.class);
    }

    @SuppressWarnings("unused")
    public Date parse(String value, ParserContext context) {
        Object o = null;

        if (context != null) {
            context.getAttribute("format");

            if (o != null && !(o instanceof java.text.DateFormat)) {
                throw new IllegalArgumentException(
                        "Illegal format object in context");
            }
        }

        if (o == null) {
            o = java.text.DateFormat.getDateTimeInstance();
        }
        try {
            return ((java.text.DateFormat) o).parse(value);
        } catch (ParseException ex) {
            return null;
        }
    }

    @Override
    public String format(Date value, ParserContext context) {
        Object o = null;

        if (context != null) {
            o = context.getAttribute("format");

            if (o != null && !(o instanceof java.text.DateFormat)) {
                throw new IllegalArgumentException(
                        "Illegal format object in context");
            }
        }

        if (o == null) {
            o = java.text.DateFormat.getDateTimeInstance();
        }

        return ((java.text.DateFormat) o).format(value);
    }
}
