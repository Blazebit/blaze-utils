package com.blazebit.regex;

import com.blazebit.regex.node.*;

public class Pattern {

    private final String pattern;
    private final Node root;
    private int cursor = 0;

    public Pattern(String pattern) {
        this.pattern = pattern;
        this.root = parseUnion();
    }

    public static Node parse(String pattern) {
        return new Pattern(pattern).root;
    }

    private boolean hasNext() {
        return cursor < pattern.length();
    }

    private char next() {
        if (!hasNext()) {
            throw new IllegalArgumentException("Unexpected end of pattern");
        }

        return pattern.charAt(cursor++);
    }

    private char next(String s) {
        if (!hasNext()) {
            return 0;
        } else if (s.indexOf(pattern.charAt(cursor)) != -1) {
            return pattern.charAt(cursor++);
        }

        return 0;
    }

    private boolean peek(String s) {
        if (!hasNext()) {
            return false;
        } else if (s.indexOf(pattern.charAt(cursor)) != -1) {
            return true;
        }

        return false;
    }

    private boolean match(char c) {
        if (!hasNext()) {
            return false;
        } else if (pattern.charAt(cursor) == c) {
            cursor++;
            return true;
        }

        return false;
    }

    private Node parseUnion() {
        OrNode rootNode = new OrNode();

        do {
            rootNode.add(parseTerm());
        } while (match('|'));

        return rootNode;
    }

    private Node parseTerm() {
        Node e = parseRepeat();

        if (hasNext() && !peek(")|")) {
            e.setNext(parseTerm());
        }

        return e;
    }

    private Node parseRepeat() {
        Node e = parseCharacterClass();
        char c;

        while ((c = next("?*+{")) != 0) {
            switch (c) {
                case '?':
                    e = new OptionalNode(e);
                    break;
                case '*':
                    e = new RepeatNode(e);
                    break;
                case '+':
                    e = new RepeatNode(e, 1);
                    break;
                case '{':
                    int n = parseInteger();

                    if (n == -1) {
                        throw new IllegalArgumentException(
                                "integer expected at position " + cursor);
                    }

                    int m = match(',') ? parseInteger() : n;

                    if (!match('}')) {
                        throw new IllegalArgumentException(
                                "expected '}' at position " + cursor);
                    }

                    if (m == -1) {
                        e = new RepeatNode(e, n);
                    } else {
                        e = new RepeatNode(e, n, m);
                    }

                    break;
            }
        }

        return e;
    }

    private int parseInteger() {
        StringBuilder sb = new StringBuilder();
        int start = cursor;

        while (peek("0123456789")) {
            sb.append(next());
        }

        if (start == cursor) {
            return -1;
        }

        return Integer.parseInt(sb.toString());
    }

    private Node parseCharacterClass() {
        if (match('[')) {
            boolean negate = false;

            if (match('^')) {
                negate = true;
            }

            Node node = parseCharacterClasses();

            if (negate) {
                node = new ComplementNode(node);
            }

            if (!match(']')) {
                throw new IllegalArgumentException("expected ']' at position "
                        + cursor);
            }

            return node;
        } else
            return parseAtom();
    }

    private Node parseCharacterClasses() {
        OrNode node = new OrNode();

        do {
            node.add(parseCharacterRange());
        } while (hasNext() && !peek("]"));

        return node;
    }

    private Node parseCharacterRange() {
        char c = parseCharacter();

        if (match('-')) {
            if (peek("]")) {
                OrNode node = new OrNode();
                node.add(new CharNode(c));
                node.add(new CharNode('-'));
                return node;
            } else {
                return new CharRangeNode(c, parseCharacter());
            }
        } else {
            return new CharNode(c);
        }
    }

    private Node parseAtom() throws IllegalArgumentException {
        if (match('.')) {
            return new DotNode();
        } else if (match('(')) {
            if (match(')')) {
                return new EmptyNode();
            }

            Node e = parseUnion();

            if (!match(')')) {
                throw new IllegalArgumentException("expected ')' at position "
                        + cursor);
            }

            return e;
        } else
            return new CharNode(parseCharacter());
    }

    private char parseCharacter() throws IllegalArgumentException {
        match('\\');
        return next();
    }

}
