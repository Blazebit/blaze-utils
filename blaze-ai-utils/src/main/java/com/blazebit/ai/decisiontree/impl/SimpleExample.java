package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.Attribute;
import com.blazebit.ai.decisiontree.AttributeValue;
import com.blazebit.ai.decisiontree.Example;
import java.util.Map;

/**
 *
 * @author Christian Beikov
 */
public class SimpleExample<T> extends SimpleItem implements Example<T>{
    private final T result;
    
    public SimpleExample(final Map<Attribute, ? extends AttributeValue> values, final T result){
        super(values);
        this.result = result;
    }

    @Override
    public T getResult() {
        return result;
    }
}
