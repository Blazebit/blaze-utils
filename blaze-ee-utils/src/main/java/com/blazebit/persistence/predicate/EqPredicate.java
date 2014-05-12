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

import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.ExpressionUtils;
import com.blazebit.persistence.expression.ParameterExpression;
import com.blazebit.persistence.impl.BuilderEndedListener;

/**
 *
 * @author cpbec
 */
public class EqPredicate  extends QuantifiableBinaryExpressionPredicate {

    public EqPredicate(Expression left, Expression right) {
        super(left, right, PredicateQuantifier.ONE);
    }
    
    public EqPredicate(Expression left, Expression right, PredicateQuantifier quantifier) {
        super(left, right, quantifier);
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
    
    public static class EqPredicateBuilder<T> extends AbstractQuantifiablePredicateBuilder<T> {

        public EqPredicateBuilder(T result, BuilderEndedListener listener, Expression leftExpression, boolean wrapNot) {
            super(result, listener, leftExpression, wrapNot);
        }
       
        @Override
        public T value(Object value) {
            return chain(new EqPredicate(leftExpression, new ParameterExpression(value), quantifier));
        }

        @Override
        public T expression(String expression) {
            return chain(new EqPredicate(leftExpression, ExpressionUtils.parse(expression), quantifier));
        }
        
    }
    
}
