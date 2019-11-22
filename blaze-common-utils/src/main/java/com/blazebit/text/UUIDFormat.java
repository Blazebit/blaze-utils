/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.util.UUID;

/**
 * @author Christian Beikov
 * @since 0.1.9
 */
public class UUIDFormat extends AbstractFormat<UUID> {
    private static final long serialVersionUID = 1L;

    public UUIDFormat() {
        super(UUID.class);
    }

    public UUID parse(String value, ParserContext context) {
        return UUID.fromString(value);
    }

}
