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

import com.blazebit.persistence.expression.ArrayExpression;
import com.blazebit.persistence.expression.CompositeExpression;
import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.FooExpression;
import com.blazebit.persistence.expression.PathElementExpression;
import com.blazebit.persistence.expression.PathExpression;
import com.blazebit.persistence.expression.PropertyExpression;
import com.blazebit.persistence.predicate.EqPredicate;
import java.util.ArrayList;

/**
 *
 * @author cpbec
 */
public class ArrayExpressionTransformer {

    public static Expression transform(Expression original, AbstractCriteriaBuilder<?, ?> builder) {
        // TODO: transform the original expression and apply changes in the criteria builder
        if(original instanceof FooExpression){
            return original;
        }
        
        if (original instanceof CompositeExpression) {
            CompositeExpression composite = (CompositeExpression) original;
            CompositeExpression transformed = new CompositeExpression(new ArrayList<Expression>());
            for (Expression e : composite.getExpressions()) {
                transformed.getExpressions().add(transform(e, builder));
            }
            return transformed;
        }

        if (!(original instanceof PathExpression)) {
            throw new IllegalArgumentException("Probably a programming error");
        }

        PathExpression path = (PathExpression) original;

        ArrayExpression arrayExp = null;
        PathExpression transformedPath = new PathExpression(new ArrayList<PathElementExpression>());
        for (int i = 0; i < path.getExpressions().size(); i++) {
            Expression expr = path.getExpressions().get(i);
            if (expr instanceof ArrayExpression) {
                arrayExp = (ArrayExpression) expr;

                CompositeExpression keyExpression = new CompositeExpression(new ArrayList<Expression>());
                keyExpression.getExpressions().add(new FooExpression("KEY("));

                PathExpression keyPath = new PathExpression(new ArrayList<PathElementExpression>(transformedPath.getExpressions()));
                keyPath.getExpressions().add(arrayExp.getBase());
                keyExpression.getExpressions().add(keyPath);
                keyExpression.getExpressions().add(new FooExpression(")"));
                builder.addWherePredicate(new EqPredicate(keyExpression, arrayExp.getIndex()));
                
                transformedPath.getExpressions().add(arrayExp.getBase());
            }else{
                transformedPath.getExpressions().add((PropertyExpression)expr);
            }
        }

        if (arrayExp != null) {
            // add value for last array expression
            CompositeExpression valueExpression = new CompositeExpression(new ArrayList<Expression>());
            valueExpression.getExpressions().add(new FooExpression("VALUE("));
            PathExpression valuePath = new PathExpression(new ArrayList<PathElementExpression>(transformedPath.getExpressions()));
            valueExpression.getExpressions().add(valuePath);
            valueExpression.getExpressions().add(new FooExpression(")"));
            return valueExpression;
        }

        return original;
    }
}
