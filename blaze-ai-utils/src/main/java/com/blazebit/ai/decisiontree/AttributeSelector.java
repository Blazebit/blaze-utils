package com.blazebit.ai.decisiontree;

import java.util.Set;

/**
 *
 * @author Christian Beikov
 */
public interface AttributeSelector<T> {
    
    public Attribute select(Set<Example<T>> examples, Set<Attribute> availableAttributes, Set<Attribute> usedAttributes);
}
