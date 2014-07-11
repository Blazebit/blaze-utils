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

import com.blazebit.persistence.impl.objectbuilder.ClassResultTransformer;
import com.blazebit.persistence.impl.objectbuilder.ConstructorResultTransformer;
import com.blazebit.persistence.impl.objectbuilder.TupleResultTransformer;
import com.blazebit.persistence.CaseWhenBuilder;
import com.blazebit.persistence.ObjectBuilder;
import com.blazebit.persistence.QueryBuilder;
import com.blazebit.persistence.SelectObjectBuilder;
import com.blazebit.persistence.SimpleCaseWhenBuilder;
import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.Expression.Visitor;
import com.blazebit.persistence.expression.ExpressionUtils;
import com.blazebit.persistence.expression.PathExpression;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.transform.ResultTransformer;

/**
 *
 * @author ccbem
 */
public class SelectManager<T> extends AbstractManager {

    private final List<SelectInfo> selectInfos = new ArrayList<SelectInfo>();
    private boolean distinct = false;
    private SelectObjectBuilderImpl<?> selectObjectBuilder;
    private ObjectBuilder<T> selectObjectTransformer;
    // Maps alias to SelectInfo
    private final Map<String, SelectInfo> selectAliasToInfoMap = new HashMap<String, SelectInfo>();
    // needed for tuple/alias matching
    private final Map<String, Integer> selectAliasToPositionMap = new HashMap<String, Integer>();
    private final Map<String, SelectInfo> selectAbsolutePathToInfoMap = new HashMap<String, SelectInfo>();
    private final SelectObjectBuilderEndedListenerImpl selectObjectBuilderEndedListener = new SelectObjectBuilderEndedListenerImpl();

    public SelectManager(QueryGenerator queryGenerator) {
        super(queryGenerator);
    }

    void verifyBuilderEnded() {
        selectObjectBuilderEndedListener.verifyBuilderEnded();
    }

    ObjectBuilder<T> getSelectObjectTransformer() {
        return selectObjectTransformer;
    }

    public Map<String, SelectInfo> getSelectAbsolutePathToInfoMap() {
        return selectAbsolutePathToInfoMap;
    }

    public Map<String, SelectInfo> getSelectAliasToInfoMap() {
        return selectAliasToInfoMap;
    }

    public Map<String, Integer> getSelectAliasToPositionMap() {
        return selectAliasToPositionMap;
    }
    
    void acceptVisitor(Visitor v) {
        //TODO: implement test for select new with joins!! - we might also have to do implicit joins for constructor arguments
        // carry out implicit joins
        for (SelectInfo selectInfo : selectInfos) {
            selectInfo.getExpression().accept(v);
        }
    }

