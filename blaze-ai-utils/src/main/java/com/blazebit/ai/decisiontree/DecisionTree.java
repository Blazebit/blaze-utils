package com.blazebit.ai.decisiontree;

import java.util.Set;

/**
 * @author Christian Beikov
 */
public interface DecisionTree<T> {

    public Set<T> apply(Item test);

    /**
     * @param test
     * @return
     * @throws IllegalArgumentException Is thrown when more than one result would apply.
     */
    public T applySingle(Item test);
}
