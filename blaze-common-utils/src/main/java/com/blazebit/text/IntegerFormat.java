/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * @author Christian Beikov
 * @since 0.1.2
 */
public class IntegerFormat extends AbstractFormat<Integer> {

    private static final long serialVersionUID = 1L;

    public IntegerFormat() {
        super(Integer.class);
    }

    public Integer parse(String value, ParserContext context) {
        return Integer.parseInt(value);
    }
}
