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
package com.blazebit.persistence.impl;

import com.blazebit.persistence.ParameterNameGenerator;
import com.blazebit.persistence.expression.ArrayExpression;
import com.blazebit.persistence.expression.CompositeExpression;
import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.FooExpression;
import com.blazebit.persistence.expression.ParameterExpression;
import com.blazebit.persistence.expression.PropertyExpression;
import com.blazebit.persistence.predicate.AndPredicate;
import com.blazebit.persistence.predicate.BetweenPredicate;
import com.blazebit.persistence.predicate.BinaryExpressionPredicate;
import com.blazebit.persistence.predicate.EqPredicate;
import com.blazebit.persistence.predicate.GePredicate;
import com.blazebit.persistence.predicate.GtPredicate;
import com.blazebit.persistence.predicate.InPredicate;
import com.blazebit.persistence.predicate.IsEmptyPredicate;
import com.blazebit.persistence.predicate.IsMemberOfPredicate;
import com.blazebit.persistence.predicate.IsNullPredicate;
import com.blazebit.persistence.predicate.LePredicate;
import com.blazebit.persistence.predicate.LikePredicate;
import com.blazebit.persistence.predicate.LtPredicate;
import com.blazebit.persistence.predicate.NotPredicate;
import com.blazebit.persistence.predicate.OrPredicate;
import com.blazebit.persistence.predicate.Predicate;
import com.blazebit.persistence.predicate.PredicateQuantifier;
import com.blazebit.persistence.predicate.QuantifiableBinaryExpressionPredicate;
import java.util.Map;

/**
 *
 * @author ccbem
 */
public class ArrayTransformationVisitor implements Predicate.Visitor, Expression.Visitor {
    
    private final CriteriaBuilderImpl<?> builder;

    public ArrayTransformationVisitor(CriteriaBuilderImpl<?> builder) {
        this.builder = builder;
    }

    @Override
    public void visit(AndPredicate predicate) {
    }

    @Override
    public void visit(OrPredicate predicate) {
    }

    @Override
    public void visit(NotPredicate predicate) {
    }

    @Override
    public void visit(EqPredicate predicate) {
    }

    @Override
    public void visit(IsNullPredicate predicate) {
    }

    @Override
    public void visit(IsEmptyPredicate predicate) {
    }

    @Override
    public void visit(IsMemberOfPredicate predicate) {
    }

    @Override
    public void visit(LikePredicate predicate) {
    }

    @Override
    public void visit(BetweenPredicate predicate) {
    }

    @Override
    public void visit(InPredicate predicate) {
    }

    @Override
    public void visit(GtPredicate predicate) {
    }

    @Override
    public void visit(GePredicate predicate) {
    }

    @Override
    public void visit(LtPredicate predicate) {
    }

    @Override
    public void visit(LePredicate predicate) {
    }

    @Override
    public void visit(PropertyExpression expression) {
    }

    @Override
    public void visit(ParameterExpression expression) {
    }

    @Override
    public void visit(CompositeExpression expression) {
        ArrayExpressionTransformer.transform(expression, builder);
    }

    @Override
    public void visit(FooExpression expression) {
    }

    
}
