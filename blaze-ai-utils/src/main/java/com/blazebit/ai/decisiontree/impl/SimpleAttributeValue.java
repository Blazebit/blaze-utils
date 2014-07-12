package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.AttributeValue;

/**
 *
 * @author Christian Beikov
 */
public class SimpleAttributeValue implements AttributeValue{
    private final Object value;
    private transient int hashCode = -1;
    
    public SimpleAttributeValue(Object value){
        this.value = value;
    }

    @Override
    public int hashCode() {
        if(hashCode < 0){
            hashCode = 89 * 3 + (this.value != null ? this.value.hashCode() : 0);
        }
        
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SimpleAttributeValue)) {
            return false;
        }
        final SimpleAttributeValue other = (SimpleAttributeValue) obj;
        final Object v1 = this.value;
        final Object v2 = other.value;
        if (v1 != v2 && (v1 == null || !v1.equals(v2))) {
            return false;
        }
        return true;
    }
    
    @Override
    public Object getValue(){
        return value;
    }

    @Override
    public String toString() {
        return "AttributeValue{" + "value=" + value + '}';
    }
}
