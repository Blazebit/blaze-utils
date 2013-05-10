package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.Attribute;
import com.blazebit.ai.decisiontree.AttributeSelector;
import com.blazebit.ai.decisiontree.DecisionNode;
import com.blazebit.ai.decisiontree.DecisionNodeFactory;
import com.blazebit.ai.decisiontree.DecisionTree;
import com.blazebit.ai.decisiontree.Example;
import com.blazebit.ai.decisiontree.Item;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
