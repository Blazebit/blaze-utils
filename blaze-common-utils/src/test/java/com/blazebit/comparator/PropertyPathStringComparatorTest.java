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
 *
 * @author cchet
 */
public class PropertyPathStringComparatorTest {

    public class StringCompareModel {

        private final String id;
        private final StringCompareModel model;

        public StringCompareModel(final String value) {
            this.id = value;
            model = null;
        }

        public StringCompareModel(final StringCompareModel value) {
            this.id = value.toString();
            this.model = value;
        }

        public String getId() {
            return id;
        }

        public StringCompareModel getModel() {
            return model;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StringCompareModel other = (StringCompareModel) obj;
            if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return id != null ? id : super.toString();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPropertyPathStringComparator_null_locale() {
        new PropertyPathStringComparator<StringCompareModel>(null, "member.string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPropertyPathStringComparator_null_propertypath() {
        new PropertyPathStringComparator<StringCompareModel>(Locale.getDefault(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPropertyPathStringComparator_empty_propertypath() {
        new PropertyPathStringComparator<StringCompareModel>(Locale.getDefault(), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPropertyPathStringComparator_invalid_propertypath() {
        List<StringCompareModel> actual = Arrays.asList(
                new StringCompareModel("ABD"),
                new StringCompareModel("ABb"),
                new StringCompareModel((String) null),
                new StringCompareModel("abc"));
        List<StringCompareModel> expected = Arrays.asList(
                new StringCompareModel((String) null),
                new StringCompareModel("ABb"),
                new StringCompareModel("abc"),
                new StringCompareModel("ABD"));
        Collections.sort(actual,
                new PropertyPathStringComparator<StringCompareModel>(Locale.getDefault(), "seghdshsdfhsdf"));
        assertEquals(expected, actual);
    }

    @Test
    public void testPropertyPathStringComparator_string_value() {
        List<StringCompareModel> actual = Arrays.asList(
                new StringCompareModel("ABD"),
                new StringCompareModel("ABb"),
                new StringCompareModel((String) null),
                new StringCompareModel("abc"));
        List<StringCompareModel> expected = Arrays.asList(
                new StringCompareModel("ABb"),
                new StringCompareModel("abc"),
                new StringCompareModel("ABD"),
                new StringCompareModel((String) null));
        Collections.sort(actual,
                new PropertyPathStringComparator<StringCompareModel>(Locale.getDefault(), "id"));

        assertEquals(expected, actual);
    }

    @Test
    public void testPropertyPathStringComparator_member_value() {
        List<StringCompareModel> actual = Arrays.asList(
                new StringCompareModel(new StringCompareModel("ABD")),
                new StringCompareModel(new StringCompareModel("ABb")),
                new StringCompareModel(new StringCompareModel((String) null)),
                new StringCompareModel(new StringCompareModel("abc")));
        List<StringCompareModel> expected = Arrays.asList(
                new StringCompareModel(new StringCompareModel("ABb")),
                new StringCompareModel(new StringCompareModel("abc")),
                new StringCompareModel(new StringCompareModel("ABD")),
                new StringCompareModel(new StringCompareModel((String)null)));
        Collections.sort(actual,
                new PropertyPathStringComparator<
               StringCompareModel>(Locale.getDefault(), "model"));

        assertEquals(expected, actual);
    }
}
