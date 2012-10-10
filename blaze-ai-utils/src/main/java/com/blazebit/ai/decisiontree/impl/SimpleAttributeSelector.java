package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.Attribute;
import com.blazebit.ai.decisiontree.AttributeSelector;
import com.blazebit.ai.decisiontree.Example;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Christian Beikov
 */
public class SimpleAttributeSelector<T> implements AttributeSelector<T> {

    @Override
    public Attribute select(final Set<Example<T>> examples, final Set<Attribute> availableAttributes, final Set<Attribute> usedAttributes) {
        final Set<Attribute> usable = new HashSet<Attribute>(availableAttributes);
        usable.removeAll(usedAttributes);
        return usable.size() > 0 ? usable.iterator().next() : null;
    }
    
}
