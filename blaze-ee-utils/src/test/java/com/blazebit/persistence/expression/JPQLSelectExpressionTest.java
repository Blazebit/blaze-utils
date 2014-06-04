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
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author ccbem
 */
public class JPQLSelectExpressionTest {

    private CompositeExpression parse(String expr){
        JPQLSelectExpressionLexer l = new JPQLSelectExpressionLexer(new ANTLRInputStream(expr));
        CommonTokenStream tokens = new CommonTokenStream(l);
        JPQLSelectExpressionParser p = new JPQLSelectExpressionParser(tokens);
        JPQLSelectExpressionParser.ParseSimpleExpressionContext ctx = p.parseSimpleExpression();

        ParseTreeWalker w = new ParseTreeWalker();
        
        JPQLParseTreeListenerImpl listener = new JPQLParseTreeListenerImpl();
        w.walk(listener, ctx);
        
        return listener.getCompositeExpression();
    }
    
    private PathExpression path(String ... properties){
        PathExpression p = new PathExpression(new ArrayList<PropertyExpression>());
        for(String pathElem : properties){
            p.getExpressions().add(new PropertyExpression(pathElem));
        }
        return p;
    }
    
    @Test
    public void testParser1() {
        CompositeExpression result = parse("AVG(d.age)");
        List<Expression> expressions = result.getExpressions();
        
        System.out.println(result.toString());
        
        assertTrue(expressions.size() == 3);
        assertTrue(expressions.get(0).equals(new FooExpression("AVG(")));
        assertTrue(expressions.get(1).equals(path("d", "age")));
        assertTrue(expressions.get(2).equals(new FooExpression(")")));
    }
    
    
    
    @Test
    public void testParser2() {
        CompositeExpression result = parse("d.problem.age");
        List<Expression> expressions = result.getExpressions();
        
        System.out.println(result.toString());
        
        assertTrue(expressions.size() == 1);
        assertTrue(expressions.get(0).equals(path("d", "problem", "age")));
    }
    
    @Test
    public void testParser3() {
        CompositeExpression result = parse("age");
        List<Expression> expressions = result.getExpressions();
        
        System.out.println(result.toString());
        
        assertTrue(expressions.size() == 1);
        assertTrue(expressions.get(0).equals(path("age")));
    }
    
    @Test
    public void testParserArithmetic() {
        CompositeExpression result = parse("d.age + SUM(d.children.age)");
        List<Expression> expressions = result.getExpressions();
        
        System.out.println(result.toString());
        
        assertTrue(expressions.size() == 4);
        
        assertTrue(expressions.get(0).equals(path("d", "age")));
        assertTrue(expressions.get(1).equals(new FooExpression("+SUM(")));
        assertTrue(expressions.get(2).equals(path("d", "children", "age")));
        assertTrue(expressions.get(3).equals(new FooExpression(")")));
    }
}
