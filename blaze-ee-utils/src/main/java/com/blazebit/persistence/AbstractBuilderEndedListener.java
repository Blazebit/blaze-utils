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
    
    protected final Set<PredicateBuilder> startedBuilders = Collections.newSetFromMap(new IdentityHashMap<PredicateBuilder, Boolean>());

    @Override
    public void onBuilderEnded(PredicateBuilder o) {
        if (!startedBuilders.remove(o)) {
            throw new IllegalArgumentException("Invalid builder ended notification! " + o);
        }
    }
    
}
