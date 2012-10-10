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
public class SimpleDecisionTree<T> implements DecisionTree<T> {

    private final Set<Attribute> attributes;
    private final AttributeSelector attributeSelector;
    private final DecisionNode<T> root;

    public SimpleDecisionTree(final Set<Attribute> attributes, final Set<Example<T>> examples, final AttributeSelector<T> attributeSelector) {
        this.attributes = new HashSet<Attribute>(attributes);
        this.attributeSelector = attributeSelector;
        this.root = new SimpleDecisionNodeFactory(new HashSet<Attribute>(0)).createNode(null, examples);
    }

    @Override
    public Set<T> apply(final Item test) {
        return root.apply(test);
    }

    @Override
    public T applySingle(final Item test) {
        return root.applySingle(test);
    }

    private static class LeafNode<T> implements DecisionNode<T> {

        private final T result;
        private final Set<T> results;

        public LeafNode() {
            this.result = null;
            this.results = Collections.emptySet();
        }
        
        public LeafNode(final Set<Example<T>> examples) {
            final Set<T> tempResults = new HashSet<T>(examples.size());

            for (final Example<T> example : examples) {
                tempResults.add(example.getResult());
            }

            if (tempResults.size() > 1) {
                this.result = null;
            } else {
                this.result = tempResults.iterator().next();
            }

            this.results = Collections.unmodifiableSet(tempResults);
        }

        @Override
        public Attribute getAttribute() {
            return null;
        }

        @Override
        public Set<T> apply(final Item item) {
            return results;
        }

        @Override
        public T applySingle(final Item item) {
            final T localResult = result;

            if (localResult == null) {
                throw new IllegalArgumentException("Ambigious result for the given item!");
            }

            return localResult;
        }
    }

    private class SimpleDecisionNodeFactory implements DecisionNodeFactory {

        private final Set<Attribute> usedAttributes;

        public SimpleDecisionNodeFactory(final Set<Attribute> usedAttributes) {
            this.usedAttributes = usedAttributes;
        }

        @Override
        public <T> DecisionNode<T> createNode(final Attribute usedAttribute, final Set<Example<T>> examples) {
            if (examples.size() < 1) {
                return new LeafNode<T>();
            }

            final Set<Attribute> localUsedAttributes = usedAttributes;
            final Set<Attribute> usedAttributesNew;

            if (usedAttribute != null) {
                usedAttributesNew = new HashSet<Attribute>(localUsedAttributes.size() + 1);
                usedAttributesNew.addAll(localUsedAttributes);
                usedAttributesNew.add(usedAttribute);
            } else {
                usedAttributesNew = localUsedAttributes;
            }

            final Attribute selectedAttribute = attributeSelector.select(examples, attributes, usedAttributesNew);

            if (selectedAttribute == null) {
                return new LeafNode<T>(examples);
            }

            return selectedAttribute.createNode(new SimpleDecisionNodeFactory(usedAttributesNew), examples);
        }
    }
}
