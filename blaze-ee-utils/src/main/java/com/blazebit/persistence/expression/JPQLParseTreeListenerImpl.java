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
package com.blazebit.persistence.expression;

import com.blazebit.persistence.expression.CompositeExpression;
import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.FooExpression;
import com.blazebit.persistence.expression.PropertyExpression;
import com.blazebit.persistence.parser.JPQLSelectExpressionListener;
import com.blazebit.persistence.parser.JPQLSelectExpressionParser;
import java.util.ArrayList;
import org.antlr.runtime.Token;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 *
 * @author ccbem
 */
public class JPQLParseTreeListenerImpl implements JPQLSelectExpressionListener {

    private CompositeExpression root = new CompositeExpression(new ArrayList<Expression>());
    private PathExpression path;
    private StringBuilder fooBuilder = new StringBuilder();
    private boolean inFooContext = true;
    
    public CompositeExpression getCompositeExpression(){
        return root;
    }

    @Override
    public void enterState_field_path_expression(JPQLSelectExpressionParser.State_field_path_expressionContext ctx) {
        endFooContext(); 
        System.out.println("enterState_field_path_expression");
    }

    @Override
    public void exitState_field_path_expression(JPQLSelectExpressionParser.State_field_path_expressionContext ctx) {
        startFooContext();
    }
    
    @Override
    public void enterSingle_valued_object_path_expression(JPQLSelectExpressionParser.Single_valued_object_path_expressionContext ctx) {
        endFooContext();
        System.out.println("enterSingle_valued_object_path_expression");
    }
    
    @Override
    public void exitSingle_valued_object_path_expression(JPQLSelectExpressionParser.Single_valued_object_path_expressionContext ctx) {
        startFooContext();
    }
    
    @Override
    public void enterCollection_valued_path_expression(JPQLSelectExpressionParser.Collection_valued_path_expressionContext ctx) {
        endFooContext();
        System.out.println("enterCollection_valued_path_expression");
    }

    @Override
    public void exitCollection_valued_path_expression(JPQLSelectExpressionParser.Collection_valued_path_expressionContext ctx) {
        startFooContext();
    }

    private void endFooContext(){
        if (fooBuilder.length() > 0) {
            root.getExpressions().add(new FooExpression(fooBuilder.toString()));
            fooBuilder.setLength(0);
        }
        inFooContext = false;
        path = new PathExpression(new ArrayList<PropertyExpression>());
    }
    
    private void startFooContext(){
        inFooContext = true;
        root.getExpressions().add(path);
    }
    
    @Override
    public void enterGeneral_identification_variable(JPQLSelectExpressionParser.General_identification_variableContext ctx) {
        path.getExpressions().add(new PropertyExpression(ctx.getText()));
        System.out.println("enterGeneral_identification_variable");
    }

