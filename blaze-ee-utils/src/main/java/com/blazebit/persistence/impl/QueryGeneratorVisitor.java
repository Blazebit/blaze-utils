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
import com.blazebit.persistence.expression.ArrayExpression;
import com.blazebit.persistence.expression.CompositeExpression;
import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.FooExpression;
import com.blazebit.persistence.expression.ParameterExpression;
import com.blazebit.persistence.expression.PropertyExpression;
import com.blazebit.persistence.predicate.AndPredicate;
import com.blazebit.persistence.predicate.BetweenPredicate;
import com.blazebit.persistence.predicate.BinaryExpressionPredicate;
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
import com.blazebit.persistence.predicate.QuantifiableBinaryExpressionPredicate;
import java.util.Map;

/**
 *
 * @author ccbem
 */
public class QueryGeneratorVisitor implements Predicate.Visitor, Expression.Visitor {

    private final StringBuilder sb;
    private final ParameterNameGenerator paramNameGenerator;

    public QueryGeneratorVisitor(StringBuilder sb, ParameterNameGenerator paramNameGenerator) {
        this.sb = sb;
        this.paramNameGenerator = paramNameGenerator;
    }

    @Override
    public void visit(AndPredicate predicate) {
        if(predicate.getChildren().size() == 1){
            predicate.getChildren().get(0).accept(this);
            return;
        }
        final String and = " AND ";
        for (Predicate child : predicate.getChildren()) {
            if (child instanceof OrPredicate) {
                sb.append("(");
                child.accept(this);
                sb.append(")");
            } else {
                child.accept(this);
            }
            sb.append(and);
        }
        if (predicate.getChildren().size() > 1) {
            sb.delete(sb.length() - and.length(), sb.length());
        }
    }

    @Override
    public void visit(OrPredicate predicate) {
        if(predicate.getChildren().size() == 1){
            predicate.getChildren().get(0).accept(this);
            return;
        }
        final String or = " OR ";
        for (Predicate child : predicate.getChildren()) {
            if (child instanceof AndPredicate) {
                sb.append("(");
                child.accept(this);
                sb.append(")");
            } else {
                child.accept(this);
            }
            sb.append(or);
        }
        if (predicate.getChildren().size() > 1) {
            sb.delete(sb.length() - or.length(), sb.length());
        }
    }

    @Override
    public void visit(NotPredicate predicate) {
        sb.append("NOT ");
        predicate.getPredicate().accept(this);
    }

    @Override
    public void visit(EqPredicate predicate) {
        visitQuantifiableBinaryPredicate(predicate, " = ");
    }

    @Override
    public void visit(IsNullPredicate predicate) {
        predicate.getExpression().accept(this);
        sb.append(" IS NULL");
    }

    @Override
    public void visit(IsEmptyPredicate predicate) {
        predicate.getExpression().accept(this);
        sb.append(" IS EMPTY");
    }

    @Override
    public void visit(IsMemberOfPredicate predicate) {
        predicate.getLeft().accept(this);
        sb.append(" MEMBER OF ");
        predicate.getRight().accept(this);
    }

    @Override
    public void visit(LikePredicate predicate) {
        if (!predicate.isCaseSensitive()) {
            sb.append("UPPER(");
        }
        predicate.getLeft().accept(this);
        if (!predicate.isCaseSensitive()) {
            sb.append(")");
        }
        sb.append(" LIKE ");
        if (!predicate.isCaseSensitive()) {
            sb.append("UPPER(");
        }
        predicate.getRight().accept(this);
        if (!predicate.isCaseSensitive()) {
            sb.append(")");
        }
        if (predicate.getEscapeCharacter() != null) {
            sb.append(" ESCAPE ");
            if (!predicate.isCaseSensitive()) {
                sb.append("UPPER(");
            }
            sb.append("'").append(predicate.getEscapeCharacter()).append("'");
            if (!predicate.isCaseSensitive()) {
                sb.append(")");
            }
        }
    }

    @Override
    public void visit(BetweenPredicate predicate) {
        predicate.getLeft().accept(this);
        sb.append(" BETWEEN ");
        predicate.getStart().accept(this);
        sb.append(" AND ");
        predicate.getEnd().accept(this);
    }

    @Override
    public void visit(InPredicate predicate) {
        predicate.getLeft().accept(this);
        sb.append(" IN (");
        predicate.getRight().accept(this);
        sb.append(")");
    }

    private void visitQuantifiableBinaryPredicate(QuantifiableBinaryExpressionPredicate predicate, String operator){
        predicate.getLeft().accept(this);
        sb.append(operator);
        if (predicate.getQuantifier() != PredicateQuantifier.ONE) {
            sb.append(predicate.getQuantifier().toString());
            sb.append("(");
        }
        predicate.getRight().accept(this);
        if (predicate.getQuantifier() != PredicateQuantifier.ONE) {
            sb.append(")");
        }
    }
    @Override
    public void visit(GtPredicate predicate) {
        visitQuantifiableBinaryPredicate(predicate, " > ");
    }

    @Override
    public void visit(GePredicate predicate) {
        visitQuantifiableBinaryPredicate(predicate, " >= ");
    }

    @Override
    public void visit(LtPredicate predicate) {
        visitQuantifiableBinaryPredicate(predicate, " < ");
    }

    @Override
    public void visit(LePredicate predicate) {
        visitQuantifiableBinaryPredicate(predicate, " <= ");
    }

    /* Expression.Visitor */
    @Override
    public void visit(PropertyExpression expression) {
        sb.append(expression.getBaseNode().getAliasInfo().getAlias())
                .append(".")
                .append(expression.getField()); //TODO: resolve joins
    }

    @Override
    public void visit(ParameterExpression expression) {
        String paramName = paramNameGenerator.getParamNameForObject(expression.getValue());
        sb.append(":");
        sb.append(paramName);
    }

    @Override
    public void visit(CompositeExpression expression) {
        for (Expression e : expression.getExpressions()) {
            e.accept(this);
        }
    }

    @Override
    public void visit(FooExpression expression) {
        sb.append(expression.getString());
    }
}
