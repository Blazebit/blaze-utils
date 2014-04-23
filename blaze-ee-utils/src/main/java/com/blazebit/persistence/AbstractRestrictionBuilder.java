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
package com.blazebit.persistence;

import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.ExpressionUtils;
import com.blazebit.persistence.expression.ParameterExpression;
import com.blazebit.persistence.expression.PropertyExpression;
import com.blazebit.persistence.predicate.EqPredicate;
import com.blazebit.persistence.predicate.IsNullPredicate;
import com.blazebit.persistence.predicate.LikePredicate;
import com.blazebit.persistence.predicate.NotPredicate;
import com.blazebit.persistence.predicate.Predicate;
import java.util.List;

/**
 *
 * @author cpbec
 */
public abstract class AbstractRestrictionBuilder<T> implements RestrictionBuilder<T> {
    
    private final Expression leftExpression;
    protected Predicate predicate;

    public AbstractRestrictionBuilder(Expression leftExpression) {
        this.leftExpression = leftExpression;
    }

    @Override
    public Predicate getPredicate() {
        return predicate;
    }
    
    protected abstract T chain(Predicate predicate);

    @Override
    public T between(Object start, Object end) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T notBetween(Object start, Object end) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public QuantifiableBinaryPredicateBuilder<T> equalTo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T equalTo(Object value) {
        return chain(new EqPredicate(leftExpression, new ParameterExpression(value)));
    }

    @Override
    public T equalToExpression(String expression) {
        return chain(new EqPredicate(leftExpression, ExpressionUtils.parse(expression)));
    }

    @Override
    public QuantifiableBinaryPredicateBuilder<T> notEqualTo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T notEqualTo(Object value) {
        return chain(new NotPredicate(new EqPredicate(leftExpression, new ParameterExpression(value))));
    }

    @Override
    public T notEqualToExpression(String expression) {
        return chain(new NotPredicate(new EqPredicate(leftExpression, ExpressionUtils.parse(expression))));
    }

    @Override
    public QuantifiableBinaryPredicateBuilder<T> greaterThan() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T greaterThan(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T greaterThanExpression(String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public QuantifiableBinaryPredicateBuilder<T> greaterOrEqualThan() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T greaterOrEqualThan(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T greaterOrEqualThanExpression(String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public QuantifiableBinaryPredicateBuilder<T> lowerThan() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T lowerThan(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T lowerThanExpression(String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public QuantifiableBinaryPredicateBuilder<T> lowerOrEqualThan() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T lowerOrEqualThan(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T lowerOrEqualThanExpression(String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T in(List<?> values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T inElements(String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T inIndices(String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T existsElements(String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T existsIndices(String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public T isNull() {
        return chain(new IsNullPredicate(leftExpression));
    }

    @Override
    public T isNotNull() {
        return chain(new NotPredicate(new IsNullPredicate(leftExpression)));
    }

    @Override
    public T isEmpty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T isNotEmpty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T isMemberOf(String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T isNotMemberOf(String expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public T like(String value) {
        return chain(new LikePredicate(leftExpression, new ParameterExpression(value), true, null));
    }

    @Override
    public T like(String value, boolean caseSensitive, Character escapeCharacter) {
        return chain(new LikePredicate(leftExpression, new ParameterExpression(value), caseSensitive, escapeCharacter));
    }
    
    @Override
    public T likeExpression(String expression) {
        return chain(new LikePredicate(leftExpression, ExpressionUtils.parse(expression), true, null));
    }

    @Override
    public T likeExpression(String expression, boolean caseSensitive, Character escapeCharacter) {
        return chain(new LikePredicate(leftExpression, ExpressionUtils.parse(expression), caseSensitive, escapeCharacter));
    }

    @Override
    public T notLike(String value) {
        return chain(new NotPredicate(new LikePredicate(leftExpression, new ParameterExpression(value), true, null)));
    }

    @Override
    public T notLike(String value, boolean caseSensitive, Character escapeCharacter) {
        return chain(new NotPredicate(new LikePredicate(leftExpression, new ParameterExpression(value), caseSensitive, escapeCharacter)));
    }

    @Override
    public T notLikeExpression(String expression) {
        return chain(new NotPredicate(new LikePredicate(leftExpression, ExpressionUtils.parse(expression), true, null)));
    }

    @Override
    public T notLikeExpression(String expression, boolean caseSensitive, Character escapeCharacter) {
        return chain(new NotPredicate(new LikePredicate(leftExpression, ExpressionUtils.parse(expression), caseSensitive, escapeCharacter)));
    }
    
}