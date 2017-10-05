/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.exception;

import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for exception objects.
 *
 * @author Christian Beikov
 * @author Thomas Herzog
 * @since 0.1.2
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
    }

    /**
     * Throws the given throwable even if it is a checked exception.
     *
     * @param e The throwable to throw.
     */
    public static void doThrow(Throwable e) {
        ExceptionUtils.<RuntimeException>doThrow0(e);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void doThrow0(Throwable e) throws T {
        throw (T) e;
    }

    /**
     * Unwraps the throwable until it is no instance of the throwableClass.
     * Basically this method unwraps the throwable instances by calling
     * getCause() except the throwable element is an instance of
     * InvocationTargetException. When an InvocationTargetException is given
     * getTargetException() is used for unwrapping.
     * <p>
     * Be carefull when you use this method with a too general throwableClass.
     * It might be, that getCause() returns the this element in some cases and
     * this could lead to an endless loop!
     *
     * @param t              The throwable to unwrap
     * @param throwableClass The class of which a throwable must be to be
     *                       unwrapped
     * @return The unwrapped throwable or null if no further causes can be
     * unwrapped
     */
    @SafeVarargs
    public static Throwable unwrap(Throwable t,
                                   Class<? extends Throwable>... throwableClasses) {
        Throwable unwrapped = t;

        while (unwrapped != null && isInstance(unwrapped, throwableClasses)) {
            if (unwrapped.getCause() == null
                    && unwrapped instanceof InvocationTargetException) {
                // We do this here because an invocation target exception may
                // return null on getCause() but the throwable element we are
                // looking for can be accessed via getTargetException()
                unwrapped = ((InvocationTargetException) unwrapped)
                        .getTargetException();
            } else {
                unwrapped = unwrapped.getCause();
            }
        }

        return unwrapped;
    }

    public static Throwable unwrap(Throwable t,
                                   Class<? extends Throwable> throwableClass) {
        Throwable unwrapped = t;

        while (unwrapped != null && throwableClass.isInstance(unwrapped)) {
            if (unwrapped.getCause() == null
                    && unwrapped instanceof InvocationTargetException) {
                // We do this here because an invocation target exception may
                // return null on getCause() but the throwable element we are
                // looking for can be accessed via getTargetException()
                unwrapped = ((InvocationTargetException) unwrapped)
                        .getTargetException();
            } else {
                unwrapped = unwrapped.getCause();
            }
        }

        return unwrapped;
    }

    private static boolean isInstance(Throwable t,
                                      Class<? extends Throwable>... throwableClasses) {
        for (int i = 0; i < throwableClasses.length; i++) {
            if (throwableClasses[i].isInstance(t)) {
                return true;
            }
        }

        return false;
    }

    private static boolean equals(Class<? extends Throwable> t,
                                  Class<? extends Throwable>... throwableClasses) {
        for (int i = 0; i < throwableClasses.length; i++) {
            if (throwableClasses[i].equals(t)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the cause which are one of the given expected causes. This method is
     * useful when you have to find a cause within unexpected or unpredictable
     * exception wrappings.
     *
     * @param e      the exception instance to search the cause on
     * @param causes the causes which are searched.
     * @return the found cause, where the first occurring cause instance will be returned. If
     * the cause could not be found, then null will be returned
     */
    public static Throwable getCause(Throwable e, Class<? extends Throwable>... causes) {
        if ((e == null) || (causes == null) || (causes.length < 1)) {
            return null;
        } else if (isInstance(e, causes)) {
            if (((e.getCause() == null) || (e.getCause()
                    .equals(e) || (!equals(e.getClass(), causes))))) {
                return e;
            } else {
                return getCause(e.getCause(), causes);
            }
        } else if ((e.getCause() == null) && (e instanceof InvocationTargetException)) {
            return getCause(((InvocationTargetException) e).getTargetException(), causes);
        } else if (e.getCause() == null) {
            return null;
        } else {
            return getCause(e.getCause(), causes);
        }
    }
}
