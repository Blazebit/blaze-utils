/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.exception;

import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for exception objects.
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class ExceptionUtil {

    private ExceptionUtil() {}

    /**
     * Unwraps the cause of InvocationTargetException until an throwable element
     * is given that is no subtype of InvocationTargetException.
     * 
     * Using this method is equal to unwrap(t, InvocationTargetException.class)
     * 
     * @param t The throwable to unwrap
     * @return The unwrapped throwable or null if no further causes can be unwrapped
     * @see ExceptionUtil#unwrap(java.lang.Throwable, java.lang.Class) 
     */
    public static Throwable unwrapInvocationTargetException(Throwable t) {
        return unwrap(t, InvocationTargetException.class);
    }
    
    /**
     * Unwraps the throwable until it is no instance of the throwableClass.
     * Basically this method unwraps the throwable instances by calling
     * getCause() except the throwable element is an instance of
     * InvocationTargetException. When an InvocationTargetException is given
     * getTargetException() is used for unwrapping.
     * 
     * Be carefull when you use this method with a too general throwableClass.
     * It might be, that getCause() returns the this element in some cases
     * and this could lead to an endless loop!
     * 
     * @param t The throwable to unwrap
     * @param throwableClass The class of which a throwable must be to be unwrapped
     * @return The unwrapped throwable or null if no further causes can be unwrapped
     */
    public static Throwable unwrap(Throwable t, Class<? extends Throwable> throwableClass) {
        Throwable unwrapped = t;
        
        while (unwrapped != null && throwableClass.isInstance(unwrapped)) {
            if(unwrapped.getCause() == null && unwrapped instanceof InvocationTargetException){
                // We do this here because an invocation target exception may
                // return null on getCause() but the throwable element we are
                // looking for can be accessed via getTargetException()
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else {
                unwrapped = unwrapped.getCause();
            }
        }

        return unwrapped;
    }
}
