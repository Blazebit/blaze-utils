/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.comparator;

import com.blazebit.lang.StringUtils;
import java.util.Comparator;

import com.blazebit.reflection.ExpressionUtils;

/**
 * Generic Comparator is used at Collections.sort(); and can be applied on every
 * compareable Object.
 *
 * @author cchet
 */
public class GenericComparator<T> extends BaseComparator<T> {

    protected String propertyPath;

    /**
     * Be arware that if one of the elements in the list does not have the
     * memeber set addressed via propertyPath, an IllegalArgumentException will
     * occur.
     *
     * @param propertyPath the path to the memeber which shall be compared.
     * E.g.: object.valueHolder.value
     */
    public GenericComparator(final String propertyPath) {
        super();
        this.propertyPath = propertyPath;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compare(final T object1, final T object2) {
        try {
            Integer result = compareNullObjects(object1, object2);

            if (result == null) {
                // Retrieve field values of the objects
                Object value1 = ExpressionUtils.getValue(object1, propertyPath);
                Object value2 = ExpressionUtils.getValue(object2, propertyPath);
                result = compareNullObjects(value1, value2);

                if (result == null) {
                    if (!(value1 instanceof Comparable)) {
                        throw new IllegalArgumentException(new StringBuilder()
                                .append("Type '").append(value1.getClass().getName())
                                .append("' is not comparable.").toString());
                    }
                    result = ((Comparable<Object>) value1).compareTo(value2);
                }
            }
            return result;

        } catch (Throwable ex) {
            final String object1Class = (object1 != null) ? object1.getClass().toString() : "was null";
            final String object2Class = (object2 != null) ? object2.getClass().toString() : "was null";
            throw new IllegalArgumentException(new StringBuilder(
                    "Could not compare !!! object1: ")
                    .append(object1Class)
                    .append(" / object2: ")
                    .append(object2Class)
                    .append(" / propertyPath: ").append(propertyPath)
                    .toString(), ex);
        }
    }
}
