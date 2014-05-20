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

import com.blazebit.persistence.parser.JPQLSelectExpressionLexer;
import com.blazebit.persistence.parser.JPQLSelectExpressionParser;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Test;

/**
 *
 * @author ccbem
 */
public class JPQLSelectExpressionTest {

    @Test
    public void testParser() {
        JPQLSelectExpressionLexer l = new JPQLSelectExpressionLexer(new ANTLRInputStream("AVG(d.age)"));
        CommonTokenStream tokens = new CommonTokenStream(l);
        JPQLSelectExpressionParser p = new JPQLSelectExpressionParser(tokens);
        JPQLSelectExpressionParser.ParseSimpleExpressionContext ctx = p.parseSimpleExpression();

        ExpressionParseTreeVisitor visitor = new ExpressionParseTreeVisitor();
        ctx.accept(visitor);
        System.out.println(visitor.expression);
    }

    static class ExpressionParseTreeVisitor implements ParseTreeVisitor<Expression> {

        List<Expression> expressions = new ArrayList<Expression>();
        CompositeExpression expression = new CompositeExpression(expressions);

        @Override
        public Expression visit(ParseTree pt) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            System.out.println(pt.getClass());
//            System.out.println(pt.getText());
//            return null;
        }

        @Override
        public Expression visitChildren(RuleNode rn) {
            switch (rn.getRuleContext().getRuleIndex()) {
//                case JPQLSelectExpressionParser.RULE_parseSimpleExpression:
//
//                    break;
//                case JPQLSelectExpressionParser.RULE_parseScalarExpression:
//
//                    break;
//                case JPQLSelectExpressionParser.RULE_parseCaseOperandExpression:
//
//                    break;
                case JPQLSelectExpressionParser.RULE_qualified_identification_variable:

                    break;
                case JPQLSelectExpressionParser.RULE_composable_qualified_identification_variable:

                    break;
                case JPQLSelectExpressionParser.RULE_single_valued_path_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_general_identification_variable:

                    break;
                case JPQLSelectExpressionParser.RULE_general_subpath:

                    break;
                case JPQLSelectExpressionParser.RULE_state_field_path_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_single_valued_object_path_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_collection_valued_path_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_simple_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_aggregate_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_derived_path_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_simple_derived_path:

                    break;
                case JPQLSelectExpressionParser.RULE_scalar_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_comparison_operator:

                    break;
                case JPQLSelectExpressionParser.RULE_arithmetic_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_arithmetic_term:

                    break;
                case JPQLSelectExpressionParser.RULE_arithmetic_factor:

                    break;
                case JPQLSelectExpressionParser.RULE_arithmetic_primary:

                    break;
                case JPQLSelectExpressionParser.RULE_string_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_datetime_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_boolean_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_enum_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_entity_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_simple_entity_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_entity_type_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_type_discriminator:

                    break;
                case JPQLSelectExpressionParser.RULE_functions_returning_numerics:

                    break;
                case JPQLSelectExpressionParser.RULE_functions_returning_datetime:

                    break;
                case JPQLSelectExpressionParser.RULE_functions_returning_strings:

                    break;
                case JPQLSelectExpressionParser.RULE_trim_specification:

                    break;
                case JPQLSelectExpressionParser.RULE_function_invocation:

                    break;
                case JPQLSelectExpressionParser.RULE_function_arg:

                    break;
                case JPQLSelectExpressionParser.RULE_case_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_case_operand:

                    break;
                case JPQLSelectExpressionParser.RULE_coalesce_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_nullif_expression:

                    break;
                case JPQLSelectExpressionParser.RULE_literal:

                    break;
                case JPQLSelectExpressionParser.RULE_literal_temporal:

                    break;
                default:
                    break;
            }

            for (int i = 0; i < rn.getChildCount(); i++) {
                rn.getChild(i)
                    .accept(this);
            }

            return null;
        }

        @Override
        public Expression visitTerminal(TerminalNode tn) {
            System.out.println(tn.getSymbol()
                .getType());
            System.out.println(tn.getText());
            return null;
        }

        @Override
        public Expression visitErrorNode(ErrorNode en) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
