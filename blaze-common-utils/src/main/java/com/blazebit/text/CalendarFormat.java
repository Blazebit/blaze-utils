/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Christian Beikov
 * @since 0.1.2
 */
public class CalendarFormat extends AbstractFormat<Calendar> {

    private static final long serialVersionUID = 1L;

    public CalendarFormat() {
        super(Calendar.class);
    }

    @SuppressWarnings("unused")
    public Calendar parse(String value, ParserContext context) throws ParseException {
        Calendar calendar = Calendar.getInstance();
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

        calendar.setTime(((java.text.DateFormat) o).parse(value));
        return calendar;
    }

    @Override
    public String format(Calendar value, ParserContext context) {
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