    @Override
    public void exitGeneral_identification_variable(JPQLSelectExpressionParser.General_identification_variableContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void enterGeneral_path_element(JPQLSelectExpressionParser.General_path_elementContext ctx) {
        path.getExpressions().add(new PropertyExpression(ctx.getText()));
    }

    @Override
    public void exitGeneral_path_element(JPQLSelectExpressionParser.General_path_elementContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void enterGeneral_subpath(JPQLSelectExpressionParser.General_subpathContext ctx) {
//        root.getExpressions().add(new PropertyExpression(ctx.getText()));
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitGeneral_subpath(JPQLSelectExpressionParser.General_subpathContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void enterSimple_identifier(JPQLSelectExpressionParser.Simple_identifierContext ctx) {
        System.out.println("enterSimple_identifier");
        endFooContext();
        path.getExpressions().add(new PropertyExpression(ctx.getText()));
    }

    @Override
    public void exitSimple_identifier(JPQLSelectExpressionParser.Simple_identifierContext ctx) {
        System.out.println("exitSimple_identifier");
        startFooContext();
    }
    
    @Override
    public void enterArithmetic_factor(JPQLSelectExpressionParser.Arithmetic_factorContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitArithmetic_factor(JPQLSelectExpressionParser.Arithmetic_factorContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterCase_operand(JPQLSelectExpressionParser.Case_operandContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitCase_operand(JPQLSelectExpressionParser.Case_operandContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterEnum_expression(JPQLSelectExpressionParser.Enum_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitEnum_expression(JPQLSelectExpressionParser.Enum_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterComparison_operator(JPQLSelectExpressionParser.Comparison_operatorContext ctx) {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitComparison_operator(JPQLSelectExpressionParser.Comparison_operatorContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterString_expression(JPQLSelectExpressionParser.String_expressionContext ctx) {
        
    }

    @Override
    public void exitString_expression(JPQLSelectExpressionParser.String_expressionContext ctx) {

    }

    @Override
    public void enterParseSimpleExpression(JPQLSelectExpressionParser.ParseSimpleExpressionContext ctx) {
        System.out.println("enterParseSimpleExpression");
    }

    @Override
    public void exitParseSimpleExpression(JPQLSelectExpressionParser.ParseSimpleExpressionContext ctx) {
        if(fooBuilder.length() > 0){
            root.getExpressions().add(new FooExpression(fooBuilder.toString()));
        }
    }
    
    

    @Override
    public void enterSimple_entity_expression(JPQLSelectExpressionParser.Simple_entity_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitSimple_entity_expression(JPQLSelectExpressionParser.Simple_entity_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterDatetime_expression(JPQLSelectExpressionParser.Datetime_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitDatetime_expression(JPQLSelectExpressionParser.Datetime_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterParseCaseOperandExpression(JPQLSelectExpressionParser.ParseCaseOperandExpressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitParseCaseOperandExpression(JPQLSelectExpressionParser.ParseCaseOperandExpressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterEntity_expression(JPQLSelectExpressionParser.Entity_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitEntity_expression(JPQLSelectExpressionParser.Entity_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterSimple_expression(JPQLSelectExpressionParser.Simple_expressionContext ctx) {
        System.out.println("enterSimple_expression");
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitSimple_expression(JPQLSelectExpressionParser.Simple_expressionContext ctx) {
        System.out.println("exitSimple_expression");
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterFunctions_returning_strings(JPQLSelectExpressionParser.Functions_returning_stringsContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitFunctions_returning_strings(JPQLSelectExpressionParser.Functions_returning_stringsContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterFunctions_returning_numerics(JPQLSelectExpressionParser.Functions_returning_numericsContext ctx) {
    }

    @Override
    public void exitFunctions_returning_numerics(JPQLSelectExpressionParser.Functions_returning_numericsContext ctx) {
    }

    @Override
    public void enterParseScalarExpression(JPQLSelectExpressionParser.ParseScalarExpressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitParseScalarExpression(JPQLSelectExpressionParser.ParseScalarExpressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterSingle_valued_path_expression(JPQLSelectExpressionParser.Single_valued_path_expressionContext ctx) {
        System.out.println("enterSingle_valued_path_expression");
    }

    @Override
    public void exitSingle_valued_path_expression(JPQLSelectExpressionParser.Single_valued_path_expressionContext ctx) {
    }

    @Override
    public void enterQualified_identification_variable(JPQLSelectExpressionParser.Qualified_identification_variableContext ctx) {
        System.out.println("enterQualified_identification_variable");
    }

    @Override
    public void exitQualified_identification_variable(JPQLSelectExpressionParser.Qualified_identification_variableContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterArithmetic_expression(JPQLSelectExpressionParser.Arithmetic_expressionContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitArithmetic_expression(JPQLSelectExpressionParser.Arithmetic_expressionContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterEntity_type_expression(JPQLSelectExpressionParser.Entity_type_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitEntity_type_expression(JPQLSelectExpressionParser.Entity_type_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterNullif_expression(JPQLSelectExpressionParser.Nullif_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitNullif_expression(JPQLSelectExpressionParser.Nullif_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterFunction_arg(JPQLSelectExpressionParser.Function_argContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitFunction_arg(JPQLSelectExpressionParser.Function_argContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void enterFunctions_returning_datetime(JPQLSelectExpressionParser.Functions_returning_datetimeContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitFunctions_returning_datetime(JPQLSelectExpressionParser.Functions_returning_datetimeContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterTrim_specification(JPQLSelectExpressionParser.Trim_specificationContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitTrim_specification(JPQLSelectExpressionParser.Trim_specificationContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterScalar_expression(JPQLSelectExpressionParser.Scalar_expressionContext ctx) {
        System.out.println("enterScalar_expression");
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitScalar_expression(JPQLSelectExpressionParser.Scalar_expressionContext ctx) {
        System.out.println("exitScalar_expression");
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterArithmetic_term(JPQLSelectExpressionParser.Arithmetic_termContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitArithmetic_term(JPQLSelectExpressionParser.Arithmetic_termContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterComposable_qualified_identification_variable(JPQLSelectExpressionParser.Composable_qualified_identification_variableContext ctx) {
        System.out.println("enterComposable_qualified_identification_variable");
    }

    @Override
    public void exitComposable_qualified_identification_variable(JPQLSelectExpressionParser.Composable_qualified_identification_variableContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterBoolean_expression(JPQLSelectExpressionParser.Boolean_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitBoolean_expression(JPQLSelectExpressionParser.Boolean_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterAggregate_expression(JPQLSelectExpressionParser.Aggregate_expressionContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitAggregate_expression(JPQLSelectExpressionParser.Aggregate_expressionContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterType_discriminator(JPQLSelectExpressionParser.Type_discriminatorContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitType_discriminator(JPQLSelectExpressionParser.Type_discriminatorContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterFunction_invocation(JPQLSelectExpressionParser.Function_invocationContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitFunction_invocation(JPQLSelectExpressionParser.Function_invocationContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterCoalesce_expression(JPQLSelectExpressionParser.Coalesce_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitCoalesce_expression(JPQLSelectExpressionParser.Coalesce_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterLiteral_temporal(JPQLSelectExpressionParser.Literal_temporalContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitLiteral_temporal(JPQLSelectExpressionParser.Literal_temporalContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterArithmetic_primary(JPQLSelectExpressionParser.Arithmetic_primaryContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitArithmetic_primary(JPQLSelectExpressionParser.Arithmetic_primaryContext ctx) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterCase_expression(JPQLSelectExpressionParser.Case_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitCase_expression(JPQLSelectExpressionParser.Case_expressionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterLiteral(JPQLSelectExpressionParser.LiteralContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitLiteral(JPQLSelectExpressionParser.LiteralContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        if(inFooContext){
            fooBuilder.append(node.getSymbol().getText());
        }
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {

    }

    

    

}
