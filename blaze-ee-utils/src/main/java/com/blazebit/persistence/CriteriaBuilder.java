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
package com.blazebit.persistence;

import com.blazebit.lang.StringUtils;
import com.blazebit.persistence.expression.ExpressionUtils;
import com.blazebit.persistence.expression.PropertyExpression;
import com.blazebit.persistence.predicate.AndPredicate;
import com.blazebit.persistence.predicate.PredicateBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author cpbec
 */
public class CriteriaBuilder<T> implements Filterable<RestrictionBuilder<? extends CriteriaBuilder<T>>> {
    
    private final Class<T> clazz;
    private final AliasInfo rootAliasInfo;
    // Maps alias to join path with the root as base
    private final Map<String, AliasInfo> aliasInfos = new HashMap<String, AliasInfo>();
    private final JoinNode rootNode;
    private final Map<String, OrderByInfo> orderByInfos = new HashMap<String, OrderByInfo>();
    private final RootPredicate rootWherePredicate;
    private final RootPredicate rootHavingPredicate;
    private final Map<String, Object> parameters = new HashMap<String, Object>();

    public CriteriaBuilder(Class<T> clazz, String alias) {
        this.clazz = clazz;
        this.rootAliasInfo = new AliasInfo(alias, "", true);
        this.aliasInfos.put(alias, rootAliasInfo);
        this.rootNode = new JoinNode(rootAliasInfo, null, false);
        this.rootWherePredicate = new RootPredicate();
        this.rootHavingPredicate = new RootPredicate();
    }
    
    public static <T> CriteriaBuilder<T> from(Class<T> clazz) {
        return new CriteriaBuilder<T>(clazz, StringUtils.firstToLower(clazz.getSimpleName()));
    }
    
    public static <T> CriteriaBuilder<T> from(Class<T> clazz, String alias) {
        return new CriteriaBuilder<T>(clazz, alias);
    }
    
    /* 
     * Where methods
     */
    @Override
    public RestrictionBuilder<CriteriaBuilder<T>> where(String expression) {
        return rootWherePredicate.startBuilder(new RestrictionBuilderImpl<CriteriaBuilder<T>>(this, rootWherePredicate, ExpressionUtils.parse(expression)));
    }
    
    public WhereOrBuilder<CriteriaBuilder<T>> whereOr() {
        return rootWherePredicate.startBuilder(new WhereOrBuilderImpl<CriteriaBuilder<T>>(this, rootWherePredicate));
    }
    
    public RestrictionBuilder<CriteriaBuilder<T>> having(String expression) {
        return rootHavingPredicate.startBuilder(new RestrictionBuilderImpl<CriteriaBuilder<T>>(this, rootHavingPredicate, ExpressionUtils.parse(expression)));
    }
    
    public HavingOrBuilder<CriteriaBuilder<T>> havingOr() {
        return rootHavingPredicate.startBuilder(new HavingOrBuilderImpl<CriteriaBuilder<T>>(this, rootHavingPredicate));
    }
    
    /* 
     * Order by methods
     */
    public CriteriaBuilder<T> orderByDesc(String path) {
        return orderBy(path, false, false);
    }
    
    public CriteriaBuilder<T> orderByAsc(String path) {
        return orderBy(path, true, false);
    }
    
    public CriteriaBuilder<T> orderByDesc(String path, boolean nullFirst) {
        return orderBy(path, false, nullFirst);
    }
    
    public CriteriaBuilder<T> orderByAsc(String path, boolean nullFirst) {
        return orderBy(path, true, nullFirst);
    }
    
    private void verifyBuilderEnded() {
        rootWherePredicate.verifyBuilderEnded();
        rootHavingPredicate.verifyBuilderEnded();
    }
    
    public CriteriaBuilder<T> orderBy(String path, boolean ascending, boolean nullFirst) {
        verifyBuilderEnded();
        
        JoinNode node;
        String normalizedPath;
        String orderByField;
        
        if (startsAtRootAlias(path)) {
            // The given path is relative to the root
            normalizedPath = path.substring(rootAliasInfo.alias.length() + 1);
        } else {
            // The path is either already normalized or uses a specific alias as base
            normalizedPath = path;
        }

        int dotIndex;
        int fieldStartDotIndex;
        if ((fieldStartDotIndex = normalizedPath.lastIndexOf('.')) != -1) {
            // First we extract the field by which should be ordered
            orderByField = normalizedPath.substring(fieldStartDotIndex + 1);
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
                String potentialBasePath = potentialBaseInfo.absolutePath;
                JoinNode aliasNode = findNode(rootNode, potentialBasePath);
                String relativePath = normalizedPath.substring(aliasNode.aliasInfo.alias.length() + 1);
                normalizedPath = potentialBasePath + '.' + relativePath;
                String relativeJoinPath = relativePath.substring(0, relativePath.length() - orderByField.length());
                
                if (relativeJoinPath.isEmpty()) {
                    node = aliasNode;
                } else {
                    node = createOrUpdateNodeForOrderBy(aliasNode, potentialBasePath, relativeJoinPath);
                }
            } else {
                // The given path is relative to the root
                node = createOrUpdateNodeForOrderBy(rootNode, "", joinPath);
            }
        } else {
            // The given path is relative to the root
            node = rootNode;
            orderByField = normalizedPath;
        }
        
