package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.*;
import java.util.Set;

/**
 *
 * @author Christian Beikov
 */
public class StringAttribute extends AbstractAttribute implements Attribute {
    
    public StringAttribute(final String name, final Set<? extends AttributeValue> values){
        super(name);
    }
    
    @Override
    public <T> DecisionNode<T> createNode(final DecisionNodeFactory decisionNodeFactory, final Set<Example<T>> examples){
        return new StringDecisionNode(decisionNodeFactory, this, examples);
    }
}
