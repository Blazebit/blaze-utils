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
import java.util.List;

/**
 *
 * @author cpbec
 */
public interface RestrictionBuilder<T> extends PredicateBuilder {
    
    // TODO: [expression] [operator] [ALL | ANY | SOME] [subquery]
    
    // Functions TODO: SIZE, UPPER, LOWER, TRIM, CONCAT
    //                 COUNT, AVG, MIN, MAX, SUM
    
    // Operators TODO: IN [subquery], 
    //                 EXISTS [subquery]
    
    public T between(Object start, Object end);
    
    public T notBetween(Object start, Object end);
    
    public QuantifiableBinaryPredicateBuilder<T> equalTo();
    
    public T equalTo(Object value);
    
    public T equalToExpression(String expression);
    
    public QuantifiableBinaryPredicateBuilder<T> notEqualTo();
    
    public T notEqualTo(Object value);
    
    public T notEqualToExpression(String expression);
    
    public QuantifiableBinaryPredicateBuilder<T> greaterThan();
    
    public T greaterThan(Object value);
    
    public T greaterThanExpression(String expression);
    
    public QuantifiableBinaryPredicateBuilder<T> greaterOrEqualThan();
    
    public T greaterOrEqualThan(Object value);
    
    public T greaterOrEqualThanExpression(String expression);
    
    public QuantifiableBinaryPredicateBuilder<T> lowerThan();
    
    public T lowerThan(Object value);
    
    public T lowerThanExpression(String expression);
    
    public QuantifiableBinaryPredicateBuilder<T> lowerOrEqualThan();
    
    public T lowerOrEqualThan(Object value);
    
    public T lowerOrEqualThanExpression(String expression);
    
    //public T in(CriteriaBuilder builder);
    
    public T in(List<?> values);
    
    public T inElements(String expression);
    
    public T inIndices(String expression);
    
    //public T exists(CriteriaBuilder builder);
    
    public T existsElements(String expression);
    
    public T existsIndices(String expression);
    
    public T isNull();
    
    public T isNotNull();
    
    public T isEmpty();
    
    public T isNotEmpty();
    
    public T isMemberOf(String expression);
    
    public T isNotMemberOf(String expression);
    
    public T like(String value);
    
    public T like(String value, boolean caseSensitive, Character escapeCharacter);
    
    public T likeExpression(String expression);
    
    public T likeExpression(String expression, boolean caseSensitive, Character escapeCharacter);
    
    public T notLike(String value);
    
    public T notLike(String value, boolean caseSensitive, Character escapeCharacter);
    
    public T notLikeExpression(String expression);
    
    public T notLikeExpression(String expression, boolean caseSensitive, Character escapeCharacter);
}
