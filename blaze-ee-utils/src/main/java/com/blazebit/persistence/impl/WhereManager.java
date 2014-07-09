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

import com.blazebit.persistence.RestrictionBuilder;
import com.blazebit.persistence.WhereOrBuilder;
import com.blazebit.persistence.expression.ExpressionUtils;
import com.blazebit.persistence.predicate.Predicate.Visitor;

/**
 *
 * @author ccbem
 */
public class WhereManager<U> extends PredicateManager<U> {

    WhereManager(QueryGenerator queryGenerator) {
        super(queryGenerator);
    }

    @Override
    protected String getClauseName() {
        return "WHERE";
    }

    WhereOrBuilder<U> whereOr(AbstractCriteriaBuilder<?, ?> builder) {
        return rootPredicate.startBuilder(new WhereOrBuilderImpl<U>((U) builder, rootPredicate));
    }

    String buildClause(boolean generateRequiredMapKeyFiltersOnly) {
        queryGenerator.setGenerateRequiredMapKeyFiltersOnly(generateRequiredMapKeyFiltersOnly);
        String clause = super.buildClause();
        queryGenerator.setGenerateRequiredMapKeyFiltersOnly(false);
        return clause;
    }

}
