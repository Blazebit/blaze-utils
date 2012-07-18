/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.util.Calendar;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class ClassFormat extends AbstractFormat<Class> {

    public ClassFormat() {
        super(Class.class);
    }

    public Class<?> parse(String value, ParserContext context) {
        try {
            return Class.forName(value);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    @Override
    public String format(Class value, ParserContext context){
        return value.getName();
    }

}
