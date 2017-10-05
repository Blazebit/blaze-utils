/*
 * Copyright 2013 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blazebit.comparator;

import com.blazebit.lang.StringUtils;
import com.blazebit.reflection.ExpressionUtils;

import java.text.Collator;
import java.util.Locale;

/**
 * This Comparator implementation compares the string values of the resolved
 * property path via a Collator instantiated witht he defined locale. If the
 * resolved value of property path is not of instance String then toString() on
 * the value will get called.
 *
 * @param <T> The types that should be compared
 * @author Thomas Herzog
 * @see BaseComparator
 */
public class PropertyPathStringComparator<T> extends BaseComparator<T> {

    private final Collator collator;
    private final String propertyPath;

    /**
     * @param locale       null not allowed
     * @param propertyPath null or empty string not allowed
     */
    public PropertyPathStringComparator(Locale locale, String propertyPath) {
        super();
        if (StringUtils.isEmpty(propertyPath)) {
            throw new IllegalArgumentException("PropertyPath must not be null !!!");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Locale must not be null !!!");
        }
        this.collator = Collator.getInstance(locale);
        this.propertyPath = propertyPath;
    }

    /**
     * Sets the JVM default Locale.
     *
     * @param propertyPath
     * @see PropertyPathStringComparator#PropertyPathStringComparator(java.util.Locale,
     * java.lang.String)
     * @see Locale#getDefault()
     */
    public PropertyPathStringComparator(String propertyPath) {
        this(Locale.getDefault(), propertyPath);
    }

    @Override
    public int compare(T object1, T object2) {
        try {
            Integer result = compareNullObjects(object1, object2);

            if (result == null) {
                // Retrieve field values of the objects
                Object value1 = ExpressionUtils.getValue(object1, propertyPath);
                Object value2 = ExpressionUtils.getValue(object2, propertyPath);

                result = compareNullObjects(value1, value2);

                if (result == null) {
                    result = collator.compare((value1 instanceof String) ? value1 : value1.toString(), (value2 instanceof String) ? value2 : value2.toString());
                }
            }
            return result;
        } catch (Throwable e) {
            throw new IllegalArgumentException(new StringBuilder(
                    "Could not compare !!! object1: ")
                    .append(object1.getClass().getName())
                    .append(" / object2: ")
                    .append(object2.getClass().getName())
                    .append(" / propertyPath: ").append(propertyPath)
                    .toString(), e);
        }
    }
}
