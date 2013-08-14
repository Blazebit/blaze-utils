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

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.junit.Test;

/**
 * This test class test the #{@link PropertyPathStringComparator}.
 *
 * @author Thomas Herzog
 */
public class PropertyPathStringComparatorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testPropertyPathStringComparator_null_locale() {
        new PropertyPathStringComparator<CompareModel>(null, CompareModel.PATH_MODEL_VALUE);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPropertyPathStringComparator_null_propertypath() {
        new PropertyPathStringComparator<CompareModel>(Locale.getDefault(), null);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPropertyPathStringComparator_empty_propertypath() {
        new PropertyPathStringComparator<CompareModel>(Locale.getDefault(), "");
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPropertyPathStringComparator_invalid_propertypath() {
        List<CompareModel> actual = Arrays.asList(
                new CompareModel("ABD"),
                new CompareModel("ABb"));
        Collections.sort(actual,
                new PropertyPathStringComparator<CompareModel>(Locale.getDefault(), CompareModel.PATH_INVALID));
    }

    @Test
    public void testPropertyPathStringComparator_string_value() {
        List<CompareModel> actual = Arrays.asList(
                new CompareModel("ÄBb"),
                new CompareModel("ABD"),
                new CompareModel("ABb"),
                new CompareModel((String) null),
                new CompareModel("abc"));
        List<CompareModel> expected = Arrays.asList(
                new CompareModel("ABb"),
                new CompareModel("ÄBb"),
                new CompareModel("abc"),
                new CompareModel("ABD"),
                new CompareModel((String) null));
        Collections.sort(actual,
                new PropertyPathStringComparator<CompareModel>(Locale.GERMAN, CompareModel.PATH_VALUE));

        assertEquals(expected, actual);
    }

    @Test
    public void testPropertyPathStringComparator_model_value() {
        List<CompareModel> actual = Arrays.asList(
                new CompareModel(new CompareModel("ABD")),
                new CompareModel(new CompareModel("ABb")),
                new CompareModel(new CompareModel((String) null)),
                new CompareModel(new CompareModel("abc")));
        List<CompareModel> expected = Arrays.asList(
                new CompareModel(new CompareModel("ABb")),
                new CompareModel(new CompareModel("abc")),
                new CompareModel(new CompareModel("ABD")),
                new CompareModel(new CompareModel((String) null)));
        Collections.sort(actual,
                new PropertyPathStringComparator<
               CompareModel>(Locale.getDefault(), CompareModel.PATH_MODEL));

        assertEquals(expected, actual);
    }
}
