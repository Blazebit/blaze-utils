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

import java.util.Comparator;

/**
 * This is the base for comparator implementations providing util mehtods all
 * comparator need.
 *
 * @author Thomas Herzog
 */
public abstract class BaseComparator<T> implements Comparator<T> {

    /**
     * Checks for null objects and returns the proper result depedning on which
     * object is null
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
        return null;
    }
}
