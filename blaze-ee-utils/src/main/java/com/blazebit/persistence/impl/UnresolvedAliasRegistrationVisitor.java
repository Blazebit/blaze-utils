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

import com.blazebit.persistence.expression.ExpressionUtils;
import com.blazebit.persistence.expression.PathExpression;

/**
 *
 * @author ccbem
 */
public class UnresolvedAliasRegistrationVisitor extends VisitorAdapter {

    private AbstractCriteriaBuilder<?, ?> builder;

    public UnresolvedAliasRegistrationVisitor(AbstractCriteriaBuilder<?, ?> builder) {
        this.builder = builder;
    }

    @Override
    public void visit(PathExpression expression) {
        String path = expression.getPath();
        if (!builder.startsAtRootAlias(path)) {
                // either we have an implicit root alias or we have a new
            // alias that can not yet be resolved --> we don't know, add
            // to unresolved for later resolving

            // first part of the path becomes alias
            String temporaryAlias = ExpressionUtils.getFirstPathElement(path);
            builder.addUnresolvedAlias(temporaryAlias, new NodeInfo(expression));
        }
    }

}
