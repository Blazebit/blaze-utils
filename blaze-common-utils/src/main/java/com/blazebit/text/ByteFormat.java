/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class ByteFormat extends AbstractFormat<Byte> {
	private static final long serialVersionUID = 1L;

	public ByteFormat() {
		super(Byte.class);
	}

	public Byte parse(String value, ParserContext context) {
		return Byte.parseByte(value);
	}

}
