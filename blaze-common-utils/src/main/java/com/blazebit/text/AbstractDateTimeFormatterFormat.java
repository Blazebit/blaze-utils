/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class AbstractDateTimeFormatterFormat extends AbstractFormat {

    private static final long serialVersionUID = 1L;
    private static final Class<?> DATE_TIME_FORMATTER_CLASS;
    private static final Method FORMAT_METHOD;
    private static final Method PARSE_METHOD;

    static {
        try {
            DATE_TIME_FORMATTER_CLASS = Class.forName("java.time.format.DateTimeFormatter");
            FORMAT_METHOD = DATE_TIME_FORMATTER_CLASS.getMethod("format", Class.forName("java.time.temporal.TemporalAccessor"));
            PARSE_METHOD = DATE_TIME_FORMATTER_CLASS.getMethod("parse", CharSequence.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final String contextFormatterName;
    private final Object formatter;
    private final Method factory;

    public AbstractDateTimeFormatterFormat(Class<?> valueClass, String contextFormatterName, String formatterFieldName) {
        super(valueClass);
        this.contextFormatterName = contextFormatterName;
        try {
            this.formatter = DATE_TIME_FORMATTER_CLASS.getField(formatterFieldName).get(null);
            this.factory = valueClass.getMethod("from", Class.forName("java.time.temporal.TemporalAccessor"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public Serializable parse(String value, ParserContext context) {
        Object o = null;

        if (context != null) {
            o = context.getAttribute(contextFormatterName);

            if (o != null && !DATE_TIME_FORMATTER_CLASS.isInstance(o)) {
                throw new IllegalArgumentException(
                    "Illegal formatter object in context");
            }
        }

        if (o == null) {
            o = formatter;
        }

        try {
            return (Serializable) factory.invoke(null, PARSE_METHOD.invoke(o, value));
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public String format(Serializable value, ParserContext context) {
        Object o = null;

        if (context != null) {
            o = context.getAttribute(contextFormatterName);

            if (o != null && !DATE_TIME_FORMATTER_CLASS.isInstance(o)) {
                throw new IllegalArgumentException(
                    "Illegal formatter object in context");
            }
        }

        if (o == null) {
            o = formatter;
        }

        try {
            return (String) FORMAT_METHOD.invoke(o, value);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
