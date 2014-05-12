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
import java.util.IdentityHashMap;
import java.util.Map;

/**
 *
 * @author ccbem
 */
public class ParameterNameGeneratorImpl implements ParameterNameGenerator {
    private static final String prefix = "param_";
    private int counter;
    private Map<Object, String> nameCache = new IdentityHashMap<Object, String>();
    
    @Override
    public String getNextName() {
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
