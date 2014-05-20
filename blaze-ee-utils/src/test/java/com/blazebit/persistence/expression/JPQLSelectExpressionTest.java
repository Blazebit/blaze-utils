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
        JPQLSelectExpressionParser.ParseSimpleExpressionContext ctx = p.parseSimpleExpression();
        
        System.out.println(ctx.toStringTree());
//        ctx.children.get(0).
    }
    
}
