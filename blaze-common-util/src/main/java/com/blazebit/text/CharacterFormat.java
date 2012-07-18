/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class CharacterFormat extends AbstractFormat<Character> {

    private static final long serialVersionUID = 1L;

    public CharacterFormat() {
        super(Character.class);
    }

    public Character parse(String value, ParserContext context) {
        return value.charAt(0);
    }
}
