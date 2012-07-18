/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.comparator;

import com.blazebit.reflection.ReflectionUtil;
import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Generic Comparator is used at Collections.sort(); and can be applied on every
 * compareable Object.
 *
 * @author cchet
 */
public class GenericComparator<T> implements Comparator<T> {

    protected String field;

    /**
     * @param field
     */
    public GenericComparator(String field) {
        super();
        this.field = field;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compare(T object1, T object2) {
        try {
            if (field == null) {
                throw new IllegalArgumentException("Field must not be null !!!");
            }
            if (object1 == null || object2 == null) {
                return compareNullObjects(object1, object2);
            }

            Method method1 = ReflectionUtil.getGetter(object1.getClass(), field);
            Method method2 = ReflectionUtil.getGetter(object2.getClass(), field);

            // Retrieve field values of the objects
            Object value1 = method1.invoke(object1);
            Object value2 = method2.invoke(object2);

            if (value1 == null || value2 == null) {
                return compareNullObjects(value1, value2);
            }

            if (!(value1 instanceof Comparable)) {
                throw new IllegalArgumentException(new StringBuilder().append("Type '").append(value1.getClass().getName()).append("' is not comparable.").toString());
            }

            return ((Comparable)value1).compareTo(value2);
        } catch(RuntimeException ex){
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Could not compare !!!", e);
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
