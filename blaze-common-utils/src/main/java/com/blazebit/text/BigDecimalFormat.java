/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.math.BigDecimal;

/**
 * @author Christian Beikov
 * @since 0.1.2
 */
public class BigDecimalFormat extends AbstractFormat<BigDecimal> {

    private static final long serialVersionUID = 1L;

    public BigDecimalFormat() {
        super(BigDecimal.class);
    }

    public BigDecimal parse(String value, ParserContext context) {
        return new BigDecimal(value);
    }
}
