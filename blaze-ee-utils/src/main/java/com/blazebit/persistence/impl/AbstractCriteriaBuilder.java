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
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.HavingOrBuilder;
import com.blazebit.persistence.JoinType;
import com.blazebit.persistence.ModelUtils;
import com.blazebit.persistence.ObjectBuilder;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import com.blazebit.persistence.QueryBuilder;
import com.blazebit.persistence.RestrictionBuilder;
import com.blazebit.persistence.SelectObjectBuilder;
import com.blazebit.persistence.SimpleCaseWhenBuilder;
import com.blazebit.persistence.WhereOrBuilder;
import com.blazebit.persistence.expression.CompositeExpression;
import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.ExpressionUtils;
import com.blazebit.persistence.expression.PathExpression;
import com.blazebit.persistence.predicate.AndPredicate;
import com.blazebit.persistence.predicate.Predicate;
import com.blazebit.persistence.predicate.PredicateBuilder;
import com.blazebit.reflection.ReflectionUtils;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;

/**
 *
 * @author ccbem
 */
public abstract class AbstractCriteriaBuilder<T, U extends QueryBuilder<T, U>> implements QueryBuilder<T, U> {

    protected static final Logger log = Logger.getLogger(CriteriaBuilderImpl.class.getName());

    protected final Class<T> clazz;
    protected final AliasInfo rootAliasInfo;
    // Maps alias to join path with the root as base
    protected final Map<String, AliasInfo> joinAliasInfos;
    // we might have multiple nodes that depend on the same unresolved alias,
    // hence we need a List of NodeInfos.
    // e.g. SELECT a.X, a.Y FROM A a
    // a is unresolved for both X and Y
    protected final JoinNode rootNode;
    protected final List<OrderByInfo> orderByInfos;
    protected final RootPredicate rootWherePredicate;
    protected final RootPredicate rootHavingPredicate;
    protected final List<NodeInfo> groupByInfos;
    protected final ParameterManager parameterManager;

    protected final SelectBuilderImpl<T, U> selectBuilder;

    /**
     * Create flat copy of builder
     * @param builder 
     */
    protected AbstractCriteriaBuilder(AbstractCriteriaBuilder<T, ? extends QueryBuilder<T, ?>> builder) {
        this.clazz = builder.clazz;
        this.rootAliasInfo = builder.rootAliasInfo;
        this.joinAliasInfos = builder.joinAliasInfos;
        this.rootNode = builder.rootNode;
        this.orderByInfos = builder.orderByInfos;
        this.rootWherePredicate = builder.rootWherePredicate;
        this.rootHavingPredicate = builder.rootHavingPredicate;
        this.groupByInfos = builder.groupByInfos;
        this.parameterManager = builder.parameterManager;
        this.selectBuilder = builder.selectBuilder;
    }

    public AbstractCriteriaBuilder(Class<T> clazz, String alias) {
        this.clazz = clazz;
        this.rootAliasInfo = new AliasInfo(alias, "", true);
        this.joinAliasInfos = new HashMap<String, AliasInfo>();
        this.joinAliasInfos.put(alias, rootAliasInfo);
        this.rootNode = new JoinNode(rootAliasInfo, null, false, null);
        this.rootWherePredicate = new RootPredicate(this);
        this.rootHavingPredicate = new RootPredicate(this);
        this.selectBuilder = new SelectBuilderImpl<T, U>(this);
        this.orderByInfos = new ArrayList<OrderByInfo>();
        this.groupByInfos = new ArrayList<NodeInfo>();
        this.parameterManager = new ParameterManager();
    }

    private static class JoinResult {

        private final JoinNode baseNode;
        private final String field;

        public JoinResult(JoinNode baseNode, String field) {
            this.baseNode = baseNode;
            this.field = field;
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

        public void setAlias(String alias) {
            this.alias = alias;
        }
    }

    private static class RootPredicate extends AbstractBuilderEndedListener {

