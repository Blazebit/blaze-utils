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

import com.blazebit.persistence.HelloLexer;
import com.blazebit.persistence.HelloListener;
import com.blazebit.persistence.HelloParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Test;

/**
 *
 * @author ccbem
 */
public class ParserTest {

    @Test
    public void testParser() {
        HelloLexer l = new HelloLexer(new ANTLRInputStream("hello test"));
        CommonTokenStream tokens = new CommonTokenStream(l);
        HelloParser p = new HelloParser(tokens);
        
        HelloListener listener =  new HelloListener() {

            @Override
            public void enterR(HelloParser.RContext ctx) {
                System.out.println("Enter R");
            }

            @Override
            public void exitR(HelloParser.RContext ctx) {
                System.out.println("Exit R");
            }

            @Override
            public void visitTerminal(TerminalNode node) {
                System.out.println(node.getSymbol());
            }

            @Override
            public void visitErrorNode(ErrorNode node) {
            }

            @Override
            public void enterEveryRule(ParserRuleContext ctx) {
                System.out.println("Enter " + ctx.toString());
            }

            @Override
            public void exitEveryRule(ParserRuleContext ctx) {
            }
        };
        ParseTreeWalker.DEFAULT.walk(listener, p.r());
//        HelloParser.RContext tree = p.r();
//        System.out.println(tree.toStringTree());        
    }

}
