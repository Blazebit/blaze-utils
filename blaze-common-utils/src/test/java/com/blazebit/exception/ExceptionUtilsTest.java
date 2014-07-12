/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.exception;

import java.lang.reflect.InvocationTargetException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author Christian Beikov
 */
@SuppressWarnings("unchecked")
public class ExceptionUtilsTest {

    /**
     * Test of unwrapInvocationTargetException method, of class ExceptionUtil.
     */
    @Test
    public void testUnwrapInvocationTargetException() {
        Throwable inner = new RuntimeException();
        Throwable outer = new InvocationTargetException(inner);
        assertEquals(
            ExceptionUtils.unwrap(outer, InvocationTargetException.class),
            inner);
        outer = new InvocationTargetException(outer);
        assertEquals(
            ExceptionUtils.unwrap(outer, InvocationTargetException.class),
            inner);
        assertEquals(
            ExceptionUtils.unwrap(inner, InvocationTargetException.class),
            inner);
        assertNull(ExceptionUtils.unwrap(new InvocationTargetException(null),
                                         InvocationTargetException.class));
    }

    /**
     * Test of unwrap method, of class ExceptionUtil.
     */
    @Test
    public void testUnwrap() {
        Throwable inner = new RuntimeException();
        Throwable outer = new InvocationTargetException(inner);
        assertEquals(
            ExceptionUtils.unwrap(outer, InvocationTargetException.class),
            inner);
        Throwable outer1 = new Error(outer);
        assertEquals(ExceptionUtils.unwrap(outer1, Error.class), outer);
        assertNull(ExceptionUtils.unwrap(inner, RuntimeException.class));

    }
    
    @Test
    public void test_unwrap_withNullShouldReturnNull() {
        // Given
        Throwable expected = null;
        Throwable exception = null;
        
        // When
        Throwable actual = ExceptionUtils.unwrap(exception);
        
        // Then
        assertEquals(expected, actual);
    }
    
    @Test
    public void test_unwrap_withNonAvailableTypeShouldReturnPassedInstance() {
        // Given
        Throwable expected = new Exception();
        Throwable exception = expected;
        
        // When
        Throwable actual = ExceptionUtils.unwrap(exception);
        
        // Then
        assertEquals(expected, actual);
    }
    
    @Test
    public void test_unwrap_withNonAvailableSubTypeShouldReturnPassedInstance() {
        // Given
        Throwable expected = new Exception();
        Throwable exception = expected;
        
        // When
        Throwable actual = ExceptionUtils.unwrap(exception, RuntimeException.class);
        
        // Then
        assertEquals(expected, actual);
    }
    
    @Test
    public void test_unwrap_withKnownWrapperShouldReturnExpected() {
        // Given
        Throwable expected = new Exception();
        Throwable exception = new RuntimeException(expected);
        
        // When
        Throwable actual = ExceptionUtils.unwrap(exception, RuntimeException.class);
        
        // Then
        assertEquals(expected, actual);
    }
    
    @Test
    public void test_unwrap_withInvocationTargetExceptionWrapperShouldReturnExpected() {
        // Given
        Throwable expected = new Exception();
        Throwable exception = new InvocationTargetException(expected);
        
        // When
        Throwable actual = ExceptionUtils.unwrap(exception, InvocationTargetException.class);
        
        // Then
        assertEquals(expected, actual);
    }
    
    @Test
    public void test_unwrap_withInvocationTargetExceptionWrapperAndNullCauseShouldReturnNull() {
        // Given
        Throwable expected = null;
        Throwable exception = new InvocationTargetException(expected);
        
        // When
        Throwable actual = ExceptionUtils.unwrap(exception, InvocationTargetException.class);
        
        // Then
        assertEquals(expected, actual);
    }
    
    @Test
    public void test_unwrap_withMultipleNestedWrappersShouldReturnExpected() {
        // Given
        Throwable expected = new Exception();
        Throwable exception = new RuntimeException(new InvocationTargetException(new RuntimeException(new InvocationTargetException(expected))));
        
        // When
        Throwable actual = ExceptionUtils.unwrap(exception, RuntimeException.class, InvocationTargetException.class);
        
        // Then
        assertEquals(expected, actual);
    }
    
    @Test
    public void test_unwrap_withMultipleNestedWrappersAndNullCauseShouldReturnNull() {
        // Given
        Throwable expected = null;
        Throwable exception = new RuntimeException(new InvocationTargetException(new RuntimeException(new InvocationTargetException(expected))));
        
        // When
        Throwable actual = ExceptionUtils.unwrap(exception, RuntimeException.class, InvocationTargetException.class);
        
        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testGetCause_null_exception() {
        assertNull(ExceptionUtils.getCause(null, IllegalAccessError.class));
    }

    @Test
    public void testGetCause_null_causes() {
        assertNull(ExceptionUtils.getCause(new Exception(), (Class<Throwable>[]) null));
    }

    @Test
    public void testGetCause_empty_causes() {
        assertNull(ExceptionUtils.getCause(new Exception(), (Class<Throwable>[]) new Class[]{}));
    }

    @Test
    public void testGetCause_not_found() {
        final IllegalArgumentException expected = new IllegalArgumentException();
        Exception ex = new Exception(new InvocationTargetException(expected));
        assertNull(ExceptionUtils.getCause(ex, IllegalAccessError.class));
    }

    @Test
    public void testGetCause() {
        final IllegalArgumentException expected = new IllegalArgumentException();
        Exception ex = new Exception(new InvocationTargetException(new Exception(expected)));
        assertEquals(expected, ExceptionUtils.getCause(ex, IllegalAccessError.class, IllegalArgumentException.class));
    }
}
