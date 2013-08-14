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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author cchet
 */
public class StringComparatorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testStringComparator_null_locale() {
        new StringComparator(null);
        fail();
    }

    @Test
    public void testStringComparator() {
        final List<String> expected = Arrays.asList("ABb", "ÄBb", "Abc", "abd", "aBe");
        final List<String> actual = Arrays.asList("Abc", "aBe", "abd", "ABb", "ÄBb");
        Collections.sort(actual, new StringComparator());
        assertEquals(expected, actual);
    }
}
