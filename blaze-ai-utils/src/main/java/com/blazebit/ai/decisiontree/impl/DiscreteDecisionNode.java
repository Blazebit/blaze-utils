package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.*;

import java.util.*;

/**
 * @author Christian Beikov
 */
public class DiscreteDecisionNode<T> implements DecisionNode<T> {

    private final Attribute attribute;
    private final Map<AttributeValue, DecisionNode<T>> children;

    public DiscreteDecisionNode(final DecisionNodeFactory decisionNodeFactory, final DiscreteAttribute attribute, final Set<Example<T>> examples) {
        this.attribute = attribute;

        final Set<AttributeValue> attributeValues = attribute.getValues();
        final Map<AttributeValue, Set<Example<T>>> exampleMap = new HashMap<AttributeValue, Set<Example<T>>>(attributeValues.size() + 1);

        /* Fill values */
        for (final Example<T> example : examples) {
            final AttributeValue attributeValue = example.getValues().get(attribute);
            Set<Example<T>> set = exampleMap.get(attributeValue);

            if (set == null) {
                set = new HashSet<Example<T>>();
                exampleMap.put(attributeValue, set);
            }

            set.add(example);
        }

        final Map<AttributeValue, DecisionNode<T>> localChildren = new HashMap<AttributeValue, DecisionNode<T>>(attributeValues.size() + 1);
        
        /* Select next attribute for each attribute value */
        for (final Map.Entry<AttributeValue, Set<Example<T>>> entry : exampleMap.entrySet()) {
            localChildren.put(entry.getKey(), decisionNodeFactory.createNode(attribute, entry.getValue()));
        }

        this.children = localChildren;
    }

    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public Set<T> apply(final Item item) {
        final AttributeValue value = item.getValues().get(attribute);

        if (value == null) {
            /* If no value exists for the current attribute, use the results of all the children */
            final Set<T> results = new HashSet<T>();

            for (final DecisionNode<T> node : children.values()) {
                results.addAll(node.apply(item));
            }

            return results;
        } else {
            final DecisionNode<T> node = children.get(value);

            if (node == null) {
                return Collections.emptySet();
            } else {
                return node.apply(item);
            }
        }
    }

    @Override
    public T applySingle(final Item item) {
        final AttributeValue value = item.getValues().get(attribute);

        if (value == null) {
            /* If no value exists for the current attribute, use the results of all the children */
            T result = null;

            for (final DecisionNode<T> node : children.values()) {
                final T tempResult = node.applySingle(item);

                if (result == null) {
                    result = tempResult;
                } else if (tempResult != null) {
                    throw new IllegalArgumentException("Ambigious result for the given item!");
                }
            }

            return result;
        } else {
            final DecisionNode<T> node = children.get(value);

            if (node == null) {
                return null;
            } else {
                return node.applySingle(item);
            }
        }
    }
}
