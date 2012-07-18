/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.io.Serializable;
import java.text.*;

/**
 * 
 * @param <T> The type that should be parsed from a string value.
 * @author Christian Beikov
 * @since 0.1.2
 */
public abstract class AbstractFormat<T extends Serializable> extends Format implements SerializableFormat<T>{
    
    private Class<T> clazz;

    public AbstractFormat(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if(!clazz.isInstance(obj)){
            throw new IllegalArgumentException("The given object is not of the expected type!");
        }
        
        return format(obj, toAppendTo);
    }
    
    public StringBuffer format(Object obj, StringBuffer toAppendTo){
        toAppendTo.append(format(obj, null));
        return toAppendTo;
    }
    
    @Override
    public String format(T object, ParserContext context){
        return object.toString();
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return parse(source, null);
    }
}