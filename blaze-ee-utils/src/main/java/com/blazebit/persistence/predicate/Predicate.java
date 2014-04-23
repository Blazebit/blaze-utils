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
package com.blazebit.persistence.predicate;

/**
 *
 * @author cpbec
 */
public interface Predicate {
    
    public static interface Visitor {
        
        public void visit(AndPredicate predicate);
        
        public void visit(OrPredicate predicate);
        
        public void visit(NotPredicate predicate);
        
        public void visit(EqPredicate predicate);
        
        public void visit(IsNullPredicate predicate);
        
        public void visit(LikePredicate predicate);
    }
    
    /**
     * The predicate tree is traversed in pre-order.
     * 
     * @param visitor 
     */
    public void accept(Visitor visitor);
}
