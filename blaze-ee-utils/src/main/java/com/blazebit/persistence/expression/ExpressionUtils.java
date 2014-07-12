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
import java.util.BitSet;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author cpbec
 */
public final class ExpressionUtils {

    public static Expression parse(String expression) {
        if(expression == null){
            throw new NullPointerException("expression");
        }
        if(expression.isEmpty()){
            throw new IllegalArgumentException("expression");
        }
        JPQLSelectExpressionLexer l = new JPQLSelectExpressionLexer(new ANTLRInputStream(expression));
        l.addErrorListener(new ANTLRErrorListener() {

            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw e;
            }

            @Override
            public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
            }

            @Override
            public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
            }

            @Override
            public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
            }
        });
        CommonTokenStream tokens = new CommonTokenStream(l);
        JPQLSelectExpressionParser p = new JPQLSelectExpressionParser(tokens);
        JPQLSelectExpressionParser.ParseSimpleExpressionContext ctx = p.parseSimpleExpression();

        ParseTreeWalker w = new ParseTreeWalker();
        
        JPQLParseTreeListenerImpl antlrToExpressionTransformer = new JPQLParseTreeListenerImpl();
        
        w.walk(antlrToExpressionTransformer, ctx);
        
        CompositeExpression expr = antlrToExpressionTransformer.getCompositeExpression();
        
        // unwrap composite expression with single child
        if(expr.getExpressions().size() == 1){
            return expr.getExpressions().get(0);
        }
        return expr;
    }

    public static Expression parseCaseOperand(String caseOperandExpression) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Expression parseScalarExpression(String expression) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static String getFirstPathElement(String path) {
        String elem;
        int firstDotIndex;
        if ((firstDotIndex = path.indexOf('.')) == -1) {
            elem = path;
        } else {
            elem = path.substring(0, firstDotIndex);
        }
        return elem;
    }
}
