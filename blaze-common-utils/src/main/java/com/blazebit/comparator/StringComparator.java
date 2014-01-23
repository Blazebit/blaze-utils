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

import java.text.Collator;
import java.util.Locale;

/**
 * This Comparator implementation compares the string values via a Collator
 * instantiated witht he defined locale if the given objects are of instance
 * String, if they are not toString() will get called on the given objects.
 *
 * @author Thomas Herzog
 * @see BaseComparator
 */
public class StringComparator extends BaseComparator<Object> {

    private final Collator collator;

    /**
     * @param locale
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
     * @see
     * PropertyPathStringComparator#PropertyPathStringComparator(java.util.Locale,
     * java.lang.String)
     */
    public StringComparator() {
        this(Locale.getDefault());
    }

    @Override
    public int compare(Object object1, Object object2) {
        try {
            Integer result = compareNullObjects(object1, object2);

            if (result == null) {
                result = collator.compare((object1 instanceof String) ? (String) object1 : object1.toString(), (object2 instanceof String) ? (String) object2 : object2.toString());
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
