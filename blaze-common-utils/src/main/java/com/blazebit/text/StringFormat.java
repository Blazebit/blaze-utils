/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class StringFormat extends AbstractFormat<String> {

	private static final long serialVersionUID = 1L;

	public StringFormat() {
		super(String.class);
	}

	public String parse(String value, ParserContext context) {
		return value;
	}
}
