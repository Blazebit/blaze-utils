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
import com.blazebit.persistence.expression.Expression.Visitor;
import com.blazebit.persistence.expression.ExpressionUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ccbem
 */
public class GroupByManager extends AbstractManager{
    private final List<NodeInfo> groupByInfos;
    
    GroupByManager(QueryGenerator queryGenerator, ArrayExpressionTransformer transformer) {
        super(queryGenerator, transformer);
        groupByInfos = new ArrayList<NodeInfo>();
    }
    
    void groupBy(String expression){
        Expression exp = transformer.transform(ExpressionUtils.parse(expression));
        groupByInfos.add(new NodeInfo(exp));
    }
    
    String buildGroupBy(){
        StringBuilder sb = new StringBuilder();
        queryGenerator.setQueryBuffer(sb);
        applyGroupBys(queryGenerator, sb, groupByInfos);
        return sb.toString();
    }
    
    void applyGroupBys(QueryGenerator queryGenerator, StringBuilder sb, List<NodeInfo> groupBys) {
        if (groupBys.isEmpty()) {
            return;
        }
        sb.append(" GROUP BY ");
        Iterator<NodeInfo> iter = groupBys.iterator();
        iter.next().getExpression().accept(queryGenerator);
        while (iter.hasNext()) {
            sb.append(", ");
            iter.next().getExpression().accept(queryGenerator);
        }
    }
    
    void acceptVisitor(Visitor v){
        for (NodeInfo groupBy : groupByInfos) {
            groupBy.getExpression().accept(v);
        }
    }

    List<NodeInfo> getGroupByInfos() {
        return groupByInfos;
    }
    
    
}
