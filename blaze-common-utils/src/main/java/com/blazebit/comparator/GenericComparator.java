/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.comparator;

import java.util.Comparator;

import com.blazebit.reflection.ExpressionUtils;

/**
 * Generic Comparator is used at Collections.sort(); and can be applied on every
 * compareable Object.
 *
 * @author cchet
 */
public class GenericComparator<T> implements Comparator<T> {

    protected String propertyPath;

    /**
     * Be arware that if one of the elements in the list does not have the
     * memeber set addressed via propertyPath, an IllegalArgumentException will
     * occur.
     *
     * @param propertyPath the path to the memeber which shall be compared.
     * E.g.: object.valueHolder.value
     */
    public GenericComparator(String propertyPath) {
        super();
        this.propertyPath = propertyPath;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compare(T object1, T object2) {
        try {
            if (propertyPath == null) {
                throw new IllegalArgumentException("PropertyPath must not be null !!!");
            }
            if (object1 == null || object2 == null) {
                return compareNullObjects(object1, object2);
            }

            // Retrieve field values of the objects
            Object value1 = ExpressionUtils.getValue(object1, propertyPath);
            Object value2 = ExpressionUtils.getValue(object2, propertyPath);

            if (value1 == null || value2 == null) {
                return compareNullObjects(value1, value2);
            }

            if (!(value1 instanceof Comparable)) {
                throw new IllegalArgumentException(new StringBuilder()
                        .append("Type '").append(value1.getClass().getName())
                        .append("' is not comparable.").toString());
            }

            return ((Comparable<Object>) value1).compareTo(value2);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(new StringBuilder("Could not compare !!! object1: ").append(object1.getClass().getName()).append(" / object2: ").append(object2.getClass().getName()).append(" / propertyPath: ").append(propertyPath).toString(), ex);
        } catch (Exception e) {
            throw new IllegalArgumentException(new StringBuilder("Could not compare !!! object1: ").append(object1.getClass().getName()).append(" / object2: ").append(object2.getClass().getName()).append(" / propertyPath: ").append(propertyPath).toString(), e);
        }
    }

    /**
     * Checks for null objects.
     *
     * @param object1
     * @param object2
     * @return <ol> <li>0 = field null or both objects null</li> <li>1 = object1
     * is null</li> <li>-1 = object2 is null</li> <li>null = both objects are
     * not null</li> </ol>
     */
    protected Integer compareNullObjects(Object object1, Object object2) {
        if ((object1 == null) && (object2 == null)) {
            return 0;
        }
        if (object1 == null) {
            return 1;
        }
        if (object2 == null) {
            return -1;
        }
        return 0;
    }
}
