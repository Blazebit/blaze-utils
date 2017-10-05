package com.blazebit.ai.decisiontree;

import java.util.Set;

/**
 * Discrete Attribute.
 *
 * @author Christian Beikov
 */
public interface DiscreteAttribute extends Attribute {

    public Set<AttributeValue> getValues();
}
