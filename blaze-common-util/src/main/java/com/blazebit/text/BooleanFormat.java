/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class BooleanFormat extends AbstractFormat<Boolean> {
    
    private static final long serialVersionUID = 1L;

    public BooleanFormat() {
        super(Boolean.class);
    }

    public Boolean parse(String value, ParserContext context) {
        return Boolean.parseBoolean(value);
    }

}
