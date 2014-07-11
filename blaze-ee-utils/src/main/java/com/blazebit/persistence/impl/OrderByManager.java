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
import com.blazebit.persistence.expression.ExpressionUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ccbem
 */
public class OrderByManager extends AbstractManager {
    private final List<OrderByInfo> orderByInfos = new ArrayList<OrderByInfo>();
    
    OrderByManager(QueryGenerator queryGenerator, ParameterManager parameterManager) {
        super(queryGenerator, parameterManager);
    }
    
    void orderBy(String expression, boolean ascending, boolean nullFirst){
        Expression exp = ExpressionUtils.parse(expression);
        orderByInfos.add(new OrderByInfo(exp, ascending, nullFirst));
        
        registerParameterExpressions(exp);
    }
    
    void acceptVisitor(Expression.Visitor v){
        for (OrderByInfo orderBy : orderByInfos) {
            orderBy.getExpression().accept(v);
        }
    }
    
    void applyTransformer(ArrayExpressionTransformer transformer){
        for (OrderByInfo orderBy : orderByInfos) {
            orderBy.setExpression(transformer.transform(orderBy.getExpression()));
        }
    }
    
    String buildOrderBy() {
        
        if (orderByInfos.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        queryGenerator.setQueryBuffer(sb);
        sb.append(" ORDER BY ");
        Iterator<OrderByInfo> iter = orderByInfos.iterator();
        applyOrderBy(sb, iter.next());
        while (iter.hasNext()) {
            sb.append(", ");
            applyOrderBy(sb, iter.next());
        }
        return sb.toString();
    }

    private void applyOrderBy(StringBuilder sb, OrderByInfo orderBy) {
        orderBy.getExpression().accept(queryGenerator);
        if (!orderBy.ascending) {
            sb.append(" DESC");
        } else {
            sb.append(" ASC");
        }
        if (orderBy.nullFirst) {
            sb.append(" NULLS FIRST");
        } else {
            sb.append(" NULLS LAST");
        }
    }
    
    private static class OrderByInfo extends NodeInfo {

        private boolean ascending;
        private boolean nullFirst;

        public OrderByInfo(Expression expression, boolean ascending, boolean nullFirst) {
            super(expression);
            this.ascending = ascending;
            this.nullFirst = nullFirst;
        }
    }
}