        orderByInfos.put(normalizedPath, new OrderByInfo(node.aliasInfo, orderByField, ascending, nullFirst));
        return this;
    }
    
    /* 
     * Join methods
     */
    
    public CriteriaBuilder<T> innerJoin(String path, String alias) {
        return join(path, alias, JoinType.INNER, false);
    }
    
    public CriteriaBuilder<T> innerJoinFetch(String path, String alias) {
        return join(path, alias, JoinType.INNER, true);
    }
    
    public CriteriaBuilder<T> leftJoin(String path, String alias) {
        return join(path, alias, JoinType.LEFT, false);
    }
    
    public CriteriaBuilder<T> leftJoinFetch(String path, String alias) {
        return join(path, alias, JoinType.LEFT, true);
    }
    
    public CriteriaBuilder<T> rightJoin(String path, String alias) {
        return join(path, alias, JoinType.RIGHT, false);
    }
    
    public CriteriaBuilder<T> rightJoinFetch(String path, String alias) {
        return join(path, alias, JoinType.RIGHT, true);
    }
    
    public CriteriaBuilder<T> outerJoin(String path, String alias) {
        return join(path, alias, JoinType.OUTER, false);
    }
    
    public CriteriaBuilder<T> outerJoinFetch(String path, String alias) {
        return join(path, alias, JoinType.OUTER, true);
    }
    
    public CriteriaBuilder<T> join(String path, String alias, JoinType type, boolean fetch) {
        verifyBuilderEnded();
        
        String normalizedPath;
        
        if (startsAtRootAlias(path)) {
            // The given path is relative to the root
            normalizedPath = path.substring(rootAliasInfo.alias.length() + 1);
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
                    String potentialBasePath = potentialBaseInfo.absolutePath;
                    JoinNode aliasNode = findNode(rootNode, potentialBasePath);
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
        return path.startsWith(rootAliasInfo.alias) && path.length() > rootAliasInfo.alias.length() && path.charAt(rootAliasInfo.alias.length()) == '.';
    }
    
    private JoinNode findNode(JoinNode baseNode, String path) {
        JoinNode currentNode = baseNode;
        String[] pathElements = path.split("\\.");
        
        for (int i = 0; i < pathElements.length; i++) {
            currentNode = currentNode.nodes.get(pathElements[i]);
        }
        
        return currentNode;
    }
    
    private JoinNode createOrUpdateNodeForOrderBy(JoinNode baseNode, String basePath, String joinPath) {
        return createOrUpdateNode(baseNode, basePath, joinPath, null, null, false, true);
    }
    
    private JoinNode createOrUpdateNode(JoinNode baseNode, String basePath, String joinPath, String alias, JoinType type, boolean fetch, boolean implicit) {
        JoinNode currentNode = baseNode;
        StringBuilder currentPath = new StringBuilder(basePath);
        String joinAlias = alias;
        String[] pathElements = joinPath.split("\\.");
        
        for (int i = 0; i < pathElements.length - 1; i++) {
            // TODO: Implement model aware joining or use fetch profiles or so to decide the join types automatically
            currentNode = getOrCreate(currentPath, currentNode, pathElements[i], pathElements[i], JoinType.LEFT, false, "Ambiguous implicit join", true);
        }
        
        if (joinAlias == null) {
            joinAlias = pathElements[pathElements.length - 1];
        }
        
        if (type == null) {
            // TODO: Implement model aware joining
            type = JoinType.LEFT;
        }
        
        currentNode = getOrCreate(currentPath, currentNode, pathElements[pathElements.length - 1], joinAlias, type, fetch, "Ambiguous alias", implicit);
        
        // We can only change the join type if the existing node is implicit and the update on the node is not implicit
        if (currentNode.aliasInfo.implicit && !implicit) {
            currentNode.type = type;
        }
        if (fetch) {
            currentNode.fetch = true;
        }
        return currentNode;
    }
    
    private JoinNode getOrCreate(StringBuilder currentPath, JoinNode currentNode, String joinRelation, String alias, JoinType type, boolean fetch, String errorMessage, boolean implicit) {
        JoinNode node = currentNode.nodes.get(joinRelation);
            
        if (currentPath.length() > 0) {
            currentPath.append('.');
        }

        currentPath.append(joinRelation);

        if (node == null) {
            String currentJoinPath = currentPath.toString();
            AliasInfo oldAliasInfo = aliasInfos.get(alias);

            if (oldAliasInfo != null) {
                if (!oldAliasInfo.absolutePath.equals(currentJoinPath)) {
                    throw new IllegalArgumentException(errorMessage);
                } else {
                    throw new RuntimeException("Probably a programming error if this happens. An alias[" + alias + "] for the same join path[" + currentJoinPath + "] is available but the join node is not!");
                }
            } else {
                node = new JoinNode(new AliasInfo(alias, currentJoinPath, implicit), type, fetch);
                aliasInfos.put(alias, node.aliasInfo);
            }

            currentNode.nodes.put(joinRelation, node);
        } else {
            AliasInfo nodeAliasInfo = node.aliasInfo;
            
            if (!alias.equals(nodeAliasInfo.alias)) {
                // Aliases for the same join paths don't match
                if (nodeAliasInfo.implicit && !implicit) {
                    // Overwrite implicit aliases
                    String oldAlias = nodeAliasInfo.alias;
                    nodeAliasInfo.alias = alias;
                    nodeAliasInfo.implicit = false;
                    aliasInfos.remove(oldAlias);
                    aliasInfos.put(alias, nodeAliasInfo);
                } else if (!nodeAliasInfo.implicit && !implicit) {
                    throw new IllegalArgumentException("Alias conflict[" + nodeAliasInfo.alias + "=" + nodeAliasInfo.absolutePath + ", " + alias + "=" + currentPath.toString() + "]");
                }
            }
        }

        return node;
    }
    
    private static void applyJoins(StringBuilder sb, AliasInfo joinBase, Map<String, JoinNode> nodes) {
        for (Map.Entry<String, JoinNode> nodeEntry : nodes.entrySet()) {
            String relation = nodeEntry.getKey();
            JoinNode node = nodeEntry.getValue();
            
            sb.append(' ');
            
            switch (node.type) {
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
            
            if (node.fetch) {
                sb.append("FETCH ");
            }
            
            sb.append(joinBase.alias).append('.').append(relation).append(' ').append(node.aliasInfo.alias);
            
            if (!node.nodes.isEmpty()) {
                applyJoins(sb, node.aliasInfo, node.nodes);
            }
        }
    }
    
    private static void applyOrderBys(StringBuilder sb, Map<String, OrderByInfo> orderBys) {
        if (orderBys.isEmpty()) {
            return;
        }
        
        sb.append(" ORDER BY ");
        Iterator<OrderByInfo> iter = orderBys.values().iterator();
        applyOrderBy(sb, iter.next());
        
        while(iter.hasNext()) {
            sb.append(", ");
            applyOrderBy(sb, iter.next());
        }
    }
    
    private static void applyOrderBy(StringBuilder sb, OrderByInfo orderBy) {
        sb.append(orderBy.baseAliasInfo.alias)
            .append('.')
            .append(orderBy.field);
        
        if (!orderBy.ascending) {
            sb.append(" DESC");
        }
        
        if (orderBy.nullFirst) {
            sb.append(" NULLS FIRST");
        } else {
            sb.append(" NULLS LAST");
        }
    }

//    private void applyConditions(StringBuilder sb, List<Condition> conditions) {
//        
//    }
    
    public String getQueryString() {
        verifyBuilderEnded();
        StringBuilder sb = new StringBuilder();
        
        sb.append("FROM ").append(clazz.getSimpleName()).append(' ').append(rootAliasInfo.alias);
        applyJoins(sb, rootAliasInfo, rootNode.nodes);
//        applyConditions(sb, conditions);
        applyOrderBys(sb, orderByInfos);
        
        return sb.toString();
    }
    
    public TypedQuery<T> getQuery(EntityManager em) {
        TypedQuery<T> query = em.createQuery(getQueryString(), clazz);
        
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        
        return query;
    }
    
    private static class JoinNode {
        private AliasInfo aliasInfo;
        private JoinType type = JoinType.LEFT;
        private boolean fetch = false;
        private final Map<String, JoinNode> nodes = new HashMap<String, JoinNode>();
        
        public JoinNode(AliasInfo aliasInfo, JoinType type, boolean fetch) {
            this.aliasInfo = aliasInfo;
            this.type = type;
            this.fetch = fetch;
        }
    }
    
    private static class AliasInfo {
        private String alias;
        // The absolute normalized path with the root as implicit base
        private String absolutePath;
        private boolean implicit;

        public AliasInfo(String alias, String absolutePath, boolean implicit) {
            this.alias = alias;
            this.absolutePath = absolutePath;
            this.implicit = implicit;
        }
    }
    
    private static class OrderByInfo {
        private AliasInfo baseAliasInfo;
        private String field;
        private boolean ascending;
        private boolean nullFirst;

        public OrderByInfo(AliasInfo baseAliasInfo, String field, boolean ascending, boolean nullFirst) {
            this.baseAliasInfo = baseAliasInfo;
            this.field = field;
            this.ascending = ascending;
            this.nullFirst = nullFirst;
        }
    }
    
    private static class RootPredicate extends AbstractBuilderEndedListener {
        
        private final AndPredicate predicate;

        public RootPredicate() {
            this.predicate = new AndPredicate();
        }
        
    
        @Override
        public void onBuilderEnded(PredicateBuilder builder) {
            super.onBuilderEnded(builder);
            predicate.getChildren().add(builder.getPredicate());
        }
    }
}
