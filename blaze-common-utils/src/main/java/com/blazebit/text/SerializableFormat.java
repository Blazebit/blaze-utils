/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.io.Serializable;
import java.text.ParseException;

/**
 * @author Christian Beikov
 */
public interface SerializableFormat<T extends Serializable> {

    public String format(T object, ParserContext context);

    public T parse(String value, ParserContext context) throws ParseException;
}
