package com.blazebit.ai.decisiontree;

import java.util.Set;

/**
 *
 * @author Christian Beikov
 */
public interface DecisionNode<T> {

    public Attribute getAttribute();
    
    public Set<T> apply(Item item);
    
    /**
     * 
     * @param test
     * @return 
     * @throws IllegalArgumentException Is thrown when more than one result would apply.
     */
    public T applySingle(Item item);
}