    String buildSelect() {
        if (selectInfos.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        if (distinct) {
            sb.append("DISTINCT ");
        }
        // we must not replace select alias since we would loose the original expressions
        populateSelectAliasAbsolutePaths();
        queryGenerator.setQueryBuffer(sb);
        queryGenerator.setReplaceSelectAliases(false);
        Iterator<SelectInfo> iter = selectInfos.iterator();
        applySelect(queryGenerator, sb, iter.next());
        while (iter.hasNext()) {
            sb.append(", ");
            applySelect(queryGenerator, sb, iter.next());
        }
        queryGenerator.setReplaceSelectAliases(true);
        return sb.toString();

    }

    void applyTransformer(ArrayExpressionTransformer transformer) {
        // carry out transformations
        for (SelectInfo selectInfo : selectInfos) {
            Expression transformed = transformer.transform(selectInfo.getExpression(), true);
            selectInfo.setExpression(transformed);
        }
    }

    void select(AbstractCriteriaBuilder<?, ?> builder, String expression, String selectAlias) {
        Expression expr = ExpressionUtils.parse(expression);
        SelectInfo selectInfo = new SelectInfo(expr, selectAlias);
        if (selectAlias != null) {
            selectAliasToInfoMap.put(selectAlias, selectInfo);
            selectAliasToPositionMap.put(selectAlias, selectAliasToPositionMap.size());
        }
        selectInfos.add(selectInfo);
        selectObjectTransformer = (ObjectBuilder<T>) new TupleResultTransformer(this);
    }

//    public U select(Class<? extends T> clazz) {
//        throw new UnsupportedOperationException();
//    }
//
//    public U select(Constructor<? extends T> constructor) {
//        throw new UnsupportedOperationException();
//    }
//
//    // TODO: needed?
//    public U select(ObjectBuilder<? extends T> builder) {
//        throw new UnsupportedOperationException();
//    }
//    public CaseWhenBuilder<U> selectCase() {
//        return new CaseWhenBuilderImpl<U>((U) this);
//    }

    /* CASE caseOperand (WHEN scalarExpression THEN scalarExpression)+ ELSE scalarExpression END */
//    public SimpleCaseWhenBuilder<U> selectCase(String expression) {
//        return new SimpleCaseWhenBuilderImpl<U>((U) this, expression);
//    }
    <Y, T extends AbstractCriteriaBuilder<?, ?>> SelectObjectBuilder<? extends QueryBuilder<Y, ?>> selectNew(T builder, Class<Y> clazz) {
        if (selectObjectBuilder != null) {
            throw new IllegalStateException("Only one selectNew is allowed");
        }
        if (!selectInfos.isEmpty()) {
            throw new IllegalStateException("No mixture of select and selectNew is allowed");
        }
        //TODO: maybe unify with selectNew(ObjectBuilder)
        selectObjectBuilder = selectObjectBuilderEndedListener.startBuilder(new SelectObjectBuilderImpl(builder, selectObjectBuilderEndedListener));
        selectObjectTransformer = new ClassResultTransformer(clazz);
        return (SelectObjectBuilder) selectObjectBuilder;
    }

    <Y, T extends AbstractCriteriaBuilder<?, ?>> SelectObjectBuilder<? extends QueryBuilder<Y, ?>> selectNew(T builder, Constructor<Y> constructor) {
        if (selectObjectBuilder != null) {
            throw new IllegalStateException("Only one selectNew is allowed");
        }
        if (!selectInfos.isEmpty()) {
            throw new IllegalStateException("No mixture of select and selectNew is allowed");
        }
        //TODO: maybe unify with selectNew(ObjectBuilder)
        selectObjectBuilder = selectObjectBuilderEndedListener.startBuilder(new SelectObjectBuilderImpl(builder, selectObjectBuilderEndedListener));
        selectObjectTransformer = new ConstructorResultTransformer(constructor);
        return (SelectObjectBuilder) selectObjectBuilder;
    }

    <Y> QueryBuilder<Y, ?> selectNew(ObjectBuilder<Y> builder) {
        if (selectObjectBuilder != null) {
            throw new IllegalStateException("Only one selectNew is allowed");
        }
        if (!selectInfos.isEmpty()) {
            throw new IllegalStateException("No mixture of select and selectNew is allowed");
        }
        throw new UnsupportedOperationException();
    }

    void distinct() {
        if (selectInfos.isEmpty()) {
            throw new IllegalStateException("Distinct requires select");
        }
        this.distinct = true;
    }

    private void applySelect(QueryGenerator queryGenerator, StringBuilder sb, SelectInfo select) {
        select.getExpression().accept(queryGenerator);
        if (select.alias != null) {
            sb.append(" AS ").append(select.alias);
        }
    }

    private void applySelects(QueryGenerator queryGenerator, StringBuilder sb, List<SelectInfo> selects) {
        if (selects.isEmpty()) {
            return;
        }
        sb.append("SELECT ");
        if (distinct) {
            sb.append("DISTINCT ");
        }
        // we must not replace select alias since we would loose the original expressions
        queryGenerator.setReplaceSelectAliases(false);
        Iterator<SelectInfo> iter = selects.iterator();
        applySelect(queryGenerator, sb, iter.next());
        while (iter.hasNext()) {
            sb.append(", ");
            applySelect(queryGenerator, sb, iter.next());
        }
        sb.append(" ");
        queryGenerator.setReplaceSelectAliases(true);
    }

    protected void populateSelectAliasAbsolutePaths() {
        selectAbsolutePathToInfoMap.clear();
        for (Map.Entry<String, SelectInfo> selectAliasEntry : selectAliasToInfoMap.entrySet()) {
            Expression selectExpr = selectAliasEntry.getValue().getExpression();
            if (selectExpr instanceof PathExpression) {
                PathExpression pathExpr = (PathExpression) selectExpr;
                String absPath = pathExpr.getBaseNode().getAliasInfo().getAbsolutePath();
                selectAbsolutePathToInfoMap.put(absPath, selectAliasEntry.getValue());
            }
        }
    }

    private class SelectObjectBuilderEndedListenerImpl implements SelectObjectBuilderEndedListener {

        private SelectObjectBuilder currentBuilder;

        protected void verifyBuilderEnded() {
            if (currentBuilder != null) {
                throw new IllegalStateException("A builder was not ended properly.");
            }
        }

        protected <T extends SelectObjectBuilder> T startBuilder(T builder) {
            if (currentBuilder != null) {
                throw new IllegalStateException("There was an attempt to start a builder but a previous builder was not ended.");
            }

            currentBuilder = builder;
            return builder;
        }

        @Override
        public void onBuilderEnded(Collection<Expression> expressions) {
            if (currentBuilder == null) {
                throw new IllegalStateException("There was an attempt to end a builder that was not started or already closed.");
            }
            currentBuilder = null;
            for (Expression e : expressions) {
                SelectManager.this.selectInfos.add(new SelectInfo(e));
            }
        }

    }

    static class SelectInfo extends NodeInfo {

        private String alias;

        public SelectInfo(Expression expression) {
            super(expression);
        }

        public SelectInfo(Expression expression, String alias) {
            super(expression);
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }
    }
}
