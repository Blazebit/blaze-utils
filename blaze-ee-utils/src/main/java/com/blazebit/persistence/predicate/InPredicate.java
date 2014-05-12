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
package com.blazebit.persistence.predicate;

import com.blazebit.persistence.impl.BuilderEndedListener;
import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.ExpressionUtils;
import com.blazebit.persistence.expression.ParameterExpression;

/**
 *
 * @author cpbec
 */
public class InPredicate extends BinaryExpressionPredicate {

    private final PredicateQuantifier quantifier;

    public InPredicate(Expression left, Expression right) {
        this(left, right, PredicateQuantifier.ONE);
    }

    public InPredicate(Expression left, Expression right, PredicateQuantifier quantifier) {
        super(left, right);
        this.quantifier = quantifier;
    }

    public PredicateQuantifier getQuantifier() {
        return quantifier;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class InPredicateBuilder<T extends BuilderEndedListener> extends AbstractQuantifiablePredicateBuilder<T> {

        public InPredicateBuilder(T result, BuilderEndedListener listener, Expression leftExpression) {
            super(result, listener, leftExpression, false);
        }

        @Override
        public T value(Object value) {
            return chain(new InPredicate(leftExpression, new ParameterExpression(value), quantifier));
        }

        @Override
        public T expression(String expression) {
            return chain(new InPredicate(leftExpression, ExpressionUtils.parse(expression), quantifier));
        }
    }
}
