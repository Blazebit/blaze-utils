/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class ShortFormat extends AbstractDecimalFormat<Short> {

	private static final long serialVersionUID = 1L;

	public ShortFormat() {
		super(Short.class);
	}

	public Short parse(String value, ParserContext context) {
		return Short.parseShort(value);
	}

}
