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
import com.blazebit.persistence.expression.Expression;
import com.blazebit.persistence.expression.ParameterExpression;
import com.blazebit.persistence.expression.PropertyExpression;
import java.util.Map;

/**
 *
 * @author ccbem
 */
public class ExpressionVisitorImpl implements Expression.Visitor{
    private StringBuilder sb = new StringBuilder();
    private ParameterNameGenerator paramNameGenerator;
    private Map<String, Object> parameters;
    
    public ExpressionVisitorImpl(ParameterNameGenerator paramNameGenerator, Map<String,Object> parameters){
        this.paramNameGenerator = paramNameGenerator;
        this.parameters = parameters;
    }
    
    public String getString(){
        return sb.toString();
    }
    
    @Override
    public void visit(PropertyExpression expression) {
        sb.append(expression.getProperty());
    }

    @Override
    public void visit(ParameterExpression expression) {
        String paramName = paramNameGenerator.getNextName();
        sb.append(":");
        sb.append(paramNameGenerator.getNextName());
        
        parameters.put(paramName, expression.getValue());
    }
    
}
