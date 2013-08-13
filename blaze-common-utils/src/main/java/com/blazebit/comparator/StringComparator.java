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
import com.sun.rowset.internal.BaseRow;
import java.text.Collator;
import java.util.Locale;

/**
 * This Comparator implementation compares the string values of the resolved
 * property path via a Collator instantiated witht he defined locale. If
 * resolved value of property path is not of instance String then toString() on
 * the value will get called. This collator will compare the strings with locale
 * aware and without case sinsitivity.
 *
 * @author Thomas Herzog
 * @see BaseComparator
 */
public class StringComparator extends BaseComparator<String> {

    private final Collator collator;

    /**
     * @param locale
     * @param propertyPath
     */
    public StringComparator(Locale locale) {
        super();
        if (locale == null) {
            throw new IllegalArgumentException("Locale must not be null !!!");
        }
        this.collator = Collator.getInstance(locale);
    }

    /**
     * Sets the default Locale
     *
     * @param propertyPath
     * @see
     * PropertyPathStringComparator#PropertyPathStringComparator(java.util.Locale,
     * java.lang.String)
     */
    public StringComparator() {
        this(Locale.getDefault());
    }

    @Override
    public int compare(String object1, String object2) {
        try {
            Integer result = compareNullObjects(object1, object2);

            if (result == null) {
                result = collator.compare(object1, object2);
            }
            return result;
        } catch (Throwable e) {
            throw new IllegalArgumentException(new StringBuilder(
                    "Could not compare !!! object1: ")
                    .append(object1)
                    .append(" / object2: ")
                    .append(object2)
                    .toString(), e);
        }
    }
}
