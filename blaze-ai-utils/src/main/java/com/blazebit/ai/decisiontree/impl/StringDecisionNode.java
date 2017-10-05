package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.*;
import com.blazebit.collection.TrieMap;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Christian Beikov
 */
public class StringDecisionNode<T> implements DecisionNode<T> {

    private final Attribute attribute;
    private final TrieMap<DecisionNode<T>> children;

    public StringDecisionNode(final DecisionNodeFactory decisionNodeFactory, final Attribute attribute, final Set<Example<T>> examples) {
        this.attribute = attribute;
        this.children = new TrieMap<DecisionNode<T>>();

        final TrieMap<Set<Example<T>>> exampleTrieMap = new TrieMap<Set<Example<T>>>();

        /* Fill values */
        for (final Example<T> example : examples) {
            final AttributeValue exampleAttributeValue = example.getValues().get(attribute);
            final String key = exampleAttributeValue == null ? "" : (String) exampleAttributeValue.getValue();
            Set<Example<T>> set = exampleTrieMap.get(key);

            if (set == null) {
                set = new HashSet<Example<T>>();
                exampleTrieMap.put(key, set);
            }

            set.add(example);
        }

        final TrieMap<DecisionNode<T>> localChildren = children;
        
        /* Select next attribute for each attribute value */
        for (final Map.Entry<CharSequence, Set<Example<T>>> entry : exampleTrieMap.entrySet()) {
            final Set<Example<T>> resultExamples = entry.getValue();

            if (resultExamples.size() > 0) {
                localChildren.put(entry.getKey(), decisionNodeFactory.createNode(attribute, resultExamples));
            }
        }
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
            final DecisionNode<T> node = children.get((String) value.getValue());

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
