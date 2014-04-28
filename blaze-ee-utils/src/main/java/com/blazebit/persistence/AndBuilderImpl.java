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
import com.blazebit.persistence.predicate.AndPredicate;
import com.blazebit.persistence.predicate.Predicate;
import com.blazebit.persistence.predicate.PredicateBuilder;

/**
 *
 * @author cpbec
 */
public class AndBuilderImpl<T extends BuilderEndedListener> extends AbstractBuilderEndedListener implements AndBuilder<T> {
    
    private final T result;
    private final AndPredicate predicate;
    
    public AndBuilderImpl(T result) {
        this.result = result;
        this.predicate = new AndPredicate();
    }
    
    @Override
    public T endAnd() {
        verifyBuilderEnded();
        result.onBuilderEnded(this);
        return result;
    }

    @Override
    public Predicate getPredicate() {
        return predicate;
    }
    
    @Override
    public void onBuilderEnded(PredicateBuilder builder) {
        super.onBuilderEnded(builder);
        predicate.getChildren().add(builder.getPredicate());
    }

    @Override
    public OrBuilder<AndBuilderImpl<T>> whereOr() {
        return startBuilder(new OrBuilderImpl<AndBuilderImpl<T>>(this));
    }

    @Override
    public RestrictionBuilder<? extends AndBuilder<T>> where(String property) {
        return startBuilder(new RestrictionBuilderImpl<AndBuilderImpl<T>>(this, ExpressionUtils.parse(property)));
    }
}
