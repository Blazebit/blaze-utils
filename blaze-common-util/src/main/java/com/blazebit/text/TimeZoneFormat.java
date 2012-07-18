/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.util.TimeZone;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class TimeZoneFormat extends AbstractFormat<TimeZone> {
	private static final long serialVersionUID = 1L;

	public TimeZoneFormat() {
		super(TimeZone.class);
	}

	public TimeZone parse(String value, ParserContext context) {
		return TimeZone.getTimeZone(value);
	}

}
