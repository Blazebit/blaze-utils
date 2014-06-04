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
import com.blazebit.persistence.ObjectBuilder;
import com.blazebit.persistence.ParameterNameGenerator;
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
import edu.emory.mathcs.backport.java.util.Arrays;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.TypedQuery;
import org.hibernate.Query;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.transform.ResultTransformer;

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
    private final Map<String, AliasInfo> joinAliasInfos = new HashMap<String, AliasInfo>();

    // Maps alias to SelectInfo
    final Map<String, SelectInfo> selectAliasToInfoMap = new HashMap<String, SelectInfo>();
    
    final Map<String, SelectInfo> selectAbsolutePathToInfoMap = new HashMap<String, SelectInfo>();

    // we might have multiple nodes that depend on the same unresolved alias,
    // hence we need a List of NodeInfos.
    // e.g. SELECT a.X, a.Y FROM A a
    // a is unresolved for both X and Y
    private final Map<String, List<NodeInfo>> unresolvedAliasMap = new HashMap<String, List<NodeInfo>>();

    private final JoinNode rootNode;
    private final List<OrderByInfo> orderByInfos = new ArrayList<OrderByInfo>();
    private final RootPredicate rootWherePredicate;
    private final RootPredicate rootHavingPredicate;
    private final List<SelectInfo> selectInfos = new ArrayList<SelectInfo>();
    private final List<NodeInfo> groupByInfos = new ArrayList<NodeInfo>();
    private final Map<String, Object> parameters = new HashMap<String, Object>();
    private final ParameterNameGenerator paramNameGenerator = new ParameterNameGeneratorImpl(parameters);
    private boolean distinct = false;

    private SelectObjectBuilder<T> selectObjectBuilder;
    private ResultTransformer selectObjectTransformer;
    private final SelectObjectBuilderEndedListenerImpl selectObjectBuilderEndedListener;

    // Visitors
    private final UnresolvedAliasRegistrationVisitor unresolvedAliasRegistrationVisitor = new UnresolvedAliasRegistrationVisitor(this);

    public CriteriaBuilderImpl(Class<T> clazz, String alias) {
        this.clazz = clazz;
        this.rootAliasInfo = new AliasInfo(alias, "", true);
        this.joinAliasInfos.put(alias, rootAliasInfo);
        this.rootNode = new JoinNode(rootAliasInfo, null, false);
        this.rootWherePredicate = new RootPredicate(this);
        this.rootHavingPredicate = new RootPredicate(this);
        this.selectObjectBuilderEndedListener = new SelectObjectBuilderEndedListenerImpl();
    }

    /*
     * Select methods
     */
    @Override
    public CriteriaBuilder<T> distinct() {
        if (selectInfos.isEmpty()) {
            throw new IllegalStateException("Distinct requires select");
        }
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
        if (expression == null) {
            throw new NullPointerException("expression");
        }
        if (expression.isEmpty() || (selectAlias != null && selectAlias.isEmpty())) {
            throw new IllegalArgumentException("selectAlias");
        }

        verifyBuilderEnded();

        Expression expr = ArrayExpressionTransformer.transform(ExpressionUtils.parse(expression), this);
        SelectInfo selectInfo = new SelectInfo(expr, selectAlias);

        if (selectAlias != null) {
            selectAliasToInfoMap.put(selectAlias, selectInfo);
        } 

        selectInfos.add(selectInfo);

        expr.accept(unresolvedAliasRegistrationVisitor);

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

    // TODO: needed?
    @Override
    public CriteriaBuilder<T> select(ObjectBuilder<? extends T> builder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X> SelectObjectBuilder<X> selectNew(Class<X> clazz) {
        checkSelectNewAllowed();

        //TODO: maybe unify with selectNew(ObjectBuilder)
        selectObjectBuilder = selectObjectBuilderEndedListener.startBuilder(new SelectObjectBuilderImpl<T>(this, selectObjectBuilderEndedListener));
        selectObjectTransformer = new ClassResultTransformer(clazz);
        return (SelectObjectBuilder) selectObjectBuilder;
    }

    @Override
    public SelectObjectBuilder<T> selectNew(Constructor<?> constructor) {
        checkSelectNewAllowed();

        //TODO: maybe unify with selectNew(ObjectBuilder)
        selectObjectBuilder = selectObjectBuilderEndedListener.startBuilder(new SelectObjectBuilderImpl<T>(this, selectObjectBuilderEndedListener));
        selectObjectTransformer = new ConstructorResultTransformer(constructor);
        return (SelectObjectBuilder) selectObjectBuilder;
    }

    @Override
    public SelectObjectBuilder<T> selectNew(ObjectBuilder<? extends T> builder) {
        throw new UnsupportedOperationException();
    }

    private void checkSelectNewAllowed() {
        verifyBuilderEnded();
        if (selectObjectBuilder != null) {
            throw new IllegalStateException("Only one selectNew is allowed");
        }
        if (!selectInfos.isEmpty()) {
            throw new IllegalStateException("No mixture of select and selectNew is allowed");
        }
    }

    void addUnresolvedAlias(String alias, NodeInfo nodeInfo) {
        List<NodeInfo> l = unresolvedAliasMap.get(alias);
        if (l == null) {
            l = new ArrayList<NodeInfo>();
            l.add(nodeInfo);
            unresolvedAliasMap.put(alias, l);
        } else {
            l.add(nodeInfo);
        }

    }
    /*
     * Where methods
     */

    @Override
    public RestrictionBuilder<CriteriaBuilder<T>> where(String expression) {
        if(expression == null){
            throw new NullPointerException("expression");
        }
        if(expression.isEmpty()){
            throw new IllegalArgumentException("expression");
        }
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
        groupByInfos.add(new NodeInfo(exp));
        return this;
    }

    /*
     * Having methods
     */
    @Override
    public RestrictionBuilder<CriteriaBuilder<T>> having(String expression) {
        if (groupByInfos.isEmpty()) {
            throw new IllegalStateException();
        }

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
        selectObjectBuilderEndedListener.verifyBuilderEnded();
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

    private static class JoinResult {

        private final JoinNode baseNode;
        private final String field;

        public JoinResult(JoinNode baseNode, String field) {
            this.baseNode = baseNode;
            this.field = field;
        }
    }

    private String normalizePath(String path) {
        String normalizedPath;

        if (startsAtRootAlias(path)) {
            // The given path is relative to the root
            normalizedPath = path.substring(rootAliasInfo.getAlias()
                    .length() + 1);
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
                String potentialRootProperty = ExpressionUtils.getFirstPathElement(normalizedPath);
                
                if(ReflectionUtils.getField(clazz, potentialRootProperty) == null) {
                    throw new IllegalStateException("Unresolved alias: " + normalizedPath);
                }
                // check if field is joinable
                // The given path is relative to the root
                baseNode = createOrUpdateNode(rootNode, "", joinPath, null, null, false, true);
                
            }
        } else {
            if (objectLeafAllowed) {
                // The given path may be relative to the root, an alias or the root itself
                AliasInfo alias = joinAliasInfos.get(normalizedPath);

                if (alias == rootAliasInfo) {
                    baseNode = rootNode;
                    field = null;
                } else if (alias != null) {
                    baseNode = findNode(rootNode, alias.getAbsolutePath());
                    field = null;
                } else {
                    // check if it is relative to the root
                    Field f;
                    if ((f = ReflectionUtils.getField(clazz, normalizedPath)) != null) {
                        // it is relative to the root

                        // check if field is joinable
                        if (isJoinable(clazz, f)) {
                            baseNode = createOrUpdateNode(rootNode, "", normalizedPath, null, null, false, true);
                            field = null;
                        } else {
                            baseNode = rootNode;
                            field = normalizedPath;
                        }
                    } else {
                        throw new IllegalStateException("Unresolved alias: " + normalizedPath);
                    }

                }
            } else {
                if (ReflectionUtils.getField(clazz, normalizedPath) != null) {
                    // check if field is joinable
                } else {
                    throw new IllegalStateException("Unresolved alias: " + normalizedPath);
                }
                baseNode = rootNode;
                field = normalizedPath;
            }
        }

        return new JoinResult(baseNode, field);
    }

    private boolean isJoinable(Class<?> clazz, Field f) {
        Class[] joinableAnnotations = new Class[]{OneToMany.class, ManyToMany.class, OneToOne.class, ManyToOne.class, ElementCollection.class};
        Set<Class<?>> fieldAndGetterAnnotations = new HashSet<Class<?>>();
        Method getter = ReflectionUtils.getGetter(clazz, f.getName());

        Annotation[] fieldAnnotations = f.getAnnotations();
        Annotation[] getterAnnoations = getter.getDeclaredAnnotations();
        for (Annotation a : fieldAnnotations) {
            fieldAndGetterAnnotations.add(a.getClass());
        }
        for (Annotation a : getterAnnoations) {
            fieldAndGetterAnnotations.addAll(Arrays.asList(a.getClass().getInterfaces()));
        }

        for (Class<?> joinableAnnotation : joinableAnnotations) {
            if (fieldAndGetterAnnotations.contains(joinableAnnotation)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CriteriaBuilderImpl<T> orderBy(String expression, boolean ascending, boolean nullFirst) {
        if (expression == null) {
            throw new NullPointerException("expression");
        }
        if (expression.isEmpty()) {
            throw new IllegalArgumentException("expression");
        }
        
        verifyBuilderEnded();
        Expression exp = implicitJoin(ArrayExpressionTransformer.transform(ExpressionUtils.parse(expression), this), false);
        exp.accept(unresolvedAliasRegistrationVisitor);
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
                AliasInfo potentialBaseInfo = joinAliasInfos.get(potentialBase);

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

        // resolve any previously unresolved aliases
        // TODO: use entity model to check if leafObject is allowed
        // TODO: maybe do this at query generation time??
//        List<NodeInfo> unresolved = unresolvedAliasMap.remove(alias);
//        if (unresolved != null) {
//            for (NodeInfo info : unresolved) {
//                implicitJoin(info.getExpression(), true);
//            }
//        }
        return this;
    }

    boolean startsAtRootAlias(String path) {
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
            AliasInfo oldAliasInfo = joinAliasInfos.get(alias);

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
                joinAliasInfos.put(alias, node.getAliasInfo());
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
                    joinAliasInfos.remove(oldAlias);
                    joinAliasInfos.put(alias, nodeAliasInfo);
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

    private void applySelect(QueryGeneratorVisitor queryGenerator, StringBuilder sb, SelectInfo select) {
        select.getExpression().accept(queryGenerator);
        if (select.alias != null) {
            sb.append(" AS ").append(select.alias);
        }
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
        if (rootWherePredicate.predicate.getChildren().isEmpty()) {
            return;
        }
        sb.append(" WHERE ");
        rootWherePredicate.predicate.accept(queryGenerator);
    }

    private void applyHavings(QueryGeneratorVisitor queryGenerator, StringBuilder sb) {
        if (rootHavingPredicate.predicate.getChildren().isEmpty()) {
            return;
        }
        sb.append(" HAVING ");
        rootHavingPredicate.predicate.accept(queryGenerator);
    }

    private void applyGroupBys(QueryGeneratorVisitor queryGenerator, StringBuilder sb, List<NodeInfo> groupBys) {
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

    private void applyImplicitJoins() {
        final JoinVisitor joinVisitor = new JoinVisitor(this);
        // carry out implicit joins
        for (SelectInfo selectInfo : selectInfos) {
            selectInfo.getExpression().accept(joinVisitor);
        }

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

    private void resolveAliases() {
        for (Map.Entry<String, List<NodeInfo>> unresolvedAliasEntry : unresolvedAliasMap.entrySet()) {
            String unresolvedAlias = unresolvedAliasEntry.getKey();
            if (joinAliasInfos.containsKey(unresolvedAlias)) {
                // alias is now resolvable
                for (NodeInfo info : unresolvedAliasEntry.getValue()) {
                    implicitJoin(info.getExpression(), true);
                }
            }
        }
    }

    private void populateSelectAliasAbsolutePaths(){
        for(Map.Entry<String, SelectInfo> selectAliasEntry : selectAliasToInfoMap.entrySet()){
            Expression selectExpr = selectAliasEntry.getValue().getExpression();
            if(selectExpr instanceof PathExpression){
                PathExpression pathExpr = (PathExpression) selectExpr;
                String absPath = pathExpr.getBaseNode().getAliasInfo().getAbsolutePath();
                selectAbsolutePathToInfoMap.put(absPath, selectAliasEntry.getValue());
            }
        }
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

        populateSelectAliasAbsolutePaths();
                
        QueryGeneratorVisitor queryGenerator = new QueryGeneratorVisitor(this, sb, paramNameGenerator);
        applySelects(queryGenerator, sb, selectInfos);
        sb.append("FROM ")
                .append(clazz.getSimpleName())
                .append(' ')
                .append(rootAliasInfo.getAlias());
        applyJoins(sb, rootAliasInfo, rootNode.getNodes());
        applyWhere(queryGenerator, sb);
        applyGroupBys(queryGenerator, sb, groupByInfos);
        applyHavings(queryGenerator, sb);
        applyOrderBys(queryGenerator, sb, orderByInfos);

        return sb.toString();
    }

    @Override
    public TypedQuery<T> getQuery(EntityManager em) {
        TypedQuery<T> query = (TypedQuery) em.createQuery(getQueryString(), Object[].class
        );

        if (selectObjectBuilder
                != null) {
            // get hibernate query
            Query hQuery = query.unwrap(Query.class);
            hQuery.setResultTransformer(selectObjectTransformer);
        }

        for (Map.Entry<String, Object> entry
                : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query;
    }

    void addWherePredicate(Predicate predicate) {
        rootWherePredicate.predicate.getChildren().add(predicate);

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

    private class RootPredicate extends AbstractBuilderEndedListener {

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
            pred.accept(unresolvedAliasRegistrationVisitor);

            predicate.getChildren()
                    .add(pred);
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
                e.accept(unresolvedAliasRegistrationVisitor);
                CriteriaBuilderImpl.this.selectInfos.add(new SelectInfo(e));
            }
        }

    }
}
