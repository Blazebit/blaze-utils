/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.cleanup;

import com.blazebit.annotation.AnnotationUtils;
import com.blazebit.cdi.cleanup.annotation.Cleanup;
import com.blazebit.cdi.cleanup.annotation.CleanupHandler;
import com.blazebit.cdi.cleanup.annotation.CleanupHandling;
import com.blazebit.exception.ExceptionUtils;
import com.blazebit.reflection.ReflectionUtils;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Invokes cleanup methods after the invocation of a method. The specified
 * cleanup with the cleanup name declared in CleanupHandling is used for the
 * declared exception. If CleanupHandling does not specify a cleanup name, the
 * cleanup name of the CleanupHandler is used. Cleanup methods of
 * CleanupHandling elements that have the flag always set to true, are always
 * invoked.
 *
 * @author Christian Beikov
 * @see CleanupHandler
 * @see CleanupHandling
 * @see Cleanup
 * @since 0.1.2
 */
@Interceptor
@CleanupHandler(cleanup = Object.class)
public class CleanupHandlerInterceptor implements Serializable {

    private static final long serialVersionUID = 2640134717545764135L;

    @AroundInvoke
    public Object cleanup(InvocationContext ic) throws Exception {
        Object ret = null;
        Method m = ic.getMethod();
        Object targetObject = ic.getTarget();
        Class<?> targetClass = targetObject == null ? m.getDeclaringClass()
                : targetObject.getClass();
        CleanupHandler cleanupHandlerAnnotation = AnnotationUtils
                .findAnnotation(m, targetClass, CleanupHandler.class);

        if (cleanupHandlerAnnotation == null) {
            throw new IllegalStateException(
                    "The interceptor annotation can not be determined!");
        }

        // Avoid CleanupHandling for methods annotated with Cleanup
        // This could end up in an endless loop
        if (ic.getMethod().isAnnotationPresent(Cleanup.class)) {
            return ic.proceed();
        }

        try {
            ret = ic.proceed();
        } catch (Throwable t) {
            // Unwrap Exception if t is instanceof InvocationTargetException
            Throwable t1 = ExceptionUtils.unwrap(t,
                    InvocationTargetException.class);
            handleCleanups(targetObject, cleanupHandlerAnnotation, t1);

            if (t1 instanceof Exception) {
                throw (Exception) t1;
            } else {
                throw new Exception(t1);
            }
        }

        handleCleanups(targetObject, cleanupHandlerAnnotation, null);

        return ret;
    }

    private void handleCleanups(Object target, CleanupHandler handler,
                                Throwable t) throws Exception {
        CleanupHandling[] handlings = handler.value();

        if (handlings.length > 0) {
            // Invoke cleanup handling cleanups
            for (CleanupHandling handling : handlings) {
                if (handling.always()) {
                    doInvokeCleanup(target, handler, handling, t);
                } else if (t != null
                        && t.getClass().getName().equals(handling.exception().getName())) {
                    doInvokeCleanup(target, handler, handling, t);
                }
            }
        } else {
            // Invoke Cleanup handler cleanup
            doInvokeCleanup(target, handler, null, t);
        }

    }

    private void doInvokeCleanup(Object target, CleanupHandler handler,
                                 CleanupHandling handling, Throwable exception) throws Exception {
        // Invoke handler cleanup only if handling cleanup can not be found
        if (handling == null || !invokeCleanups(target, handling.cleanup(), exception)) {
            invokeCleanups(target, handler.cleanup(), exception);
        }
    }

    private boolean invokeCleanups(Object target, Class<?> cleanupClazz, Throwable exception)
            throws Exception {
        if (cleanupClazz != null) {
            // Christian Beikov 29.07.2013: Traverse whole hierarchy
            // instead of retrieving the annotation directly from
            // the class object.
            List<Method> methods = ReflectionUtils.getMethods(target.getClass(), Cleanup.class);
            Method m = null;

            for (Method candidate : methods) {
                Cleanup c = AnnotationUtils.findAnnotation(candidate, Cleanup.class);

                if (cleanupClazz.equals(c.value())) {
                    m = candidate;
                    break;
                }
            }

            if (m != null) {
                final Class<?>[] parameterTypes = m.getParameterTypes();
                if (parameterTypes.length == 1) {
                    // Need to check for null exception
                    if (exception != null) {
                        // Check if exception type fits formal parameter type
                        if (!ReflectionUtils.isSubtype(exception.getClass(), parameterTypes[0])) {
                            throw new IllegalArgumentException("Cleanup method with name " + cleanupClazz.getName() + " requires a parameter that is not a subtype of the exception class " + exception.getClass().getName());
                        }
                    }
                    // Invoked either with set or null exception
                    m.invoke(target, exception);
                } else {
                    m.invoke(target);
                }
                return true;
            }
        } else {
            return false;
        }

        throw new IllegalArgumentException("Cleanup method with name '"
                + cleanupClazz.getName() + "' not found in "
                + target.getClass().getName());
    }
}
