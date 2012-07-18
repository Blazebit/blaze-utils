/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.util.Locale;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class LocaleFormat extends AbstractFormat<Locale> {
    private static final long serialVersionUID = 1L;

    public LocaleFormat() {
        super(Locale.class);
    }

    public Locale parse(String value, ParserContext context) {
        return new Locale(value);
    }
    
    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo){
        toAppendTo.append(((Locale)obj).getLanguage());
        return toAppendTo;
    }

}
