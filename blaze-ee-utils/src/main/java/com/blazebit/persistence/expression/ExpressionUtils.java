/*
 * Copyright 2014 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blazebit.persistence.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author cpbec
 */
public final class ExpressionUtils {

    private static final String PATH_ELEMENT = "([a-zA-Z_][\\w]*)";
    private static final String SIMPLE_PATH = PATH_ELEMENT + "(\\." + PATH_ELEMENT + ")*";
    private static final String PARAMETER_OR_PATH = "(\\:[a-zA-Z_][\\w]*) | (" + SIMPLE_PATH + ")";
    private static final String INDEX = "(\\[(" + PARAMETER_OR_PATH + ")\\])?";
    private static final String PATH = PATH_ELEMENT + INDEX + "(\\." + PATH_ELEMENT + INDEX + ")*";
    private static final ThreadLocal<Pattern> expressionExtractor = new ThreadLocal<Pattern>() {
        @Override
        protected Pattern initialValue() {
            return Pattern.compile(PATH);
        }
    };
    private static final TreeSet<String> KEYWORDS = new TreeSet<String>(Arrays.asList(
        "AVG", "MAX", "MIN", "SUM", "COUNT", "DISTINCT", "LENGTH", "LOCATE", "ABS", "SQRT", "MOD", "SIZE", "CURRENT_DATE",
        "CURRENT_TIME", "CURRENT_TIMESTAMP", "CONCAT", "SUBSTRING", "TRIM", "LEADING", "TRAILING", "BOTH", "FROM", "LOWER",
        "UPPER"));

    private static void parsePath(List<Expression> expressions, String expression, StringBuilder sb, Matcher matcher) {
        // TODO: rewrite parser to support arbitrary depth of paths
        String candidate = expression.substring(matcher.start(), matcher.end());

        if (KEYWORDS.contains(candidate.toUpperCase())) {
            sb.append(candidate);
        } else {
            if (sb.length() > 0) {
                expressions.add(new FooExpression(sb.toString()));
                sb.setLength(0);
            }

            String base = matcher.group(1);
            String index = matcher.group(4);

            if (index == null) {
                expressions.add(new PropertyExpression(base));
            } else {
                index = index.trim();
                Expression indexExpression;

                if (index.charAt(0) == ':') {
                    indexExpression = new ParameterExpression(index.substring(1));
                } else {
                    indexExpression = new PropertyExpression(index);
                }

                expressions.add(new ArrayExpression(new PropertyExpression(base), indexExpression));
            }
        }
    }

    public static Expression parse(String expression) {
        final List<Expression> expressions = new ArrayList<Expression>();
        final Matcher matcher = expressionExtractor.get().matcher(expression);
        StringBuilder sb = new StringBuilder();

        if (matcher.matches()) {
            matcher.find(0);
            parsePath(expressions, expression, sb, matcher);
        } else {
            int start = 0;

            while (start < expression.length() && matcher.find(start)) {
                if (matcher.start() != start) {
                    sb.append(expression.substring(start, matcher.start()));
                }
                
                parsePath(expressions, expression, sb, matcher);
                start = matcher.end();
            }

            if (start == 0) {
                throw new IllegalArgumentException("Just literals are not allowed!");
            } else if (start != expression.length()) {
                sb.append(expression.substring(start, expression.length()));
            }

            if (sb.length() > 0) {
                expressions.add(new FooExpression(sb.toString()));
            }
        }

        return new CompositeExpression(expressions);
    }
}
