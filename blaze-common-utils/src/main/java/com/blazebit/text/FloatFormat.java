/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class FloatFormat extends AbstractFormat<Float> {

	private static final long serialVersionUID = 1L;

	public FloatFormat() {
		super(Float.class);
	}

	public Float parse(String value, ParserContext context) {
		return Float.parseFloat(value);
	}
}