        private final AndPredicate predicate;
        private final ArrayTransformationVisitor transformer;

        public RootPredicate(AbstractCriteriaBuilder<?, ?> builder) {
            this.predicate = new AndPredicate();
            this.transformer = new ArrayTransformationVisitor(builder);
        }

        @Override
        public void onBuilderEnded(PredicateBuilder builder) {
            super.onBuilderEnded(builder);
            Predicate pred = builder.getPredicate();

            pred.accept(transformer);
//            pred.accept(unresolvedAliasRegistrationVisitor);

            predicate.getChildren()
                    .add(pred);
        }
    }

    protected static void applyJoins(StringBuilder sb, AliasInfo joinBase, Map<String, JoinNode> nodes) {
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
            sb.append(joinBase.getAlias()).append('.').append(relation).append(' ').append(node.getAliasInfo().getAlias());
            if (!node.getNodes().isEmpty()) {
                applyJoins(sb, node.getAliasInfo(), node.getNodes());
            }
        }
    }

    protected static void applyOrderBys(QueryGeneratorVisitor queryGenerator, StringBuilder sb, List<OrderByInfo> orderBys) {
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

    protected static void applyOrderBy(QueryGeneratorVisitor queryGenerator, StringBuilder sb, OrderByInfo orderBy) {
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

    @Override
    public U setParameter(String name, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public U setParameter(String name, Calendar value, TemporalType temporalType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public U setParameter(String name, Date value, TemporalType temporalType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<T> getResultList(EntityManager em) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*
     * Select methods
     */
    @Override
    public U distinct() {
        return selectBuilder.distinct();
    }

    /* CASE (WHEN condition THEN scalarExpression)+ ELSE scalarExpression END */
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
    public PaginatedCriteriaBuilder<T> page(int page, int objectsPerPage) {
        return new PaginatedCriteriaBuilderImpl<T>(this, page, objectsPerPage);

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
        if (expression == null) {
            throw new NullPointerException("expression");
        }
        if (expression.isEmpty() || (selectAlias != null && selectAlias.isEmpty())) {
            throw new IllegalArgumentException("selectAlias");
        }
        verifyBuilderEnded();
        return selectBuilder.select(expression, selectAlias);
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
    public <Y> SelectObjectBuilder<? extends QueryBuilder<Y, ?>> selectNew(Class<Y> clazz) {
        verifyBuilderEnded();
        return selectBuilder.selectNew(clazz);
    }

    @Override
    public SelectObjectBuilder<U> selectNew(Constructor<?> constructor) {
        verifyBuilderEnded();
        return selectBuilder.selectNew(constructor);
    }

    @Override
    public SelectObjectBuilder<U> selectNew(ObjectBuilder<? extends T> builder) {
        verifyBuilderEnded();
        return selectBuilder.selectNew(builder);
    }

    /*
     * Where methods
     */
    @Override
    public RestrictionBuilder<U> where(String expression) {
        if (expression == null) {
            throw new NullPointerException("expression");
        }
        if (expression.isEmpty()) {
            throw new IllegalArgumentException("expression");
        }
        return rootWherePredicate.startBuilder(new RestrictionBuilderImpl<U>((U) this, rootWherePredicate, ArrayExpressionTransformer.transform(ExpressionUtils.parse(expression), this)));
    }

    @Override
    public WhereOrBuilder<U> whereOr() {
        return rootWherePredicate.startBuilder(new WhereOrBuilderImpl<U>(this, (U) this, rootWherePredicate));
    }

    @Override
    public U groupBy(String... paths) {
        for (String path : paths) {
            groupBy(path);
        }
        return (U) this;
    }

    @Override
    public U groupBy(String expression) {
        verifyBuilderEnded();
        Expression exp = ArrayExpressionTransformer.transform(ExpressionUtils.parse(expression), this);
        groupByInfos.add(new NodeInfo(exp));
        return (U) this;
    }

    /*
     * Having methods
     */
    @Override
    public RestrictionBuilder<U> having(String expression) {
        if (groupByInfos.isEmpty()) {
            throw new IllegalStateException();
        }
        return rootHavingPredicate.startBuilder(new RestrictionBuilderImpl<U>((U) this, rootHavingPredicate, ExpressionUtils.parse(expression)));
    }

    @Override
    public HavingOrBuilder<U> havingOr() {
        return rootHavingPredicate.startBuilder(new HavingOrBuilderImpl<U>((U) this, rootHavingPredicate));
    }

    /*
     * Order by methods
     */
    @Override
    public U orderByDesc(String path) {
        return orderBy(path, false, false);
    }

    @Override
    public U orderByAsc(String path) {
        return orderBy(path, true, false);
    }

    @Override
    public U orderByDesc(String path, boolean nullFirst) {
        return orderBy(path, false, nullFirst);
    }

    @Override
    public U orderByAsc(String path, boolean nullFirst) {
        return orderBy(path, true, nullFirst);
    }

    protected void verifyBuilderEnded() {
        rootWherePredicate.verifyBuilderEnded();
        rootHavingPredicate.verifyBuilderEnded();
        selectBuilder.verifyBuilderEnded();
    }

    Expression implicitJoin(Expression expression, boolean objectLeafAllowed) {
        PathExpression pathExpression;
        if (expression instanceof PathExpression) {
            pathExpression = (PathExpression) expression;
            JoinResult result = implicitJoin(pathExpression.getPath(), objectLeafAllowed);
            pathExpression.setBaseNode(result.baseNode);
            pathExpression.setField(result.field);
        } else {
            // We know it can only be a composite expression
            for (Expression exp : ((CompositeExpression) expression).getExpressions()) {
                if (exp instanceof PathExpression) {
                    pathExpression = (PathExpression) exp;
                    JoinResult result = implicitJoin(pathExpression.getPath(), objectLeafAllowed);
                    pathExpression.setBaseNode(result.baseNode);
                    pathExpression.setField(result.field);
                }
            }
        }
        return expression;
    }

    protected String normalizePath(String path) {
        String normalizedPath;
        if (startsAtRootAlias(path)) {
            // The given path is relative to the root
            normalizedPath = path.substring(rootAliasInfo.getAlias().length() + 1);
        } else {
            // The path is either already normalized or uses a specific alias as base
            normalizedPath = path;
        }
        return normalizedPath;
    }

    JoinResult implicitJoin(String path, boolean objectLeafAllowed) {
        String normalizedPath = normalizePath(path);
        JoinNode baseNode;
        String field;
        int dotIndex;
        int fieldStartDotIndex;
        if ((fieldStartDotIndex = normalizedPath.lastIndexOf('.')) != -1) {
            // First we extract the field by which should be ordered
            field = normalizedPath.substring(fieldStartDotIndex + 1);
            String joinPath = normalizedPath.substring(0, fieldStartDotIndex);
            //TEST
            joinPath = normalizedPath;
            AliasInfo potentialBaseInfo;
            if ((dotIndex = joinPath.indexOf('.')) != -1) {
                // We found a dot in the path, so it either uses an alias or does chained joining
                String potentialBase = normalizedPath.substring(0, dotIndex);
                potentialBaseInfo = joinAliasInfos.get(potentialBase);
            } else {
                potentialBaseInfo = joinAliasInfos.get(joinPath);
            }
            if (potentialBaseInfo != null) {
                // We found an alias for the first part of the path
                String potentialBasePath = potentialBaseInfo.getAbsolutePath();
                JoinNode aliasNode = findNode(rootNode, potentialBasePath);
                // TODO: if aliasNode is null, then probably a subpath is not yet joined
                String relativePath = normalizedPath.substring(aliasNode.getAliasInfo().getAlias().length() + 1);
                normalizedPath = potentialBasePath + '.' + relativePath;
                String relativeJoinPath = relativePath.substring(0, relativePath.length() - field.length());
                //TEST
                relativeJoinPath = relativePath;
                if (relativeJoinPath.isEmpty()) {
                    baseNode = aliasNode;
                } else {
                    baseNode = createOrUpdateNode(aliasNode, potentialBasePath, relativeJoinPath, null, null, false, true);
                    if (baseNode.getAliasInfo().getAbsolutePath().endsWith(relativeJoinPath)) {
                        field = null;
                    }
                }
            } else {
                //                String potentialRootProperty = ExpressionUtils.getFirstPathElement(normalizedPath);
                //                if (ReflectionUtils.getField(clazz, potentialRootProperty) == null) {
                //                    throw new IllegalStateException("Unresolved alias: " + normalizedPath);
                //                }
                // check if field is joinable
                // The given path is relative to the root
                baseNode = createOrUpdateNode(rootNode, "", joinPath, null, null, false, true);
                if (baseNode.getAliasInfo().getAbsolutePath().endsWith(joinPath)) {
                    field = null;
                }
            }
        } else {
            // The given path may be relative to the root or it might be an alias
            if (objectLeafAllowed) {
                AliasInfo alias = joinAliasInfos.get(normalizedPath);
                if (alias == rootAliasInfo) {
                    baseNode = rootNode;
                    field = null;
                } else if (alias != null) {
                    baseNode = findNode(rootNode, alias.getAbsolutePath());
                    field = null;
                } else {
                    // check if the path is joinable, assuming it is relative to the root (implicit root prefix)
                    baseNode = createOrUpdateNode(rootNode, "", normalizedPath, null, null, false, true);
                    // check if the last path element was also joined
                    if (baseNode.getAliasInfo().getAbsolutePath().endsWith(normalizedPath)) {
                        field = null;
                    } else {
                        field = normalizedPath;
                    }
                }
            } else {
                Class<?> fieldClass = ModelUtils.resolveFieldClass(clazz, normalizedPath);
                if (ModelUtils.isJoinable(fieldClass)) {
                    throw new IllegalArgumentException("No object leaf allowed but " + normalizedPath + " is an object leaf");
                }
                baseNode = rootNode;
                field = normalizedPath;
            }
        }
        return new JoinResult(baseNode, field);
    }

    @Override
    public U orderBy(String expression, boolean ascending, boolean nullFirst) {
        if (expression == null) {
            throw new NullPointerException("expression");
        }
        if (expression.isEmpty()) {
            throw new IllegalArgumentException("expression");
        }
        verifyBuilderEnded();
        Expression exp = ArrayExpressionTransformer.transform(ExpressionUtils.parse(expression), this);
        orderByInfos.add(new OrderByInfo(exp, ascending, nullFirst));
        return (U) this;
    }

    /*
     * Join methods
     */
    @Override
    public U innerJoin(String path, String alias) {
        return join(path, alias, JoinType.INNER, false);
    }

    @Override
    public U innerJoinFetch(String path, String alias) {
        return join(path, alias, JoinType.INNER, true);
    }

    @Override
    public U leftJoin(String path, String alias) {
        return join(path, alias, JoinType.LEFT, false);
    }

    @Override
    public U leftJoinFetch(String path, String alias) {
        return join(path, alias, JoinType.LEFT, true);
    }

    @Override
    public U rightJoin(String path, String alias) {
        return join(path, alias, JoinType.RIGHT, false);
    }

    @Override
    public U rightJoinFetch(String path, String alias) {
        return join(path, alias, JoinType.RIGHT, true);
    }

    @Override
    public U outerJoin(String path, String alias) {
        return join(path, alias, JoinType.OUTER, false);
    }

    @Override
    public U outerJoinFetch(String path, String alias) {
        return join(path, alias, JoinType.OUTER, true);
    }

    @Override
    public U join(String path, String alias, JoinType type, boolean fetch) {
        if (path == null || alias == null || type == null) {
            throw new NullPointerException();
        }
        if (alias.isEmpty()) {
            throw new IllegalArgumentException();
        }
        verifyBuilderEnded();
        String normalizedPath;
        if (startsAtRootAlias(path)) {
            // The given path is relative to the root
            normalizedPath = path.substring(rootAliasInfo.getAlias().length() + 1);
            createOrUpdateNode(rootNode, "", normalizedPath, alias, type, fetch, false);
        } else {
            // The path is either already normalized or uses a specific alias as base
            normalizedPath = path;
            int dotIndex;
            if ((dotIndex = normalizedPath.indexOf('.')) != -1) {
                // We found a dot in the path, so it either uses an alias or does chained joining
                String potentialBase = normalizedPath.substring(0, dotIndex);
                AliasInfo potentialBaseInfo = joinAliasInfos.get(potentialBase);
                if (potentialBaseInfo != null) {
                    // We found an alias for the first part of the path
                    String potentialBasePath = potentialBaseInfo.getAbsolutePath();
                    JoinNode aliasNode = findNode(rootNode, potentialBasePath);
                    String relativePath = normalizedPath.substring(dotIndex + 1);
                    //                    normalizedPath = potentialBasePath + '.' + relativePath;
                    createOrUpdateNode(aliasNode, potentialBasePath, relativePath, alias, type, fetch, false);
                    //if fetch is true we have to fetch the whole path from aliasNode back to the root
                    if (fetch) {
                        fetchPath(rootNode, potentialBasePath);
                    }
                } else {
                    // The given path is relative to the root
                    createOrUpdateNode(rootNode, "", normalizedPath, alias, type, fetch, false);
                }
            } else {
                // The given path is relative to the root
                createOrUpdateNode(rootNode, "", normalizedPath, alias, type, fetch, false);
            }
        }
        // resolve any previously unresolved aliases
        // TODO: use entity model to check if leafObject is allowed
        // TODO: maybe do this at query generation time??
        //        List<NodeInfo> unresolved = unresolvedAliasMap.remove(alias);
        //        if (unresolved != null) {
        //            for (NodeInfo info : unresolved) {
        //                implicitJoin(info.getExpression(), true);
        //            }
        //        }
        return (U) this;
    }

    boolean startsAtRootAlias(String path) {
        return path.startsWith(rootAliasInfo.getAlias()) && path.length() > rootAliasInfo.getAlias().length() && path.charAt(rootAliasInfo.getAlias().length()) == '.';
    }

    protected JoinNode findNode(JoinNode baseNode, String path) {
        JoinNode currentNode = baseNode;
        String[] pathElements = path.split("\\.");
        for (int i = 0; i < pathElements.length; i++) {
            currentNode = currentNode.getNodes().get(pathElements[i]);
        }
        return currentNode;
    }

    /**
     * Base node will NOT be fetched
     *
     * @param baseNode
     * @param path
     */
    protected void fetchPath(JoinNode baseNode, String path) {
        JoinNode currentNode = baseNode;
        String[] pathElements = path.split("\\.");
        for (int i = 0; i < pathElements.length; i++) {
            currentNode = currentNode.getNodes().get(pathElements[i]);
            currentNode.setFetch(true);
        }
    }

    protected JoinNode createOrUpdateNode(JoinNode baseNode, String basePath, String joinPath, String alias, JoinType type, boolean fetch, boolean implicit) {
        JoinNode currentNode = baseNode;
        StringBuilder currentPath = new StringBuilder(basePath);
        String joinAlias = alias;
        String[] pathElements = joinPath.split("\\.");
        //        for (int i = 0; i < loopBound; i++) {
        //            // TODO: Implement model aware joining or use fetch profiles or so to decide the join types automatically
        //            currentNode = getOrCreate(currentPath, currentNode, pathElements[i], pathElements[i], JoinType.LEFT, false,
        //                    "Ambiguous implicit join", true);
        //        }
        //        getOrCreate(currentPath, currentNode, pathElements[pathElements.length - 1], joinAlias, type, fetch,
        //                "Ambiguous alias", implicit);
        Class<?> currentClass;
        if (baseNode.getPropertyClass() == null) {
            currentClass = clazz;
        } else {
            currentClass = baseNode.getPropertyClass();
        }
        // Iterate through all property names
        for (int j = 0; j < pathElements.length; j++) {
            String propertyName = pathElements[j];
            //            Field propertyField = ReflectionUtils.getField(currentClass, propertyName);
            Class<?> rawFieldClass = ReflectionUtils.getResolvedFieldType(currentClass, propertyName);
            Class<?> resolvedFieldClass = ModelUtils.resolveFieldClass(currentClass, propertyName);
            // Parseable types do not need to be fetched, so also sub
            // properties would not have to be fetched
            // Christian Beikov 14.09.13:
            // Added check for collection and map types since fieldClass evaluates to V if the field is of type Map<K, V>
            if (!ModelUtils.isJoinable(rawFieldClass)) {
                log.info(new StringBuilder("Field with name ").append(propertyName).append(" of class ").append(currentClass.getName()).append(" is parseable and therefore it has not to be fetched explicitly.").toString());
                break;
            }
            currentClass = resolvedFieldClass;
            if (j == pathElements.length - 1) {
                // use parameters for joining the last path property
                if (joinAlias == null) {
                    joinAlias = pathElements[pathElements.length - 1];
                }
                if (type == null) {
                    // TODO: Implement model aware joining
                    type = JoinType.LEFT;
                }
                currentNode = getOrCreate(currentPath, currentNode, propertyName, resolvedFieldClass, joinAlias, type, fetch, "Ambiguous implicit join", implicit);
            } else {
                currentNode = getOrCreate(currentPath, currentNode, propertyName, resolvedFieldClass, propertyName, JoinType.LEFT, fetch, "Ambiguous implicit join", true);
            }
            if (fetch) {
                currentNode.setFetch(true);
            }
        }
        return currentNode;
    }

    protected JoinNode getOrCreate(StringBuilder currentPath, JoinNode currentNode, String joinRelationName, Class<?> joinRelationClass, String alias, JoinType type, boolean fetch, String errorMessage, boolean implicit) {
        JoinNode node = currentNode.getNodes().get(joinRelationName);
        if (currentPath.length() > 0) {
            currentPath.append('.');
        }
        currentPath.append(joinRelationName);
        if (node == null) {
            String currentJoinPath = currentPath.toString();
            AliasInfo oldAliasInfo = joinAliasInfos.get(alias);
            if (oldAliasInfo != null) {
                if (!oldAliasInfo.getAbsolutePath().equals(currentJoinPath)) {
                    throw new IllegalArgumentException(errorMessage);
                } else {
                    throw new RuntimeException("Probably a programming error if this happens. An alias[" + alias + "] for the same join path[" + currentJoinPath + "] is available but the join node is not!");
                }
            } else {
                node = new JoinNode(new AliasInfo(alias, currentJoinPath, implicit), type, fetch, joinRelationClass);
                joinAliasInfos.put(alias, node.getAliasInfo());
            }
            currentNode.getNodes().put(joinRelationName, node);
        } else {
            AliasInfo nodeAliasInfo = node.getAliasInfo();
            if (!alias.equals(nodeAliasInfo.getAlias())) {
                // Aliases for the same join paths don't match
                if (nodeAliasInfo.isImplicit() && !implicit) {
                    // Overwrite implicit aliases
                    String oldAlias = nodeAliasInfo.getAlias();
                    nodeAliasInfo.setAlias(alias);
                    nodeAliasInfo.setImplicit(false);
                    // We can only change the join type if the existing node is implicit and the update on the node is not implicit
                    node.setType(type);
                    joinAliasInfos.remove(oldAlias);
                    joinAliasInfos.put(alias, nodeAliasInfo);
                } else if (!nodeAliasInfo.isImplicit() && !implicit) {
                    throw new IllegalArgumentException("Alias conflict[" + nodeAliasInfo.getAlias() + "=" + nodeAliasInfo.getAbsolutePath() + ", " + alias + "=" + currentPath.toString() + "]");
                }
            }
        }
        return node;
    }

    protected void applyWhere(QueryGeneratorVisitor queryGenerator, StringBuilder sb) {
        if (rootWherePredicate.predicate.getChildren().isEmpty()) {
            return;
        }
        sb.append(" WHERE ");
        rootWherePredicate.predicate.accept(queryGenerator);
    }

    protected void applyHavings(QueryGeneratorVisitor queryGenerator, StringBuilder sb) {
        if (rootHavingPredicate.predicate.getChildren().isEmpty()) {
            return;
        }
        sb.append(" HAVING ");
        rootHavingPredicate.predicate.accept(queryGenerator);
    }

    protected void applyGroupBys(QueryGeneratorVisitor queryGenerator, StringBuilder sb, List<NodeInfo> groupBys) {
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

    protected void applyImplicitJoins() {
        final JoinVisitor joinVisitor = new JoinVisitor(this);
        // carry out implicit joins
        selectBuilder.acceptVisitor(joinVisitor);
        rootWherePredicate.predicate.accept(joinVisitor);
        for (NodeInfo groupBy : groupByInfos) {
            groupBy.getExpression().accept(joinVisitor);
        }
        rootHavingPredicate.predicate.accept(joinVisitor);
        joinVisitor.setJoinWithObjectLeafAllowed(false);
        for (OrderByInfo orderBy : orderByInfos) {
            orderBy.getExpression().accept(joinVisitor);
        }
        joinVisitor.setJoinWithObjectLeafAllowed(true);
    }

    @Override
    public String getQueryString() {
        verifyBuilderEnded();
        StringBuilder sb = new StringBuilder();
        // resolve unresolved aliases, object model etc.
        // we must do implicit joining at the end because we can only do
        // the aliases resolving at the end and alias resolving must happen before
        // the implicit joins
        // it makes no sense to do implicit joining before this point, since
        // the user can call the api in arbitrary orders
        // so where("b.c").join("a.b") but also
        // join("a.b", "b").where("b.c")
        // in the first case
        applyImplicitJoins();
        QueryGeneratorVisitor queryGenerator = new QueryGeneratorVisitor(selectBuilder.getSelectAbsolutePathToInfoMap(), sb, parameterManager);
        sb.append(selectBuilder.buildSelect());
        sb.append("FROM ").append(clazz.getSimpleName()).append(' ').append(rootAliasInfo.getAlias());
        applyJoins(sb, rootAliasInfo, rootNode.getNodes());
        applyWhere(queryGenerator, sb);
        applyGroupBys(queryGenerator, sb, groupByInfos);
        applyHavings(queryGenerator, sb);
        applyOrderBys(queryGenerator, sb, orderByInfos);
        return sb.toString();
    }

    @Override
    public TypedQuery<T> getQuery(EntityManager em) {
        TypedQuery<T> query = (TypedQuery) em.createQuery(getQueryString(), Object[].class);
        if (selectBuilder.getSelectObjectTransformer() != null) {
            // get hibernate query
            Query hQuery = query.unwrap(Query.class);
            hQuery.setResultTransformer(selectBuilder.getSelectObjectTransformer());
        }
        for (Map.Entry<String, Object> entry : parameterManager.getParameters().entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query;
    }

    void addWherePredicate(Predicate predicate) {
        rootWherePredicate.predicate.getChildren().add(predicate);
    }

}
