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

import com.blazebit.persistence.JoinType;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author cpbec
 */
public class JoinNode {

    private AliasInfo aliasInfo;
    private JoinType type = JoinType.LEFT;
    private boolean fetch = false;
    private Class<?> propertyClass;
    // Use TreeMap so that joins get applied alphabetically for easier testing
    private final Map<String, JoinNode> nodes = new TreeMap<String, JoinNode>();

    public JoinNode(AliasInfo aliasInfo, JoinType type, boolean fetch, Class<?> propertyClass) {
        this.aliasInfo = aliasInfo;
        this.type = type;
        this.fetch = fetch;
        this.propertyClass = propertyClass;
    }

    public AliasInfo getAliasInfo() {
        return aliasInfo;
    }

    public void setAliasInfo(AliasInfo aliasInfo) {
        this.aliasInfo = aliasInfo;
    }

    public JoinType getType() {
        return type;
    }

    public void setType(JoinType type) {
        this.type = type;
    }

    public boolean isFetch() {
        return fetch;
    }

    public void setFetch(boolean fetch) {
        this.fetch = fetch;
    }

    public Map<String, JoinNode> getNodes() {
        return nodes;
    }

    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    public void setPropertyClass(Class<?> propertyClass) {
        this.propertyClass = propertyClass;
    }
}

