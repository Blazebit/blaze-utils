/**
 * Copyright 2012 Blazebit
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.blazebit.collection;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 *
 * @param <V> The value type that the pattern trie holds.
 *
 * @author Christian Beikov
 *
 */
public class PatternTrie<V> implements Serializable {

    private static final long serialVersionUID = 1L;
    private final TrieNode<V> root;

    /**
     * Constructs an empty TrieMap
     */
    public PatternTrie() {
        this.root = new TrieNode<V>(false);
    }

    public interface ParameterizedKeyBuilder<V> {

        ParameterizedKeyBuilder<V> with(String parameterName, String pattern);
    }

    public interface ParameterizedValue<V> {

        public V getValue();

        public String getParameter(String patternKey);
    }

    public V add(CharSequence key, V value) {
        final CharSequence checkedKey = keyCheck(key);
        final int keyLength = checkedKey.length();
        final V replacedValue;
        TrieNode<V> currentNode = root;
        TrieNode<V> lastNode = null;
        int i = 0;

        while (i < keyLength && currentNode != null) {
            lastNode = currentNode;
            currentNode = currentNode.children.get(checkedKey.charAt(i));
            ++i;
        }

        if (currentNode == null) {
            /* We could not find the node for the given key, so create it */
            currentNode = lastNode;
            final TrieNode<V> newNode = new TrieNode<V>(true);

            addNode(currentNode, checkedKey, --i, newNode);
            newNode.value = value;
            replacedValue = null;
        } else if (currentNode.inUse) {
            /* We found the node and it is in use, so replace the value */
            replacedValue = currentNode.value;

            if (replacedValue != value && (replacedValue == null || !replacedValue.equals(value))) {
                currentNode.value = value;
            }
        } else {
            /* We found a node that is not in use, so just set the value */
            currentNode.value = value;
            currentNode.inUse = true;
            replacedValue = null;
        }

        return replacedValue;
    }

    public ParameterizedKeyBuilder<V> addParameterized(CharSequence pattern, V value) {
        throw new UnsupportedOperationException();
    }

    public Set<V> resolve(String key) {
        throw new UnsupportedOperationException();
    }

    private static final class TrieNode<V> implements Serializable {

        private static final long serialVersionUID = 1L;
        private final Map<Character, TrieNode<V>> children;
        private V value;
        private boolean inUse;

        public TrieNode(final V value, final boolean inUse) {
            this.children = new HashMap<Character, TrieNode<V>>();
            this.value = value;
            this.inUse = inUse;
        }

        private TrieNode(final V value, final boolean inUse, int childrenSize) {
            this.children = new HashMap<Character, TrieNode<V>>(childrenSize);
            this.value = value;
            this.inUse = inUse;
        }

        public TrieNode(final boolean inUse) {
            this(null, inUse);
        }

        public TrieNode<V> unset() {
            inUse = false;
            value = null;
            return this;
        }

        public TrieNode<V> cloneDeep() {
            final TrieNode<V> node = new TrieNode<V>(value, inUse, children.size());
            final Map<Character, TrieNode<V>> nodeChildren = node.children;

            for (final Map.Entry<Character, TrieNode<V>> entry : children.entrySet()) {
                nodeChildren.put(entry.getKey(), entry.getValue().cloneDeep());
            }

            return node;
        }
    }

    /**
     * Adds the given new node to the node with the given key beginning at
     * beginIndex.
     */
    private void addNode(final TrieNode<V> node, final CharSequence key, final int beginIndex, final TrieNode<V> newNode) {
        final int lastKeyIndex = key.length() - 1;
        TrieNode<V> currentNode = node;
        int i = beginIndex;

        for (; i < lastKeyIndex; i++) {
            final TrieNode<V> nextNode = new TrieNode<V>(false);
            currentNode.children.put(key.charAt(i), nextNode);
            currentNode = nextNode;
        }

        currentNode.children.put(key.charAt(i), newNode);
    }

    /**
     * {@inheritDoc}
     */
    public V get(final String key) {
        final TrieNode<V> node = findNode(keyCheck(key));
        return node == null ? null : node.value;
    }

    private TrieNode<V> findNode(final CharSequence key) {
        final int strLen = key.length();
        TrieNode<V> currentNode = root;

        for (int i = 0; i < strLen && currentNode != null; i++) {
            currentNode = currentNode.children.get(key.charAt(i));
        }

        return currentNode;
    }

    private static CharSequence keyCheck(final CharSequence key) {
        if (key == null) {
            throw new IllegalArgumentException("This map does not support null keys");
        }

        return key;
    }
}
