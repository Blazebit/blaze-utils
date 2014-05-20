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

import com.blazebit.persistence.CaseWhenAndThenBuilder;
import com.blazebit.persistence.CaseWhenBuilder;
import com.blazebit.persistence.CaseWhenOrThenBuilder;
import com.blazebit.persistence.CaseWhenThenBuilder;
import com.blazebit.persistence.RestrictionBuilder;
import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.ExpressionUtils;
import com.blazebit.persistence.predicate.Predicate;
import com.blazebit.persistence.predicate.PredicateBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cpbec
 */
public class CaseWhenBuilderImpl<T> extends AbstractBuilderEndedListener implements CaseWhenBuilder<T>, CaseWhenThenBuilder<CaseWhenBuilder<T>> {
    
    private final T result;
    private final List<Object[]> whenThenClauses;
    private Expression elseExpression;
    
    private Predicate whenPredicate;
    
    public CaseWhenBuilderImpl(T result) {
        this.result = result;
        this.whenThenClauses = new ArrayList<Object[]>();
    }
    
    @Override
    public RestrictionBuilder<CaseWhenThenBuilder<CaseWhenBuilder<T>>> when(String expression) {
        return startBuilder(new RestrictionBuilderImpl<CaseWhenThenBuilder<CaseWhenBuilder<T>>>(this, this, ExpressionUtils.parse(expression)));
    }
    
    @Override
    public CaseWhenBuilder<T> then(String expression) {
        Object[] whenThenClause = new Object[2];
        whenThenClause[0] = this.whenPredicate;
        whenThenClause[1] = ExpressionUtils.parseScalarExpression(expression);
        whenThenClauses.add(whenThenClause);
        return this;
    }
    
    @Override
    public CaseWhenAndThenBuilder<CaseWhenBuilder<T>> whenAnd() {
        return new CaseWhenAndThenBuilderImpl<CaseWhenBuilder<T>>(this);
    }
    
    @Override
    public CaseWhenOrThenBuilder<CaseWhenBuilder<T>> whenOr() {
        return new CaseWhenOrThenBuilderImpl<CaseWhenBuilder<T>>(this);
    }
    
    @Override
    public T thenElse(String elseExpression) {
        this.elseExpression = ExpressionUtils.parseScalarExpression(elseExpression);
        return result;
    }
    
    @Override
    public void onBuilderEnded(PredicateBuilder o) {
        super.onBuilderEnded(o);
        this.whenPredicate = o.getPredicate();
    }
    
    private void verifyWhenThenBuilderEnded() {
        verifyBuilderEnded();
        if (whenPredicate != null) {
            throw new IllegalStateException("A builder was not ended properly.");
        }
    }
}
