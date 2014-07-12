package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.Attribute;
import com.blazebit.ai.decisiontree.AttributeValue;
import com.blazebit.ai.decisiontree.Item;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Christian Beikov
 */
public class SimpleItem implements Item {

    private final Map<Attribute, AttributeValue> values;

    public SimpleItem(final Map<Attribute, ? extends AttributeValue> values) {
        this.values = Collections.unmodifiableMap(new HashMap<Attribute, AttributeValue>(values));
    }

    @Override
    public Map<Attribute, AttributeValue> getValues() {
        return values;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        final Map<Attribute, AttributeValue> v = values;
        hash = 79 * hash + (v != null ? v.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Item)) {
            return false;
        }
        final Item other = (Item) obj;
        final Map<Attribute, AttributeValue> v1 = values;
        final Map<Attribute, AttributeValue> v2 = other.getValues();

        if (v1 != v2 && (v1 == null || !v1.equals(v2))) {
            return false;
        }
        return true;
    }
}
