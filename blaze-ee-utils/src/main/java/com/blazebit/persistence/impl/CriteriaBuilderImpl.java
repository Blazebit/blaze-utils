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

import com.blazebit.persistence.CaseWhenAndBuilder;
import com.blazebit.persistence.CaseWhenBuilder;
import com.blazebit.persistence.CaseWhenOrBuilder;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.HavingOrBuilder;
import com.blazebit.persistence.JoinType;
import com.blazebit.persistence.ObjectBuilder;
import com.blazebit.persistence.ParameterNameGenerator;
import com.blazebit.persistence.RestrictionBuilder;
import com.blazebit.persistence.SelectObjectBuilder;
import com.blazebit.persistence.SimpleCaseWhenBuilder;
import com.blazebit.persistence.WhereOrBuilder;
import com.blazebit.persistence.expression.CompositeExpression;
import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.ExpressionUtils;
import com.blazebit.persistence.expression.PropertyExpression;
import com.blazebit.persistence.predicate.AndPredicate;
import com.blazebit.persistence.predicate.Predicate;
import com.blazebit.persistence.predicate.PredicateBuilder;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * This class uses normalized paths(path expressions without the root alias)
 *
 * @author cpbec
 */
public class CriteriaBuilderImpl<T> extends CriteriaBuilder<T> {

    private final Class<T> clazz;
    private final AliasInfo rootAliasInfo;
    // Maps alias to join path with the root as base
    private final Map<String, AliasInfo> aliasInfos = new HashMap<String, AliasInfo>();
    private final JoinNode rootNode;
    private final List<OrderByInfo> orderByInfos = new ArrayList<OrderByInfo>();
    private final RootPredicate rootWherePredicate;
    private final RootPredicate rootHavingPredicate;
    private final List<SelectInfo> selectInfos = new ArrayList<SelectInfo>();
    private final List<GroupByInfo> groupByInfos = new ArrayList<GroupByInfo>();
    private final Map<String, Object> parameters = new HashMap<String, Object>();
    private final ParameterNameGenerator paramNameGenerator = new ParameterNameGeneratorImpl(parameters);
    private boolean distinct = false;

    public CriteriaBuilderImpl(Class<T> clazz, String alias) {
        this.clazz = clazz;
        this.rootAliasInfo = new AliasInfo(alias, "", true);
        this.aliasInfos.put(alias, rootAliasInfo);
        this.rootNode = new JoinNode(rootAliasInfo, null, false);
        this.rootWherePredicate = new RootPredicate(this);
        this.rootHavingPredicate = new RootPredicate(this);
    }

    /*
     * Select methods
     */
    @Override
    public CriteriaBuilder<T> distinct() {
        this.distinct = true;
        return this;
    }
    
    /* CASE (WHEN condition THEN scalarExpression)+ ELSE scalarExpression END */
    @Override
    public CaseWhenBuilder<CriteriaBuilder<T>> selectCase() {
        return new CaseWhenBuilderImpl<CriteriaBuilder<T>>(this);
    }

    /* CASE caseOperand (WHEN scalarExpression THEN scalarExpression)+ ELSE scalarExpression END */
    @Override
    public SimpleCaseWhenBuilder<CriteriaBuilder<T>> selectCase(String expression) {
        return new SimpleCaseWhenBuilderImpl<CriteriaBuilder<T>>(this, expression);
    }

