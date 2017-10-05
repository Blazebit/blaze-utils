package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Christian Beikov
 */
public class SimpleDiscreteAttribute extends AbstractAttribute implements DiscreteAttribute {
    private final Set<AttributeValue> values;

    public SimpleDiscreteAttribute(final String name, final Set<? extends AttributeValue> values) {
        super(name);
        this.values = Collections.unmodifiableSet(new HashSet<AttributeValue>(values));
    }

    @Override
    public Set<AttributeValue> getValues() {
        return values;
    }

    @Override
    public <T> DecisionNode<T> createNode(final DecisionNodeFactory decisionNodeFactory, final Set<Example<T>> examples) {
        return new DiscreteDecisionNode(decisionNodeFactory, this, examples);
    }
}
