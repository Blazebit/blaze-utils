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

import com.blazebit.persistence.ParameterNameGenerator;
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
import com.blazebit.persistence.predicate.PredicateQuantifier;
import java.util.Map;

/**
 *
 * @author ccbem
 */
public class QueryGeneratorVisitor implements Predicate.Visitor {

    private StringBuilder sb = new StringBuilder();
    private Map<String, Object> parameters;
    private ParameterNameGenerator paramNameGenerator;

    public QueryGeneratorVisitor(ParameterNameGenerator paramNameGenerator, Map<String, Object> parameters) {
        this.parameters = parameters;
        this.paramNameGenerator = paramNameGenerator;
    }
    
    public String getString() {
        return sb.toString();
    }

    @Override
    public void visit(AndPredicate predicate) {
        final String and = " and";
        for (Predicate child : predicate.getChildren()) {
            if (child instanceof OrPredicate) {
                sb.append("(");
                child.accept(this);
                sb.append(")");
            } else {
                child.accept(this);
            }
            sb.append(" and");
        }
        sb.deleteCharAt(and.length() - 4);
    }

    @Override
    public void visit(OrPredicate predicate) {
        for (Predicate child : predicate.getChildren()) {
            if (child instanceof AndPredicate) {
                sb.append("(");
                child.accept(this);
                sb.append(")");
            } else {
                child.accept(this);
            }
            sb.append(" or");
        }
    }

    @Override
    public void visit(NotPredicate predicate) {
        sb.append(" not");
        predicate.getPredicate().accept(this);
    }

    @Override
    public void visit(EqPredicate predicate) {
        ExpressionVisitorImpl leftEvaluation = new ExpressionVisitorImpl(paramNameGenerator, parameters);
        predicate.getLeft().accept(leftEvaluation);
        sb.append(leftEvaluation.getString());
        sb.append(" =");
        if(predicate.getQuantifier() != PredicateQuantifier.ONE)
        {
            sb.append(predicate.getQuantifier().toString());
            sb.append("(");
        }
        ExpressionVisitorImpl rightEvaluation = new ExpressionVisitorImpl(paramNameGenerator, parameters);
        predicate.getRight().accept(rightEvaluation);
        sb.append(rightEvaluation.getString());
        if(predicate.getQuantifier() != PredicateQuantifier.ONE)
        {
            sb.append(")");
        }
    }

    @Override
    public void visit(IsNullPredicate predicate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(IsEmptyPredicate predicate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(IsMemberOfPredicate predicate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(LikePredicate predicate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(BetweenPredicate predicate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(InPredicate predicate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(GtPredicate predicate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(GePredicate predicate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(LtPredicate predicate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(LePredicate predicate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
