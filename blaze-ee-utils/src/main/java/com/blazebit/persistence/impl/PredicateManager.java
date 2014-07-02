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
import com.blazebit.persistence.predicate.AndPredicate;
import com.blazebit.persistence.predicate.Predicate;
import com.blazebit.persistence.predicate.PredicateBuilder;

/**
 *
 * @author ccbem
 */
public abstract class PredicateManager<U> extends AbstractManager {
    final RootPredicate rootPredicate;

    PredicateManager(QueryGenerator queryGenerator, ArrayExpressionTransformer transformer) {
        super(queryGenerator, transformer);
        this.rootPredicate = new RootPredicate(transformer);
    }

    public RootPredicate getRootPredicate() {
        return rootPredicate;
    }
    
    RestrictionBuilder<U> restrict(AbstractCriteriaBuilder<?, ?> builder, String expression){
        return rootPredicate.startBuilder(new RestrictionBuilderImpl<U>((U) builder, rootPredicate, transformer.transform(ExpressionUtils.parse(expression))));
    }
    
    void verifyBuilderEnded(){
        rootPredicate.verifyBuilderEnded();
    }
    
    void acceptVisitor(Predicate.Visitor v){
        rootPredicate.predicate.accept(v);
    }
    
    String buildClause() {
        StringBuilder sb = new StringBuilder();
        queryGenerator.setQueryBuffer(sb);
        applyPredicate(queryGenerator, sb);
        return sb.toString();
    }
    
    abstract String getClauseName();
    
    void applyPredicate(QueryGenerator queryGenerator, StringBuilder sb){
        if (rootPredicate.predicate.getChildren().isEmpty()) {
            return;
        }
        sb.append(" ").append(getClauseName()).append(" ");
        rootPredicate.predicate.accept(queryGenerator);
    }
    
    static class RootPredicate extends AbstractBuilderEndedListener {

        final AndPredicate predicate;
        private final ArrayTransformationVisitor transformationVisitor;

        public RootPredicate(ArrayExpressionTransformer transformer) {
            this.predicate = new AndPredicate();
            this.transformationVisitor = new ArrayTransformationVisitor(transformer);
        }

        @Override
        public void onBuilderEnded(PredicateBuilder builder) {
            super.onBuilderEnded(builder);
            Predicate pred = builder.getPredicate();

            pred.accept(transformationVisitor);

            predicate.getChildren()
                    .add(pred);
        }
    }
}
