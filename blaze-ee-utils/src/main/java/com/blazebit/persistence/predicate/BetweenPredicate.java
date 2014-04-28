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

import com.blazebit.persistence.expression.Expression;

/**
 *
 * @author cpbec
 */
public class BetweenPredicate implements Predicate {
    
    private final Expression left;
    private final Expression start;
    private final Expression end;

    public BetweenPredicate(Expression left, Expression start, Expression end) {
        this.left = left;
        this.start = start;
        this.end = end;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getStart() {
        return start;
    }

    public Expression getEnd() {
        return end;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}