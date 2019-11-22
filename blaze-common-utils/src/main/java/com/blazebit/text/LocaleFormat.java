/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import com.blazebit.i18n.LocaleUtils;

import java.util.Locale;

/**
 * @author Christian Beikov
 * @since 0.1.2
 */
public class LocaleFormat extends AbstractFormat<Locale> {
    private static final long serialVersionUID = 1L;

    public LocaleFormat() {
        super(Locale.class);
    }

    public Locale parse(String value, ParserContext context) {
        return LocaleUtils.getLocale(value);
    }

}
