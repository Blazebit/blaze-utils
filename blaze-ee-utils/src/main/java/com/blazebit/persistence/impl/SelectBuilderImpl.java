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

import com.blazebit.persistence.CaseWhenBuilder;
import com.blazebit.persistence.ObjectBuilder;
import com.blazebit.persistence.QueryBuilder;
import com.blazebit.persistence.SelectBuilder;
import com.blazebit.persistence.SelectObjectBuilder;
import com.blazebit.persistence.SimpleCaseWhenBuilder;
import com.blazebit.persistence.expression.Expression;
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
public class SelectBuilderImpl<T, U extends QueryBuilder<T, U>> implements SelectBuilder<T, U> {

    protected final List<AbstractCriteriaBuilder.SelectInfo> selectInfos = new ArrayList<AbstractCriteriaBuilder.SelectInfo>();
    protected boolean distinct = false;
    protected SelectObjectBuilder<?> selectObjectBuilder;
    protected ResultTransformer selectObjectTransformer;
    // Maps alias to SelectInfo
    protected final Map<String, AbstractCriteriaBuilder.SelectInfo> selectAliasToInfoMap = new HashMap<String, AbstractCriteriaBuilder.SelectInfo>();
    protected final Map<String, AbstractCriteriaBuilder.SelectInfo> selectAbsolutePathToInfoMap = new HashMap<String, AbstractCriteriaBuilder.SelectInfo>();
    protected final SelectObjectBuilderEndedListenerImpl selectObjectBuilderEndedListener = new SelectObjectBuilderEndedListenerImpl();
    protected final AbstractCriteriaBuilder<T,U> builder;

    public SelectBuilderImpl(AbstractCriteriaBuilder<T, U> builder) {
        this.builder = builder;
    }
    
    public void verifyBuilderEnded(){
        selectObjectBuilderEndedListener.verifyBuilderEnded();
    }
    
    public ResultTransformer getSelectObjectTransformer() {
        return selectObjectTransformer;
    }
    public String buildSelect() {
        if (selectInfos.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        if (distinct) {
            sb.append("DISTINCT ");
        }
        // we must not replace select alias since we would loose the original expressions
        populateSelectAliasAbsolutePaths();
        QueryGeneratorVisitor queryGenerator = new QueryGeneratorVisitor(selectAbsolutePathToInfoMap, sb, null);
        queryGenerator.setReplaceSelectAliases(false);
        Iterator<AbstractCriteriaBuilder.SelectInfo> iter = selectInfos.iterator();
        applySelect(queryGenerator, sb, iter.next());
        while (iter.hasNext()) {
            sb.append(", ");
            applySelect(queryGenerator, sb, iter.next());
        }
        sb.append(" ");
        queryGenerator.setReplaceSelectAliases(true);
        return sb.toString();
    }

    @Override
    public U select(String... expressions) {
        for (String expression : expressions) {
            select(expression);
        }
        return (U) this;
    }

    @Override
    public U select(String expression) {
        return select(expression, null);
    }

    @Override
    public U select(String expression, String selectAlias) {
        Expression expr = ArrayExpressionTransformer.transform(ExpressionUtils.parse(expression), builder);
        AbstractCriteriaBuilder.SelectInfo selectInfo = new AbstractCriteriaBuilder.SelectInfo(expr, selectAlias);
        if (selectAlias != null) {
            selectAliasToInfoMap.put(selectAlias, selectInfo);
        }
        selectInfos.add(selectInfo);
        return (U) this;
    }

    @Override
    public U select(Class<? extends T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public U select(Constructor<? extends T> constructor) {
        throw new UnsupportedOperationException();
    }

    // TODO: needed?
    @Override
    public U select(ObjectBuilder<? extends T> builder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CaseWhenBuilder<U> selectCase() {
        return new CaseWhenBuilderImpl<U>((U) this);
    }

    /* CASE caseOperand (WHEN scalarExpression THEN scalarExpression)+ ELSE scalarExpression END */
    @Override
    public SimpleCaseWhenBuilder<U> selectCase(String expression) {
        return new SimpleCaseWhenBuilderImpl<U>((U) this, expression);
    }

    @Override
    public <Y> SelectObjectBuilder<? extends QueryBuilder<Y, ?>> selectNew(Class<Y> clazz) {
        if (selectObjectBuilder != null) {
            throw new IllegalStateException("Only one selectNew is allowed");
        }
        if (!selectInfos.isEmpty()) {
            throw new IllegalStateException("No mixture of select and selectNew is allowed");
        }
        //TODO: maybe unify with selectNew(ObjectBuilder)
        selectObjectBuilder = selectObjectBuilderEndedListener.startBuilder(new SelectObjectBuilderImpl(builder, builder, selectObjectBuilderEndedListener));
        selectObjectTransformer = new ClassResultTransformer(clazz);
        return (SelectObjectBuilder) selectObjectBuilder;
    }

    @Override
    public SelectObjectBuilder<U> selectNew(Constructor<?> constructor) {
        if (selectObjectBuilder != null) {
            throw new IllegalStateException("Only one selectNew is allowed");
        }
        if (!selectInfos.isEmpty()) {
            throw new IllegalStateException("No mixture of select and selectNew is allowed");
        }
        //TODO: maybe unify with selectNew(ObjectBuilder)
        selectObjectBuilder = selectObjectBuilderEndedListener.startBuilder(new SelectObjectBuilderImpl(builder, builder, selectObjectBuilderEndedListener));
        selectObjectTransformer = new ConstructorResultTransformer(constructor);
        return (SelectObjectBuilder) selectObjectBuilder;
    }

    @Override
    public SelectObjectBuilder<U> selectNew(ObjectBuilder<? extends T> builder) {
        if (selectObjectBuilder != null) {
            throw new IllegalStateException("Only one selectNew is allowed");
        }
        if (!selectInfos.isEmpty()) {
            throw new IllegalStateException("No mixture of select and selectNew is allowed");
        }
        throw new UnsupportedOperationException();
    }
    
    @Override
    public U distinct() {
        if (selectInfos.isEmpty()) {
            throw new IllegalStateException("Distinct requires select");
        }
        this.distinct = true;
        return (U) this;
    }

    protected void applySelect(QueryGeneratorVisitor queryGenerator, StringBuilder sb, AbstractCriteriaBuilder.SelectInfo select) {
        select.getExpression().accept(queryGenerator);
        if (select.getAlias() != null) {
            sb.append(" AS ").append(select.getAlias());
        }
    }

    protected void applySelects(QueryGeneratorVisitor queryGenerator, StringBuilder sb, List<AbstractCriteriaBuilder.SelectInfo> selects) {
        if (selects.isEmpty()) {
            return;
        }
        sb.append("SELECT ");
        if (distinct) {
            sb.append("DISTINCT ");
        }
        // we must not replace select alias since we would loose the original expressions
        queryGenerator.setReplaceSelectAliases(false);
        Iterator<AbstractCriteriaBuilder.SelectInfo> iter = selects.iterator();
        applySelect(queryGenerator, sb, iter.next());
        while (iter.hasNext()) {
            sb.append(", ");
            applySelect(queryGenerator, sb, iter.next());
        }
        sb.append(" ");
        queryGenerator.setReplaceSelectAliases(true);
    }
    
    protected void populateSelectAliasAbsolutePaths() {
        for (Map.Entry<String, AbstractCriteriaBuilder.SelectInfo> selectAliasEntry : selectAliasToInfoMap.entrySet()) {
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
                SelectBuilderImpl.this.selectInfos.add(new AbstractCriteriaBuilder.SelectInfo(e));
            }
        }

    }
}
