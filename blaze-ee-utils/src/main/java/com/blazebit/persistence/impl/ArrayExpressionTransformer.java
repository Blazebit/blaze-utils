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
import com.blazebit.persistence.expression.PathExpression;
import com.blazebit.persistence.expression.PropertyExpression;
import com.blazebit.persistence.predicate.EqPredicate;
import java.util.ArrayList;

/**
 *
 * @author cpbec
 */
public class ArrayExpressionTransformer {

    public static Expression transform(Expression original, CriteriaBuilderImpl<?> builder) {
        // TODO: transform the original expression and apply changes in the criteria builder
        if (original instanceof PathExpression) {
            // Nothing to transform here
            return original;
        }

        if (!(original instanceof CompositeExpression)) {
            throw new IllegalArgumentException("Probably a programming error");
        }
        
        CompositeExpression comp = (CompositeExpression) original;
        
        for (int i = 0; i < comp.getExpressions().size(); i++) {
            Expression expr = comp.getExpressions().get(i);
            if (expr instanceof ArrayExpression) {
                ArrayExpression arrayExp = (ArrayExpression) expr;
                if(i == 0){
//                    comp.getExpressions().
                }
                // i-1 will get "VALUE(" appended
                comp.getExpressions().set(i, arrayExp.getBase());
                // i+1 will get ")" prepended
                
                // Add implicit join
                builder.implicitJoin(arrayExp.getBase(), true);
                
                // Add where condition
                CompositeExpression keyExpression = new CompositeExpression(new ArrayList<Expression>());
                keyExpression.getExpressions().add(new FooExpression("KEY("));
                keyExpression.getExpressions().add(arrayExp.getBase());
                keyExpression.getExpressions().add(new FooExpression(")"));
                builder.addWherePredicate(new EqPredicate(keyExpression, arrayExp.getIndex()));
            }
        }
        
        return original;
    }
}
