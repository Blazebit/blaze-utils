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

import com.blazebit.persistence.expression.CompositeExpression;
import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.ExpressionUtils;
import com.blazebit.persistence.expression.FooExpression;
import com.blazebit.persistence.expression.ParameterExpression;
import com.blazebit.persistence.expression.PathExpression;
import com.blazebit.persistence.expression.PropertyExpression;
import com.blazebit.persistence.predicate.AndPredicate;
import com.blazebit.persistence.predicate.BetweenPredicate;
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

/**
 *
 * @author ccbem
 */
public class JoinVisitor extends VisitorAdapter {

    private final JoinManager joinManager;
    private final SelectManager<?> selectManager;
    private boolean joinWithObjectLeafAllowed = true;
    private boolean fromSelect = false;

    public JoinVisitor(JoinManager joinManager, SelectManager<?> selectManager) {
        this.joinManager = joinManager;
        this.selectManager = selectManager;
    }

    public boolean isFromSelect() {
        return fromSelect;
    }

    public void setFromSelect(boolean fromSelect) {
        this.fromSelect = fromSelect;
    }

    @Override
    public void visit(PathExpression expression) {
        String path = expression.getPath();
        String potentialSelectAlias = ExpressionUtils.getFirstPathElement(path);

        // do not join select aliases
        if (selectManager.getSelectAliasToInfoMap().containsKey(potentialSelectAlias)) {
            if (!potentialSelectAlias.equals(path)) {
                throw new IllegalStateException("Path starting with select alias not allowed");
            }
            return;
        }

        joinManager.implicitJoin(expression, joinWithObjectLeafAllowed, fromSelect);
    }

    public boolean isJoinWithObjectLeafAllowed() {
        return joinWithObjectLeafAllowed;
    }

    public void setJoinWithObjectLeafAllowed(boolean joinWithObjectLeafAllowed) {
        this.joinWithObjectLeafAllowed = joinWithObjectLeafAllowed;
    }
}
