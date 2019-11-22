/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.io.Serializable;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * @param <T> The type that should be parsed from a string value.
 * @author Christian Beikov
 * @since 0.1.2
 */
public abstract class AbstractFormat<T extends Serializable> extends Format
        implements SerializableFormat<T> {

    private static final long serialVersionUID = 1L;

    private Class<T> clazz;

    public AbstractFormat(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo,
                               FieldPosition pos) {
        if (!clazz.isInstance(obj)) {
            throw new IllegalArgumentException(
                    "The given object is not of the expected type!");
        }

        return format(obj, toAppendTo);
    }

    @SuppressWarnings("unchecked")
    public StringBuffer format(Object obj, StringBuffer toAppendTo) {
        toAppendTo.append(format((T) obj, (ParserContext) null));
        return toAppendTo;
    }

    @Override
    public String format(T object, ParserContext context) {
        return object.toString();
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        try {
            return parse(source, null);
        } catch (ParseException e) {
            return null;
        }
    }
}