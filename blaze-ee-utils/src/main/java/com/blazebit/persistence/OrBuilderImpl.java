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

import com.blazebit.persistence.expression.ExpressionUtils;
import com.blazebit.persistence.expression.PropertyExpression;
import com.blazebit.persistence.predicate.OrPredicate;
import com.blazebit.persistence.predicate.Predicate;
import com.blazebit.persistence.predicate.PredicateBuilder;

/**
 *
 * @author cpbec
 */
public class OrBuilderImpl<T extends BuilderEndedListener> extends AbstractBuilderEndedListener implements OrBuilder<T> {

    private final T result;
    private final OrPredicate predicate;
    
    public OrBuilderImpl(T result) {
        this.result = result;
        this.predicate = new OrPredicate();
    }
    
    @Override
    public T endOr() {
        result.onBuilderEnded(this);
        return result;
    }

    @Override
    public Predicate getPredicate() {
        return predicate;
    }
    
    @Override
    public void onBuilderEnded(PredicateBuilder o) {
        super.onBuilderEnded(o);
        predicate.getChildren().add(o.getPredicate());
    }

    @Override
    public AndBuilder<OrBuilderImpl<T>> whereAnd() {
        AndBuilder<OrBuilderImpl<T>> builder =  new AndBuilderImpl<OrBuilderImpl<T>>(this);
        startedBuilders.add(builder);
        return builder;
    }

    @Override
    public RestrictionBuilder<? extends OrBuilder<T>> where(String property) {
        RestrictionBuilder<OrBuilderImpl<T>> builder = new RestrictionBuilderImpl<OrBuilderImpl<T>>(this, ExpressionUtils.parse(property));
        startedBuilders.add(builder);
        return builder;
    }
    
}
