/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.comparator;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This comparator is able to compare objects on a specified index which are part of a Collection or an Array.
 * 
 * @author Thomas Herzog
 */
public class GenericCollectionComparator<T> extends GenericComparator<T> {

    private final int index;

    /**
     * @param field
     * @param index
     */
    public GenericCollectionComparator(String field, int index) {
        super(field);
        this.index = index;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compare(T object1, T object2) {
        Integer result = compareNullObjects(object1, object2);

        if (result == null) {
            result = super.compare((T) get(object1, index), (T) get(object2, index));
        }
        return result;
    }

    /*
     * Borrowed from Apache Commons Collections to avoid the dependency
     */
    private static Object get(Object object, int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: "
                    + index);
        }
        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            Iterator<?> iterator = map.entrySet().iterator();
            return get(iterator, index);
        } else if (object instanceof List) {
            return ((List<?>) object).get(index);
        } else if (object instanceof Object[]) {
            return ((Object[]) object)[index];
        } else if (object instanceof Iterator) {
            Iterator<?> it = (Iterator<?>) object;
            while (it.hasNext()) {
                index--;
                if (index == -1) {
                    return it.next();
                } else {
                    it.next();
                }
            }
            throw new IndexOutOfBoundsException("Entry does not exist: "
                    + index);
        } else if (object instanceof Collection) {
            Iterator<?> iterator = ((Collection<?>) object).iterator();
            return get(iterator, index);
        } else if (object instanceof Enumeration) {
            Enumeration<?> it = (Enumeration<?>) object;
            while (it.hasMoreElements()) {
                index--;
                if (index == -1) {
                    return it.nextElement();
                } else {
                    it.nextElement();
                }
            }
            throw new IndexOutOfBoundsException("Entry does not exist: "
                    + index);
        } else if (object == null) {
            throw new IllegalArgumentException("Unsupported object type: null");
        } else {
            try {
                return Array.get(object, index);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unsupported object type: "
                        + object.getClass().getName());
            }
        }
    }
}
