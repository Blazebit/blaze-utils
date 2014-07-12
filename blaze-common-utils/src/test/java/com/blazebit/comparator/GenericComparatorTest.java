/*
 * Copyright "2"011 Blazebit
 */
package com.blazebit.comparator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * This test class tests the {@link GenericComparator}
 *
 * @author Thomas Herzog
 */
public class GenericComparatorTest {

    @Test
    public void testGenericComparator_string_value() {
        List<CompareModel> values = Arrays.asList(new CompareModel("4"),
                new CompareModel("3"), new CompareModel("2"), new CompareModel("1"));
        List<CompareModel> expResult = Arrays.asList(new CompareModel("1"),
                new CompareModel("2"), new CompareModel("3"), new CompareModel("4"));
        Collections.sort(values, new GenericComparator<CompareModel>(CompareModel.PATH_VALUE));
        assertEquals(expResult, values);
    }

    @Test
    public void testGenericComparator_with_model_value() {
        List<CompareModel> values = Arrays.asList(new CompareModel(new CompareModel("4")),
                new CompareModel(new CompareModel("2")), new CompareModel(new CompareModel("3")), new CompareModel(new CompareModel("1")));
        List<CompareModel> expResult = Arrays.asList(new CompareModel(new CompareModel("1")),
                new CompareModel(new CompareModel("2")), new CompareModel(new CompareModel("3")), new CompareModel(new CompareModel("4")));
        Collections.sort(values, new GenericComparator<CompareModel>(CompareModel.PATH_MODEL));
        assertEquals(expResult, values);
    }

    @Test
    public void testGenericComparator_with_model_string_value() {
        List<CompareModel> values = Arrays.asList(new CompareModel(new CompareModel("4")),
                new CompareModel(new CompareModel("2")), new CompareModel(new CompareModel("3")), new CompareModel(new CompareModel("1")));
        List<CompareModel> expResult = Arrays.asList(new CompareModel(new CompareModel("1")),
                new CompareModel(new CompareModel("2")), new CompareModel(new CompareModel("3")), new CompareModel(new CompareModel("4")));
        Collections.sort(values, new GenericComparator<CompareModel>(CompareModel.PATH_MODEL_VALUE));
        assertEquals(expResult, values);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenericComparator_model_resolves_to_null() {
        List<CompareModel> values = Arrays.asList(new CompareModel(new CompareModel("4")),
                new CompareModel(new CompareModel("2")), new CompareModel(new CompareModel("3")), new CompareModel((CompareModel) null));
        Collections.sort(values, new GenericComparator<CompareModel>(CompareModel.PATH_MODEL_VALUE));
        fail();        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenericComparator_path_does_not_exists() {
        List<CompareModel> values = Arrays.asList(new CompareModel(new CompareModel("4")),
                new CompareModel(new CompareModel("2")), new CompareModel(new CompareModel("3")), new CompareModel((CompareModel) null));
        Collections.sort(values, new GenericComparator<CompareModel>(CompareModel.PATH_INVALID));
        fail();
    }
}
