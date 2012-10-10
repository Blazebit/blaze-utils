package com.blazebit.ai.decisiontree;

import java.util.Set;

/**
 *
 * @author Christian Beikov
 */
public interface Attribute {

    public String getName();
    
    public <T> DecisionNode<T> createNode(DecisionNodeFactory decisionNodeFactory, Set<Example<T>> examples);
}
