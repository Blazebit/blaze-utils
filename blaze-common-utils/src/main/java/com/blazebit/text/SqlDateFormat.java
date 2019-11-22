/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class SqlDateFormat extends AbstractFormat<Date> {

    private static final long serialVersionUID = 1L;

    public SqlDateFormat() {
        super(Date.class);
    }

    @SuppressWarnings("unused")
    public Date parse(String value, ParserContext context) throws ParseException {
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

        return new Date(((java.text.DateFormat) o).parse(value).getTime());
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
            o = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }

        return ((java.text.DateFormat) o).format(value.getTime());
    }
}
