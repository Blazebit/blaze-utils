package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.*;

/**
 *
 * @author Christian Beikov
 */
public abstract class AbstractAttribute implements Attribute {
    private final String name;
    
    public AbstractAttribute(final String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        final String localName = this.name;
        hash = 11 * hash + (localName != null ? localName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Attribute)) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        final String localName1 = this.name;
        final String localName2 = other.getName();
        if ((localName1 == null) ? (localName2 != null) : !localName1.equals(localName2)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Attribute{" + "name=" + name + '}';
    }
}
