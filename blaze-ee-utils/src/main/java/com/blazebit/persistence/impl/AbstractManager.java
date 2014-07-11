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

import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.ParameterExpression;
import com.blazebit.persistence.predicate.Predicate;

/**
 *
 * @author ccbem
 */
public class AbstractManager {

    final QueryGenerator queryGenerator;
    protected final ParameterManager parameterManager;
    private final VisitorAdapter parameterRegistrationVisitor = new VisitorAdapter() {
        @Override
            public void visit(ParameterExpression expression) {
                if (expression.getValue() != null) {
                    // ParameterExpression was created with an object but no name is set
                    expression.setName(parameterManager.getParamNameForObject(expression.getValue()));
                } else {
                    // Value was not set so we only have an unsatisfied parameter name which we register
                    parameterManager.registerParameterName(expression.getName());
                }
            }
};

    AbstractManager(QueryGenerator queryGenerator, ParameterManager parameterManager) {
        this.queryGenerator = queryGenerator;
        this.parameterManager = parameterManager;
    }

    protected void registerParameterExpressions(Expression expression) {
        expression.accept(parameterRegistrationVisitor);
    }

    protected void registerParameterExpressions(Predicate predicate) {
        predicate.accept(parameterRegistrationVisitor);
    }
}
