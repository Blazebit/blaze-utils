package com.blazebit.ai.decisiontree;

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
