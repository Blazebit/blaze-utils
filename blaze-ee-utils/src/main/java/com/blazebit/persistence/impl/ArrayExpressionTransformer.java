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

import com.blazebit.persistence.expression.ArrayExpression;
import com.blazebit.persistence.expression.CompositeExpression;
import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.FooExpression;
import com.blazebit.persistence.expression.ParameterExpression;
import com.blazebit.persistence.expression.PathElementExpression;
import com.blazebit.persistence.expression.PathExpression;
import com.blazebit.persistence.predicate.EqPredicate;
import com.blazebit.persistence.predicate.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cpbec
 */
//TODO: maybe implement contacts[1] = x1 AND contacts[2] = x2?
public class ArrayExpressionTransformer {

    private Map<TransformationInfo, EqPredicate> transformedPathFilterMap = new HashMap<TransformationInfo, EqPredicate>();
    private List<Predicate> additionalWherePredicates = new ArrayList<Predicate>();
    private final JoinManager joinManager;

    public ArrayExpressionTransformer(JoinManager joinManager) {
        this.joinManager = joinManager;
    }

    public Expression transform(Expression original) {
        return transform(original, false);
    }
    
    /**
     *
     * @param original
     * @return the transformed expression
     */
    public Expression transform(Expression original, boolean selectClause) {
        // TODO: transform the original expression and apply changes in the criteria builder
        if (original instanceof FooExpression || original instanceof ParameterExpression) {
            return original;
        }

        if (original instanceof CompositeExpression) {
            CompositeExpression composite = (CompositeExpression) original;
            CompositeExpression transformed = new CompositeExpression(new ArrayList<Expression>());
            for (Expression e : composite.getExpressions()) {
                transformed.getExpressions().add(transform(e, selectClause));
            }
            return transformed;
        }

        if (!(original instanceof PathExpression)) {
            throw new IllegalArgumentException("Probably a programming error");
        }

        PathExpression path = (PathExpression) original;
        ArrayExpression arrayExp = null;
        ArrayExpression farRightArrayExp = null;
        PathExpression farRightValuePath = null;
        EqPredicate farRightValueKeyFilter = null;

        String absBasePath;
        int loopEndIndex = 0;
        if (path.getBaseNode() != null) {
            absBasePath = path.getBaseNode().getAliasInfo().getAbsolutePath();
            
            if (path.getField() != null) {
                absBasePath += "." + path.getField();
            }
            
            String rootAlias;
            if(path.getExpressions().get(0).toString().equals(joinManager.getRootAlias())){
                loopEndIndex = 1;
            }
            
//            String[] absBasePathParts = absBasePath.split(".");
//            StringBuilder basePath 
//            for(int i = absBasePathParts.length - path.getExpressions().size(); i < absBasePathParts.length; i++){
//                
//            }
//            path.getExpressions().
            
//            int lastDotIndex;
//            if((lastDotIndex = absBasePath.lastIndexOf('.')) != -1){
//                absolutePathBuilder.append(absBasePath.substring(0, lastDotIndex));
//            }
        } else {
            throw new IllegalStateException("Path expression without base node");
        }

        //TODO: set baseNodes on created PathExpressions
        
        for (int i = path.getExpressions().size() - 1; i >= loopEndIndex; i--) {

            PathElementExpression expr = path.getExpressions().get(i);
            arrayExp = null;

            if (expr instanceof ArrayExpression) {
                arrayExp = (ArrayExpression) expr;

                String currentAbsPath = absBasePath;
//                currentAbsPath = currentAbsPath + arrayExp.getBase().toString();
//                String alias = joinManager.getAliasInfoByJoinPath(currentAbsPath).getAlias();
                TransformationInfo transInfo = new TransformationInfo(currentAbsPath, arrayExp.getIndex().toString());
                EqPredicate valueKeyFilterPredicate;
                if ((valueKeyFilterPredicate = transformedPathFilterMap.get(transInfo)) == null) {
                    CompositeExpression keyExpression = new CompositeExpression(new ArrayList<Expression>());
                    keyExpression.getExpressions().add(new FooExpression("KEY("));

                    PathExpression keyPath = new PathExpression(new ArrayList<PathElementExpression>(/*transformedPath.getExpressions()*/));
                    keyPath.getExpressions().add(arrayExp.getBase());
                    keyExpression.getExpressions().add(keyPath);
                    keyExpression.getExpressions().add(new FooExpression(")"));
                    valueKeyFilterPredicate = new EqPredicate(keyExpression, arrayExp.getIndex());
                    addWherePredicate(valueKeyFilterPredicate);
                    transformedPathFilterMap.put(transInfo, valueKeyFilterPredicate);

                }

                if (farRightArrayExp == null) {
                    farRightArrayExp = arrayExp;
                    farRightValueKeyFilter = valueKeyFilterPredicate;
                    // this is only necessary for correct map dereferencing output (e.g. VALUE(xy).someproperty) )
                    // however, such dereferencing is not supported by JPQL so we could also remove this
                    List<PathElementExpression> farRightValuePathElements = new ArrayList<PathElementExpression>();
                    for (int j = i + 1; j < path.getExpressions().size(); j++) {
                        farRightValuePathElements.add(path.getExpressions().get(j));
                    }
                    if (farRightValuePathElements.isEmpty() == false) {
                        farRightValuePath = new PathExpression(farRightValuePathElements);
                    }
                }

            }

            if (i == loopEndIndex) {
                absBasePath = "";
            } else {
                absBasePath = absBasePath.substring(0, absBasePath.lastIndexOf('.'));
            }
        }

        if (farRightArrayExp != null) {
            // add value for last array expression
            CompositeExpression valueExpression = new CompositeExpression(new ArrayList<Expression>());
            valueExpression.getExpressions().add(new FooExpression("VALUE("));
            PathExpression valuePath = new PathExpression();
            valuePath.getExpressions().add(farRightArrayExp.getBase());
            valueExpression.getExpressions().add(valuePath);
            if (farRightValuePath != null) {
                valueExpression.getExpressions().add(new FooExpression(")."));
                valueExpression.getExpressions().add(farRightValuePath);
            } else {
                valueExpression.getExpressions().add(new FooExpression(")"));
            }
            
            if(selectClause == true){
                farRightValueKeyFilter.setRequiredByMapValueSelect(true);
            }

            return valueExpression;
        }

        return original;
    }

    private void addWherePredicate(Predicate predicate) {
        additionalWherePredicates.add(predicate);
    }

    List<Predicate> getAdditionalWherePredicates() {
        return additionalWherePredicates;
    }
    
    private static class TransformationInfo {

        public TransformationInfo(String absoluteFieldPath, String indexedField) {
            this.absoluteFieldPath = absoluteFieldPath;
            this.indexedField = indexedField;
        }

        private final String absoluteFieldPath;
        private final String indexedField;

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.absoluteFieldPath != null ? this.absoluteFieldPath.hashCode() : 0);
            hash = 97 * hash + (this.indexedField != null ? this.indexedField.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TransformationInfo other = (TransformationInfo) obj;
            if ((this.absoluteFieldPath == null) ? (other.absoluteFieldPath != null) : !this.absoluteFieldPath.equals(other.absoluteFieldPath)) {
                return false;
            }
            if ((this.indexedField == null) ? (other.indexedField != null) : !this.indexedField.equals(other.indexedField)) {
                return false;
            }
            return true;
        }
    }
}
