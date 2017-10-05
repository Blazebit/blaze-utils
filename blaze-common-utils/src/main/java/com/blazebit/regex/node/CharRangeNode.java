package com.blazebit.regex.node;

public class CharRangeNode extends AbstractNode {

    private final char start;
    private final char end;

    public CharRangeNode(char start, char end) {
        this.start = start;
        this.end = end;
    }

    public char getStart() {
        return start;
    }

    public char getEnd() {
        return end;
    }
}
