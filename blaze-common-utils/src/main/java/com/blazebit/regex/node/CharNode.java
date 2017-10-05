package com.blazebit.regex.node;

public class CharNode extends AbstractNode {

    private final char character;

    public CharNode(char character) {
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }
}
