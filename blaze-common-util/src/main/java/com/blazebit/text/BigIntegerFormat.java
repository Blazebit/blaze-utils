/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.math.BigInteger;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class BigIntegerFormat extends AbstractFormat<BigInteger> {
    private static final long serialVersionUID = 1L;

    public BigIntegerFormat() {
        super(BigInteger.class);
    }

    public BigInteger parse(String value, ParserContext context) {
        return new BigInteger(value);
    }

}
