package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.DecisionTree;
import com.blazebit.ai.decisiontree.Item;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Christian Beikov
 */
public class NoopDecisionTree<T> implements DecisionTree<T> {

    private final T value;

    public NoopDecisionTree(T value) {
        this.value = value;
    }

    @Override
    public Set<T> apply(final Item test) {
        Set<T> set = new HashSet<T>();
        set.add(value);
        return set;
    }

    @Override
    public T applySingle(final Item test) {
        return value;
    }
}
