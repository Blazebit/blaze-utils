/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class can be used to predefine a getter chain invocation but to be
 * invoked later. It holds the source object on which to invoke the getter chain
 * and the field names with which the getter methods are determined.
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class LazyGetterMethod {
    private final Object source;
    private final PropertyPathExpression<Object, Object> expression;

    /**
     * Constructs a LazyGetterMethod object for the given source object and
     * field names as a string separated by '.' (dots). Using this constructor
     * is equal to #
     * {@link LazyGetterMethod#LazyGetterMethod(java.lang.Object, java.lang.String[]) }
     * with the second parameter <code>fieldNames.split("\\.")</code>.
     *
     * @param source     The object on which to invoke the first getter
     * @param fieldNames The field names which should be used for the getter
     *                   determination
     */
    @SuppressWarnings("unchecked")
    public LazyGetterMethod(Object source, String fieldNames) {
        this.source = source;
        this.expression = (PropertyPathExpression<Object, Object>) ExpressionUtils
                .getExpression(source.getClass(), fieldNames);
    }

    /**
     * Invokes the getter chain based on the source object. First the source
     * object is used as invocation target for the first getter then the results
     * of the previous operations will be used for the invocation.
     * <p>
     * Example of how the chaining works:
     * <p>
     * class A{ B getB(){ // return b element } }
     * <p>
     * class B{ String getA(){ // return a element } }
     * <p>
     * new LazyGetterMethod(new A(), "a.b").invoke()
     * <p>
     * is equal to
     * <p>
     * new A().getB().getA()
     *
     * @return The result of the last getter in the chain
     * @throws InvocationTargetException {@link Method#invoke(java.lang.Object, java.lang.Object[]) }
     * @throws IllegalAccessException    {@link Method#invoke(java.lang.Object, java.lang.Object[]) }
     */
    public Object invoke() throws InvocationTargetException,
            IllegalAccessException {
        return expression.getValue(source);
    }
}
