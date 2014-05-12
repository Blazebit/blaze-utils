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
package com.blazebit.persistence;

import com.blazebit.persistence.predicate.PredicateBuilder;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 *
 * @author cpbec
 */
public class AbstractBuilderEndedListener implements BuilderEndedListener{
    
    private PredicateBuilder currentBuilder;
    
    protected void verifyBuilderEnded() {
        if (currentBuilder != null) {
            throw new IllegalStateException("A builder was not ended properly.");
        }
    }
    
    protected <T extends PredicateBuilder> T startBuilder(T builder) {
        if (currentBuilder != null) {
            throw new IllegalStateException("There was an attempt to start a builder but a previous builder was not ended.");
        }
        
        currentBuilder = builder;
        return builder;
    }

    @Override
    public void onBuilderEnded(PredicateBuilder builder) {
        if (currentBuilder == null) {
            throw new IllegalStateException("There was an attempt to end a builder that was not started or already closed.");
        }
        currentBuilder = null;
    }
    
}
