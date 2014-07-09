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

import com.blazebit.persistence.impl.SelectManager.SelectInfo;
import java.util.List;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import org.hibernate.transform.ResultTransformer;

/**
 *
 * @author ccbem
 */
public class TupleResultTransformer implements ResultTransformer {

    private final SelectManager<?> selectManager;

    public TupleResultTransformer(SelectManager<?> selectManager) {
        this.selectManager = selectManager;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        return new TupleImpl(tuple);
    }

    @Override
    public List transformList(List collection) {
        return collection;
    }

    class TupleImpl implements Tuple {

        private final Object[] tuple;

        private TupleImpl(Object[] tuple) {
            if (tuple.length != selectManager.getSelectAliasToInfoMap().size()) {
                throw new IllegalArgumentException(
                        "Size mismatch between tuple result [" + tuple.length
                        + "] and expected tuple elements [" + selectManager.getSelectAbsolutePathToInfoMap().size() + "]"
                );
            }
            this.tuple = tuple;
        }

        public <X> X get(TupleElement<X> tupleElement) {
            throw new UnsupportedOperationException("Not supported");
        }

        public Object get(String alias) {
            int index = -1;
            if (alias != null) {
                alias = alias.trim();
                if (alias.length() > 0) {
                    if (selectManager.getSelectAliasToPositionMap().containsKey(alias)) {
                        index = selectManager.getSelectAliasToPositionMap().get(alias);
                    }
                }
            }
            if (index < 0) {
                throw new IllegalArgumentException(
                        "Given alias [" + alias + "] did not correspond to an element in the result tuple"
                );
            }
            // index should be "in range" by nature of size check in ctor
            return tuple[index];
        }

        public <X> X get(String alias, Class<X> type) {
            return (X) get(alias);
        }

        public Object get(int i) {
            if (i >= tuple.length) {
                throw new IllegalArgumentException(
                        "Given index [" + i + "] was outside the range of result tuple size [" + tuple.length + "] "
                );
            }
            return tuple[i];
        }

        public <X> X get(int i, Class<X> type) {
            return (X) get(i);
        }

        public Object[] toArray() {
            return tuple;
        }

        public List<TupleElement<?>> getElements() {
            throw new UnsupportedOperationException("Not supported");
        }

    }
}
