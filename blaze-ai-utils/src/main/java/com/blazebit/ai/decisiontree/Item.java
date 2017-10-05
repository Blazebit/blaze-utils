package com.blazebit.ai.decisiontree;

import java.util.Map;

/**
 * @author Christian Beikov
 */
public interface Item {

    public Map<Attribute, AttributeValue> getValues();
}
