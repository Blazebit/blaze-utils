package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.Attribute;
import com.blazebit.ai.decisiontree.AttributeValue;
import com.blazebit.ai.decisiontree.DecisionNode;
import com.blazebit.ai.decisiontree.DecisionNodeFactory;
import com.blazebit.ai.decisiontree.Example;
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
