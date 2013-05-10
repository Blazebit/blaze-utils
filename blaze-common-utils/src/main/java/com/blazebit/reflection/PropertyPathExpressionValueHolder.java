/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import com.blazebit.lang.ValueHolder;

/**
 * This class can be used to predefine a getter chain invocation but to be
 * invoked later. It holds the source object on which to invoke the getter chain
 * and the field names with which the getter methods are determined.
 * 
 * @author Christian Beikov
 * @since 1.0
 */
public class PropertyPathExpressionValueHolder<X, Y> implements ValueHolder<Y> {
	private final X source;
	private final PropertyPathExpression<X, Y> expression;

	@SuppressWarnings("unchecked")
	public PropertyPathExpressionValueHolder(X source, String propertyPath) {
		this(source, (PropertyPathExpression<X, Y>) ExpressionUtils
				.getExpression(source.getClass(), propertyPath));
	}

	public PropertyPathExpressionValueHolder(X source,
			PropertyPathExpression<X, Y> expression) {
		this.source = source;
		this.expression = expression;
	}

	@Override
	public Y getValue() {
		return expression.getValue(source);
	}

	public Y getNullSafeValue() {
		return expression.getNullSafeValue(source);
	}

	@Override
	public void setValue(Y value) {
		expression.setValue(source, value);
	}
}
