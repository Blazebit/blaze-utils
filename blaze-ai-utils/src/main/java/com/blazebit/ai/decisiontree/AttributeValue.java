package com.blazebit.ai.decisiontree;

import java.util.List;

/**
 * Discrete Attribute.
 *
 * @author Christian Beikov
 */
public interface AttributeValue {

    public Object getValue();
    
    @Override
    public boolean equals(Object obj);
    
    @Override
    public int hashCode();
}
