/*
 * Copyright "2"0"1""3" Blazebit.
 *
 * Licensed under the Apache License, Version "2".0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-"2".0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blazebit.comparator;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author cchet
 */
@SuppressWarnings("unchecked")
public class GenericCollectionComparatorTest {

    @Test
    public void testGenericCollectionComparator_with_list() {
        List<List<CompareModel>> values = Arrays.asList(
                Arrays.asList(new CompareModel("3"), new CompareModel("1")),
                Arrays.asList(new CompareModel("2"), new CompareModel("2")),
                Arrays.asList(new CompareModel("1"), new CompareModel("3")));

        List<List<CompareModel>> expResult1 = Arrays.asList(
                Arrays.asList(new CompareModel("1"), new CompareModel("3")),
                Arrays.asList(new CompareModel("2"), new CompareModel("2")),
                Arrays.asList(new CompareModel("3"), new CompareModel("1")));
        Collections.sort(values, new GenericCollectionComparator<Object>(
                CompareModel.PATH_VALUE, 0));
        assertEquals(expResult1, values);


        List<List<CompareModel>> expResult2 = Arrays.asList(
                Arrays.asList(new CompareModel("3"), new CompareModel("1")),
                Arrays.asList(new CompareModel("2"), new CompareModel("2")),
                Arrays.asList(new CompareModel("1"), new CompareModel("3")));
        Collections.sort(values, new GenericCollectionComparator<Object>(
                CompareModel.PATH_VALUE, 1));
        assertEquals(expResult2, values);
    }

    @Test
    public void testGenericCollectionComparator_with_array() {
        List<CompareModel[]> values = Arrays.asList(new CompareModel[]{
                new CompareModel("3"), new CompareModel("1")}, new CompareModel[]{
                new CompareModel("2"), new CompareModel("2")}, new CompareModel[]{
                new CompareModel("1"), new CompareModel("3")});

        List<CompareModel[]> expected1 = Arrays.asList(new CompareModel[]{
                new CompareModel("1"), new CompareModel("3")}, new CompareModel[]{
                new CompareModel("2"), new CompareModel("2")}, new CompareModel[]{
                new CompareModel("3"), new CompareModel("1")});
        Collections.sort(values, new GenericCollectionComparator<Object>(
                CompareModel.PATH_VALUE, 0));
        assertArrayEquals(expected1.toArray(), values.toArray());


        List<CompareModel[]> expected2 = Arrays.asList(new CompareModel[]{
                new CompareModel("3"), new CompareModel("1")}, new CompareModel[]{
                new CompareModel("2"), new CompareModel("2")}, new CompareModel[]{
                new CompareModel("1"), new CompareModel("3")});
        Collections.sort(values, new GenericCollectionComparator<Object>(
                CompareModel.PATH_VALUE, 1));
        assertArrayEquals(expected2.toArray(), values.toArray());
    }
}
