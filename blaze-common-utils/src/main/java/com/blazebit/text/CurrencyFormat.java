/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.util.Currency;

/**
 * @author Christian Beikov
 * @since 0.1.2
 */
public class CurrencyFormat extends AbstractFormat<Currency> {

    private static final long serialVersionUID = 1L;

    public CurrencyFormat() {
        super(Currency.class);
    }

    public Currency parse(String value, ParserContext context) {
        return Currency.getInstance(value);
    }
}
