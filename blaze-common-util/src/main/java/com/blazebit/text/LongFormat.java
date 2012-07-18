/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class LongFormat extends AbstractFormat<Long> {

	private static final long serialVersionUID = 1L;

	public LongFormat() {
		super(Long.class);
	}

	public Long parse(String value, ParserContext context) {
		return Long.parseLong(value);
	}
}
