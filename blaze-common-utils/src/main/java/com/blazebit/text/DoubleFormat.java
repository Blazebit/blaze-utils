/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * @author Christian Beikov
 * @since 0.1.2
 */
public class DoubleFormat extends AbstractFormat<Double> {

    private static final long serialVersionUID = 1L;

    public DoubleFormat() {
        super(Double.class);
    }

    public Double parse(String value, ParserContext context) {
        return Double.parseDouble(value);
    }
}
