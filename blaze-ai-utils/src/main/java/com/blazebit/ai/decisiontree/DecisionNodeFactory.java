package com.blazebit.ai.decisiontree;

import java.util.Set;

/**
 * @author Christian Beikov
 */
public interface DecisionNodeFactory {

    /**
     * @param <T>
     * @param usedAttribute
     * @param examples
     * @return never null
     */
    public <T> DecisionNode<T> createNode(Attribute usedAttribute, Set<Example<T>> examples);
}
