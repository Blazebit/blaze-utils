/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class URLFormat extends AbstractFormat<URL> {
    private static final long serialVersionUID = 1L;

    public URLFormat() {
        super(URL.class);
    }

    public URL parse(String value, ParserContext context) {
        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
