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

package com.blazebit.persistence.parser;

import com.blazebit.persistence.JPQLSelectExpressionLexer;
import com.blazebit.persistence.JPQLSelectExpressionParser;
import com.blazebit.persistence.TestGrammarLexer;
import com.blazebit.persistence.TestGrammarParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
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
        JPQLSelectExpressionParser.Parse_select_expressionContext ctx = p.parse_select_expression();
        
//        ctx.children.get(0).
    }
    
    @Test
    public void testParser2() {
        TestGrammarLexer l = new TestGrammarLexer(new ANTLRInputStream("AVG(d.age)"));
        CommonTokenStream tokens = new CommonTokenStream(l);
        TestGrammarParser p = new TestGrammarParser(tokens);
        
        System.out.println(p.parse_test_grammar().toStringTree());
    }
}