    @Override
    public CriteriaBuilder<T> page(int page, int objectsPerPage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CriteriaBuilderImpl<T> select(String... expressions) {
        for (String expression : expressions) {
            select(expression);
        }

        return this;
    }

    @Override
    public CriteriaBuilderImpl<T> select(String expression) {
        return select(expression, null);
    }

    @Override
    public CriteriaBuilderImpl<T> select(String expression, String selectAlias) {
        if (selectAlias != null) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        
        verifyBuilderEnded();
        Expression exp = implicitJoin(ArrayExpressionTransformer.transform(ExpressionUtils.parse(expression), this), true);
        selectInfos.add(new SelectInfo(exp));
        return this;
    }

    @Override
    public CriteriaBuilder<T> select(Class<? extends T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CriteriaBuilder<T> select(Constructor<? extends T> constructor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CriteriaBuilder<T> select(ObjectBuilder<? extends T> builder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SelectObjectBuilder<CriteriaBuilder<T>> selectNew(Class<? extends T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SelectObjectBuilder<CriteriaBuilder<T>> selectNew(Constructor<? extends T> constructor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SelectObjectBuilder<CriteriaBuilder<T>> selectNew(ObjectBuilder<? extends T> builder) {
        throw new UnsupportedOperationException();
    }

    /*
     * Where methods
     */
    @Override
    public RestrictionBuilder<CriteriaBuilder<T>> where(String expression) {
        return rootWherePredicate
            .startBuilder(new RestrictionBuilderImpl<CriteriaBuilder<T>>(this, rootWherePredicate,
                                                                         ExpressionUtils.parse(expression)));
    }

    @Override
    public WhereOrBuilder<CriteriaBuilder<T>> whereOr() {
        return rootWherePredicate.startBuilder(new WhereOrBuilderImpl<CriteriaBuilder<T>>(this, rootWherePredicate));
    }

    @Override
    public CriteriaBuilderImpl<T> groupBy(String... paths) {
        for (String path : paths) {
            groupBy(path);
        }

        return this;
    }

    @Override
    public CriteriaBuilderImpl<T> groupBy(String expression) {
        verifyBuilderEnded();
        Expression exp = implicitJoin(ArrayExpressionTransformer.transform(ExpressionUtils.parse(expression), this), true);
        groupByInfos.add(new GroupByInfo(exp));
        return this;
    }

    /*
     * Having methods
     */
    @Override
    public RestrictionBuilder<CriteriaBuilder<T>> having(String expression) {
        return rootHavingPredicate.startBuilder(
                new RestrictionBuilderImpl<CriteriaBuilder<T>>(this, rootHavingPredicate, ExpressionUtils.parse(expression)));
    }

    @Override
    public HavingOrBuilder<CriteriaBuilder<T>> havingOr() {
        return rootHavingPredicate.startBuilder(new HavingOrBuilderImpl<CriteriaBuilder<T>>(this, rootHavingPredicate));
    }

    /*
     * Order by methods
     */
    @Override
    public CriteriaBuilderImpl<T> orderByDesc(String path) {
        return orderBy(path, false, false);
    }

    @Override
    public CriteriaBuilderImpl<T> orderByAsc(String path) {
        return orderBy(path, true, false);
    }

    @Override
    public CriteriaBuilderImpl<T> orderByDesc(String path, boolean nullFirst) {
        return orderBy(path, false, nullFirst);
    }

    @Override
    public CriteriaBuilderImpl<T> orderByAsc(String path, boolean nullFirst) {
        return orderBy(path, true, nullFirst);
    }

    private void verifyBuilderEnded() {
        rootWherePredicate.verifyBuilderEnded();
        rootHavingPredicate.verifyBuilderEnded();
    }

    Expression implicitJoin(Expression expression, boolean objectLeafAllowed) {
        PropertyExpression propertyExpression;
        
        if (expression instanceof PropertyExpression) {
            propertyExpression = (PropertyExpression) expression;
            JoinResult result = implicitJoin(propertyExpression.getProperty(), objectLeafAllowed);
            propertyExpression.setBaseNode(result.baseNode);
            propertyExpression.setField(result.field);
        } else {
            // We know it can only be a composite expression
            for (Expression exp : ((CompositeExpression) expression).getExpressions()) {
                if (exp instanceof PropertyExpression) {
                    propertyExpression = (PropertyExpression) exp;
                    JoinResult result = implicitJoin(propertyExpression.getProperty(), objectLeafAllowed);
                    propertyExpression.setBaseNode(result.baseNode);
                    propertyExpression.setField(result.field);
                }
            }
        }

        return expression;
    }
    
    private static class JoinResult {
        private final JoinNode baseNode;
        private final String field;

        public JoinResult(JoinNode baseNode, String field) {
            this.baseNode = baseNode;
            this.field = field;
        }
    }

    private JoinResult implicitJoin(String path, boolean objectLeafAllowed) {
        String normalizedPath;
        JoinNode baseNode;
        String field;

        if (startsAtRootAlias(path)) {
            // The given path is relative to the root
            normalizedPath = path.substring(rootAliasInfo.getAlias()
                .length() + 1);
        } else {
            // The path is either already normalized or uses a specific alias as base
            normalizedPath = path;
        }

        int dotIndex;
        int fieldStartDotIndex;
        if ((fieldStartDotIndex = normalizedPath.lastIndexOf('.')) != -1) {
            // First we extract the field by which should be ordered
            field = normalizedPath.substring(fieldStartDotIndex + 1);
            String joinPath = normalizedPath.substring(0, fieldStartDotIndex);
            AliasInfo potentialBaseInfo;

            if ((dotIndex = joinPath.indexOf('.')) != -1) {
                // We found a dot in the path, so it either uses an alias or does chained joining
                String potentialBase = normalizedPath.substring(0, dotIndex);
                potentialBaseInfo = aliasInfos.get(potentialBase);
            } else {
                potentialBaseInfo = aliasInfos.get(joinPath);
            }

            if (potentialBaseInfo != null) {
                // We found an alias for the first part of the path
                String potentialBasePath = potentialBaseInfo.getAbsolutePath();
                JoinNode aliasNode = findNode(rootNode, potentialBasePath);
                // TODO: if aliasNode is null, then probably a subpath is not yet joined
                String relativePath = normalizedPath.substring(aliasNode.getAliasInfo()
                    .getAlias()
                    .length() + 1);
                normalizedPath = potentialBasePath + '.' + relativePath;
                String relativeJoinPath = relativePath.substring(0, relativePath.length() - field.length());

                if (relativeJoinPath.isEmpty()) {
                    baseNode = aliasNode;
                } else {
                    baseNode = createOrUpdateNode(aliasNode, potentialBasePath, relativeJoinPath, null, null, false, true);
                }
            } else {
                // The given path is relative to the root
                baseNode = createOrUpdateNode(rootNode, "", joinPath, null, null, false, true);
            }
        } else {
            if (objectLeafAllowed) {
                // The given path is relative to the root, an alias or the root itself
                AliasInfo alias = aliasInfos.get(normalizedPath);

                if (alias != null) {
                    baseNode = findNode(rootNode, alias.getAbsolutePath());
                    field = null;
                } else {
                    baseNode = rootNode;
                    field = normalizedPath;
                }
            } else {
                baseNode = rootNode;
                field = normalizedPath;
            }
        }

        return new JoinResult(baseNode, field);
    }

    @Override
    public CriteriaBuilderImpl<T> orderBy(String expression, boolean ascending, boolean nullFirst) {
        verifyBuilderEnded();
        Expression exp = implicitJoin(ArrayExpressionTransformer.transform(ExpressionUtils.parse(expression), this), false);
        orderByInfos.add(new OrderByInfo(exp, ascending, nullFirst));
        return this;
    }

    /*
     * Join methods
     */
    @Override
    public CriteriaBuilderImpl<T> innerJoin(String path, String alias) {
        return join(path, alias, JoinType.INNER, false);
    }

    @Override
    public CriteriaBuilderImpl<T> innerJoinFetch(String path, String alias) {
        return join(path, alias, JoinType.INNER, true);
    }

    @Override
    public CriteriaBuilderImpl<T> leftJoin(String path, String alias) {
        return join(path, alias, JoinType.LEFT, false);
    }

    @Override
    public CriteriaBuilderImpl<T> leftJoinFetch(String path, String alias) {
        return join(path, alias, JoinType.LEFT, true);
    }

    @Override
    public CriteriaBuilderImpl<T> rightJoin(String path, String alias) {
        return join(path, alias, JoinType.RIGHT, false);
    }

    @Override
    public CriteriaBuilderImpl<T> rightJoinFetch(String path, String alias) {
        return join(path, alias, JoinType.RIGHT, true);
    }

    @Override
    public CriteriaBuilderImpl<T> outerJoin(String path, String alias) {
        return join(path, alias, JoinType.OUTER, false);
    }

    @Override
    public CriteriaBuilderImpl<T> outerJoinFetch(String path, String alias) {
        return join(path, alias, JoinType.OUTER, true);
    }

    @Override
    public CriteriaBuilderImpl<T> join(String path, String alias, JoinType type, boolean fetch) {
        if(path == null || alias == null ||type == null){
            throw new NullPointerException();
        }
        if(alias.isEmpty()) throw new IllegalArgumentException();
        
        verifyBuilderEnded();

        String normalizedPath;

        if (startsAtRootAlias(path)) {
            // The given path is relative to the root
            normalizedPath = path.substring(rootAliasInfo.getAlias()
                .length() + 1);
            createOrUpdateNode(rootNode, "", normalizedPath, alias, type, fetch, false);
        } else {
            // The path is either already normalized or uses a specific alias as base
            normalizedPath = path;

            int dotIndex;
            if ((dotIndex = normalizedPath.indexOf('.')) != -1) {
                // We found a dot in the path, so it either uses an alias or does chained joining
                String potentialBase = normalizedPath.substring(0, dotIndex);
                AliasInfo potentialBaseInfo = aliasInfos.get(potentialBase);

                if (potentialBaseInfo != null) {
                    // We found an alias for the first part of the path
                    String potentialBasePath = potentialBaseInfo.getAbsolutePath();
                    JoinNode aliasNode = findNode(rootNode, potentialBasePath);
                // TODO: if aliasNode is null, then probably a subpath is not yet joined
                    String relativePath = normalizedPath.substring(dotIndex + 1);
//                    normalizedPath = potentialBasePath + '.' + relativePath;
                    createOrUpdateNode(aliasNode, potentialBasePath, relativePath, alias, type, fetch, false);
                } else {
                    // The given path is relative to the root
                    createOrUpdateNode(rootNode, "", normalizedPath, alias, type, fetch, false);
                }
            } else {
                // The given path is relative to the root
                createOrUpdateNode(rootNode, "", normalizedPath, alias, type, fetch, false);
            }
        }

        return this;
    }

    private boolean startsAtRootAlias(String path) {
        return path.startsWith(rootAliasInfo.getAlias()) && path.length() > rootAliasInfo.getAlias()
            .length() && path
            .charAt(rootAliasInfo.getAlias()
            .length()) == '.';
    }

    private JoinNode findNode(JoinNode baseNode, String path) {
        JoinNode currentNode = baseNode;
        String[] pathElements = path.split("\\.");

        for (int i = 0; i < pathElements.length; i++) {
            currentNode = currentNode.getNodes()
                .get(pathElements[i]);
        }

        return currentNode;
    }

    private JoinNode createOrUpdateNode(JoinNode baseNode, String basePath, String joinPath, String alias, JoinType type, boolean fetch, boolean implicit) {
        JoinNode currentNode = baseNode;
        StringBuilder currentPath = new StringBuilder(basePath);
        String joinAlias = alias;
        String[] pathElements = joinPath.split("\\.");

        for (int i = 0; i < pathElements.length - 1; i++) {
            // TODO: Implement model aware joining or use fetch profiles or so to decide the join types automatically
            currentNode = getOrCreate(currentPath, currentNode, pathElements[i], pathElements[i], JoinType.LEFT, false,
                    "Ambiguous implicit join", true);
        }

        if (joinAlias == null) {
            joinAlias = pathElements[pathElements.length - 1];
        }

        if (type == null) {
            // TODO: Implement model aware joining
            type = JoinType.LEFT;
        }

        currentNode = getOrCreate(currentPath, currentNode, pathElements[pathElements.length - 1], joinAlias, type, fetch,
                "Ambiguous alias", implicit);

        // We can only change the join type if the existing node is implicit and the update on the node is not implicit
        if (currentNode.getAliasInfo()
            .isImplicit() && !implicit) {
            currentNode.setType(type);
        }
        if (fetch) {
            currentNode.setFetch(true);
        }
        return currentNode;
    }

    private JoinNode getOrCreate(StringBuilder currentPath, JoinNode currentNode, String joinRelation, String alias, JoinType type, boolean fetch, String errorMessage, boolean implicit) {
        JoinNode node = currentNode.getNodes()
            .get(joinRelation);

        if (currentPath.length() > 0) {
            currentPath.append('.');
        }

        currentPath.append(joinRelation);

        if (node == null) {
            String currentJoinPath = currentPath.toString();
            AliasInfo oldAliasInfo = aliasInfos.get(alias);

            if (oldAliasInfo != null) {
                if (!oldAliasInfo.getAbsolutePath()
                    .equals(currentJoinPath)) {
                    throw new IllegalArgumentException(errorMessage);
                } else {
                    throw new RuntimeException("Probably a programming error if this happens. An alias[" + alias
                            + "] for the same join path[" + currentJoinPath + "] is available but the join node is not!");
                }
            } else {
                node = new JoinNode(new AliasInfo(alias, currentJoinPath, implicit), type, fetch);
                aliasInfos.put(alias, node.getAliasInfo());
            }

            currentNode.getNodes()
                .put(joinRelation, node);
        } else {
            AliasInfo nodeAliasInfo = node.getAliasInfo();

            if (!alias.equals(nodeAliasInfo.getAlias())) {
                // Aliases for the same join paths don't match
                if (nodeAliasInfo.isImplicit() && !implicit) {
                    // Overwrite implicit aliases
                    String oldAlias = nodeAliasInfo.getAlias();
                    nodeAliasInfo.setAlias(alias);
                    nodeAliasInfo.setImplicit(false);
                    aliasInfos.remove(oldAlias);
                    aliasInfos.put(alias, nodeAliasInfo);
                } else if (!nodeAliasInfo.isImplicit() && !implicit) {
                    throw new IllegalArgumentException("Alias conflict[" + nodeAliasInfo.getAlias() + "="
                        + nodeAliasInfo.getAbsolutePath() + ", " + alias + "=" + currentPath.toString() + "]");
                }
            }
        }

        return node;
    }

    /*
     * Apply methods
     */
    private void applySelects(QueryGeneratorVisitor queryGenerator, StringBuilder sb, List<SelectInfo> selects) {
        if (selects.isEmpty()) {
            return;
        }

        sb.append("SELECT ");
        
        if (distinct) {
            sb.append("DISTINCT ");
        }
        
        Iterator<SelectInfo> iter = selects.iterator();
        iter.next().expression.accept(queryGenerator);

        while (iter.hasNext()) {
            sb.append(", ");
            iter.next().expression.accept(queryGenerator);
        }

        sb.append(" ");
    }

    private static void applyJoins(StringBuilder sb, AliasInfo joinBase, Map<String, JoinNode> nodes) {
        for (Map.Entry<String, JoinNode> nodeEntry : nodes.entrySet()) {
            String relation = nodeEntry.getKey();
            JoinNode node = nodeEntry.getValue();

            sb.append(' ');

            switch (node.getType()) {
                case INNER:
                    sb.append("JOIN ");
                    break;
                case LEFT:
                    sb.append("LEFT JOIN ");
                    break;
                case RIGHT:
                    sb.append("RIGHT JOIN ");
                    break;
                case OUTER:
                    sb.append("OUTER JOIN ");
                    break;
            }

            if (node.isFetch()) {
                sb.append("FETCH ");
            }

            sb.append(joinBase.getAlias())
                .append('.')
                .append(relation)
                .append(' ')
                .append(node.getAliasInfo()
                .getAlias());

            if (!node.getNodes()
                .isEmpty()) {
                applyJoins(sb, node.getAliasInfo(), node.getNodes());
            }
        }
    }

    private void applyWhere(QueryGeneratorVisitor queryGenerator, StringBuilder sb) {
        if(rootWherePredicate.predicate.getChildren().isEmpty())
            return;
        sb.append(" WHERE ");
        rootWherePredicate.predicate.accept(queryGenerator);
    }

    private void applyGroupBys(QueryGeneratorVisitor queryGenerator, StringBuilder sb, List<GroupByInfo> groupBys) {
        if (groupBys.isEmpty()) {
            return;
        }

        sb.append(" GROUP BY ");
        Iterator<GroupByInfo> iter = groupBys.iterator();
        iter.next().expression.accept(queryGenerator);

        while (iter.hasNext()) {
            sb.append(", ");
            iter.next().expression.accept(queryGenerator);
        }
    }

    private static void applyOrderBys(QueryGeneratorVisitor queryGenerator, StringBuilder sb, List<OrderByInfo> orderBys) {
        if (orderBys.isEmpty()) {
            return;
        }

        sb.append(" ORDER BY ");
        Iterator<OrderByInfo> iter = orderBys.iterator();
        applyOrderBy(queryGenerator, sb, iter.next());

        while (iter.hasNext()) {
            sb.append(", ");
            applyOrderBy(queryGenerator, sb, iter.next());
        }
    }

    private static void applyOrderBy(QueryGeneratorVisitor queryGenerator, StringBuilder sb, OrderByInfo orderBy) {
        orderBy.expression.accept(queryGenerator);

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

    @Override
    public String getQueryString() {
        verifyBuilderEnded();
        StringBuilder sb = new StringBuilder();

        QueryGeneratorVisitor queryGenerator = new QueryGeneratorVisitor(sb, paramNameGenerator);
        applySelects(queryGenerator, sb, selectInfos);
        sb.append("FROM ")
            .append(clazz.getSimpleName())
            .append(' ')
            .append(rootAliasInfo.getAlias());
        applyJoins(sb, rootAliasInfo, rootNode.getNodes());
        applyWhere(queryGenerator, sb);
        applyGroupBys(queryGenerator, sb, groupByInfos);
//        applyHavings();
        applyOrderBys(queryGenerator, sb, orderByInfos);

        return sb.toString();
    }

    @Override
    public TypedQuery<T> getQuery(EntityManager em) {
        TypedQuery<T> query = em.createQuery(getQueryString(), clazz);

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query;
    }
    
    void addWherePredicate(Predicate predicate) {
        rootWherePredicate.predicate.getChildren().add(predicate);
    }

    private static class OrderByInfo {

        private Expression expression;
        private boolean ascending;
        private boolean nullFirst;

        public OrderByInfo(Expression expression, boolean ascending, boolean nullFirst) {
            this.expression = expression;
            this.ascending = ascending;
            this.nullFirst = nullFirst;
        }
    }

    private static class GroupByInfo {

        private Expression expression;

        public GroupByInfo(Expression expression) {
            this.expression = expression;
        }
    }

    private static class SelectInfo {

        private Expression expression;

        public SelectInfo(Expression expression) {
            this.expression = expression;
        }
    }

    private static class RootPredicate extends AbstractBuilderEndedListener {

        private final AndPredicate predicate;
        private final ArrayTransformationVisitor transformer;

        public RootPredicate(CriteriaBuilderImpl<?> builder) {
            this.predicate = new AndPredicate();
            this.transformer = new ArrayTransformationVisitor(builder);
        }

        @Override
        public void onBuilderEnded(PredicateBuilder builder) {
            super.onBuilderEnded(builder);
            Predicate pred = builder.getPredicate();
            
            pred.accept(transformer);
            
            predicate.getChildren()
                    .add(pred);
        }
    }
}
