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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blazebit.regex.Pattern;
import com.blazebit.regex.node.CharNode;
import com.blazebit.regex.node.CharRangeNode;
import com.blazebit.regex.node.ComplementNode;
import com.blazebit.regex.node.DotNode;
import com.blazebit.regex.node.EmptyNode;
import com.blazebit.regex.node.Node;
import com.blazebit.regex.node.OptionalNode;
import com.blazebit.regex.node.OrNode;
import com.blazebit.regex.node.RepeatNode;

/**
 * 
 * 
 * @param <V>
 *            The value type that the pattern trie holds.
 * 
 * @author Christian Beikov
 * 
 */
public class PatternTrie<V> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final TrieNode<V> root;
	private final Map<Integer, List<PatternParameter>> patternParameters;
	private int patternIds = 0;

	/**
	 * Constructs an empty PatternTrie
	 */
	public PatternTrie() {
		this.root = new TrieNode<V>();
		this.patternParameters = new HashMap<Integer, List<PatternParameter>>();
	}

	public static interface ParameterizedKeyBuilder<V> {

		ParameterizedKeyBuilder<V> matching(String parameterName, String pattern);

		ParameterizedKeyBuilder<V> matchingNot(String parameterName,
				String pattern);

		void add();
	}

	public static interface ParameterizedValue<V> {

		public V getValue();

		public String getParameter(String patternKey);
		
		public Set<String> getParameterNames();
	}

	public PatternTrie<V> add(final CharSequence key, final V value) {
		if (key == null) {
			throw new NullPointerException("key");
		}

		/* Avoid casting */
		final Map<Parameter, ExtendedPattern> emptyMap = Collections.emptyMap();
		add(key.toString().toCharArray(), value, -1, emptyMap);

		return this;
	}

	public ParameterizedKeyBuilder<V> parameterized(final CharSequence pattern,
			final V value) {
		if (pattern == null) {
			throw new NullPointerException("pattern");
		}

		final char[] chars = pattern.toString().toCharArray();

		if (chars.length > 0) {
			/*
			 * We use a linked map so we don't have to take care of parameter
			 * indices
			 */
			final Map<Parameter, ExtendedPattern> parameters = new LinkedHashMap<Parameter, ExtendedPattern>();
			final ExtendedPattern parameterPattern = new ExtendedPattern(".*",
					false);

			int cursor = 0;
			int parameterStartIndex = -1;

			while (cursor < chars.length) {
				switch (chars[cursor]) {
				case '{':
					if (parameterStartIndex > -1 && cursor != 0
							&& chars[cursor - 1] != '\\') {
						throw new IllegalArgumentException(
								"Unescaped '{' in parameter name at position "
										+ cursor);
					}

					parameterStartIndex = cursor;

					break;
				case '}':
					if (parameterStartIndex < 0 && cursor != 0
							&& chars[cursor - 1] != '\\') {
						throw new IllegalArgumentException(
								"Unescaped '{' found at position " + cursor);
					}

					final String parameterName = new String(chars,
							parameterStartIndex + 1, cursor
									- parameterStartIndex - 1);

					if (parameters.put(new Parameter(parameterName,
							parameterStartIndex), parameterPattern) != null) {
						throw new IllegalArgumentException("Parameter name '"
								+ parameterName
								+ "' is used twice at position "
								+ parameterStartIndex);
					}

					parameterStartIndex = -1;

					break;
				default:
					break;
				}

				cursor++;
			}

			if (parameterStartIndex > -1) {
				throw new IllegalArgumentException(
						"Unclosed bracket at position " + parameterStartIndex);
			}

			return new ParameterizedKeyBuilder<V>() {

				@Override
				public ParameterizedKeyBuilder<V> matchingNot(
						String parameterName, String pattern) {
					if (parameterName == null) {
						throw new NullPointerException("parameterName");
					}

					/* Position is not relevant for equals-hashCode of Parameter */
					if (parameters.put(new Parameter(parameterName, -1),
							new ExtendedPattern(pattern, true)) == null) {
						throw new IllegalArgumentException(
								"Unknown parameter '" + parameterName + "'");
					}

					return this;
				}

				@Override
				public ParameterizedKeyBuilder<V> matching(
						String parameterName, String pattern) {
					if (parameterName == null) {
						throw new NullPointerException("parameterName");
					}

					/* Position is not relevant for equals-hashCode of Parameter */
					if (parameters.put(new Parameter(parameterName, -1),
							new ExtendedPattern(pattern, false)) == null) {
						throw new IllegalArgumentException(
								"Unknown parameter '" + parameterName + "'");
					}

					return this;
				}

				@Override
				public void add() {
					PatternTrie.this
							.add(chars, value, patternIds++, parameters);
				}
			};
		}

		/* Special key build for empty pattern */
		return new ParameterizedKeyBuilder<V>() {

			@Override
			public ParameterizedKeyBuilder<V> matchingNot(String parameterName,
					String pattern) {
				if (parameterName == null) {
					throw new NullPointerException("parameterName");
				}

				throw new IllegalArgumentException("Unknown parameter '"
						+ parameterName + "'");
			}

			@Override
			public ParameterizedKeyBuilder<V> matching(String parameterName,
					String pattern) {
				if (parameterName == null) {
					throw new NullPointerException("parameterName");
				}

				throw new IllegalArgumentException("Unknown parameter '"
						+ parameterName + "'");
			}

			@Override
			public void add() {
				/* Avoid casting */
				final Map<Parameter, ExtendedPattern> emptyMap = Collections
						.emptyMap();
				PatternTrie.this.add(chars, value, -1, emptyMap);
			}
		};
	}

	public Set<ParameterizedValue<V>> resolve(String key) {
		throw new UnsupportedOperationException();
	}

	private static final class PatternParameter {
		private final int patternId;
		private final int parameterIndex;
		private final String name;

		public PatternParameter(int patternId, int parameterIndex, String name) {
			super();
			this.patternId = patternId;
			this.parameterIndex = parameterIndex;
			this.name = name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + parameterIndex;
			result = prime * result + patternId;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PatternParameter other = (PatternParameter) obj;
			if (parameterIndex != other.parameterIndex)
				return false;
			if (patternId != other.patternId)
				return false;
			return true;
		}
	}

	private static final class Parameter {
		private final String name;
		private final int startPosition;

		public Parameter(String name, int startPosition) {
			super();
			this.name = name;
			this.startPosition = startPosition;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Parameter other = (Parameter) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

	}

	private static final class ExtendedPattern {
		private final String pattern;
		private final boolean negated;

		public ExtendedPattern(String pattern, boolean negated) {
			this.pattern = pattern;
			this.negated = negated;
		}

	}

	private static final class ParameterizedValueImpl<V> implements
			ParameterizedValue<V> {

		private V value;
		private final Map<String, String> parameters = new HashMap<String, String>();

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public String getParameter(String patternKey) {
			return parameters.get(patternKey);
		}
		
		public Set<String> getParameterNames(){
			return parameters.keySet();
		}

		public void setParameter(String patternKey, String value) {
			parameters.put(patternKey, value);
		}

	}

	private static final class TrieNode<V> implements Serializable {

		private static final long serialVersionUID = 1L;
		private final Map<Character, TrieNode<V>> children = new HashMap<Character, TrieNode<V>>();
		private final Map<Character, TrieNode<V>> complementChildren = new HashMap<Character, TrieNode<V>>();
		private List<V> value;
		private boolean inUse;
		private TrieNode<V> anyCharChild;
		private final Set<PatternParameter> associatedParameters = new HashSet<PatternParameter>();
		private final Set<PatternParameter> associatedParametersEnd = new HashSet<PatternParameter>();

		public TrieNode(final V value) {
			this.value = new ArrayList<V>();
			this.value.add(value);
			this.inUse = true;
		}

		public TrieNode() {
			this.inUse = false;
		}
	}

	private void add(final char[] pattern, final V value, final int patternId,
			final Map<Parameter, ExtendedPattern> parameters) {
		if (pattern.length == 0) {
			update(root, value);
		}

		final List<Map.Entry<Parameter, ExtendedPattern>> parameterEntries = new ArrayList<Map.Entry<Parameter, ExtendedPattern>>(
				parameters.entrySet());
		int cursor = 0;

		if (parameterEntries.isEmpty()) {
			TrieNode<V> currentNode = root;
			TrieNode<V> lastNode = null;
			/* Simple key */
			while (cursor < pattern.length && currentNode != null) {
				lastNode = currentNode;
				currentNode = currentNode.children.get(pattern[cursor]);

				++cursor;
			}

			if (currentNode == null) {
				cursor--;

				for (; cursor < pattern.length - 1; cursor++) {
					final TrieNode<V> nextNode = new TrieNode<V>();
					lastNode.children.put(pattern[cursor], nextNode);
					lastNode = nextNode;
				}

				lastNode.children.put(pattern[cursor], new TrieNode<V>(value));
			} else {
				update(currentNode, value);
			}
		} else {
			List<TrieNode<V>> currentNodes = new ArrayList<TrieNode<V>>();
			currentNodes.add(root);

			/* Parameterized key */
			int currentParameterIndex = 0;
			Parameter currentParameter = parameterEntries.get(
					currentParameterIndex).getKey();
			patternParameters.put(patternId, new ArrayList<PatternParameter>());
			int currentParameterStart = currentParameter.startPosition;

			for (; cursor < pattern.length - 1; cursor++) {
				if (currentParameterStart == cursor) {
					PatternParameter patternParameter = new PatternParameter(
							patternId, currentParameterIndex,
							currentParameter.name);
					patternParameters.get(patternId).add(patternParameter);
					currentNodes = getOrCreatePatternNodes(currentNodes,
							patternParameter,
							parameterEntries.get(currentParameterIndex)
									.getValue());
					cursor = currentParameterStart
							+ currentParameter.name.length();

					if (++currentParameterIndex < parameterEntries.size()) {
						++cursor;
						currentParameter = parameterEntries.get(
								currentParameterIndex).getKey();
						currentParameterStart = currentParameter.startPosition;
					}
				} else {
					currentNodes = getOrCreate(currentNodes, pattern[cursor],
							null);
				}
			}

			if (cursor == pattern.length - 1) {
				/* Last char in pattern is part of parameter */
				update(currentNodes, value);
			} else {
				/* Last char in pattern is not part of parameter */
				update(getOrCreate(currentNodes, pattern[cursor], null), value);
			}
		}
	}

	private void update(final TrieNode<V> node, final V value) {
		if (node.inUse) {
			node.value.add(value);
		} else {
			node.value = new ArrayList<V>();
			node.value.add(value);
			node.inUse = true;
		}
	}

	private void update(final List<TrieNode<V>> nodes, final V value) {
		for (int i = 0; i < nodes.size(); i++) {
			update(nodes.get(i), value);
		}
	}

	private List<TrieNode<V>> getOrCreate(List<TrieNode<V>> nodes, Character c,
			PatternParameter parameter) {
		List<TrieNode<V>> resultNodes = new ArrayList<TrieNode<V>>(nodes.size());

		for (int i = 0; i < nodes.size(); i++) {
			TrieNode<V> node = nodes.get(i).children.get(c);

			if (node == null) {
				node = new TrieNode<V>();
				nodes.get(i).children.put(c, node);
			}

			resultNodes.add(node);

			if (parameter != null) {
				node.associatedParameters.add(parameter);
			}
		}

		return resultNodes;
	}

	private List<TrieNode<V>> getOrCreateAnyChar(List<TrieNode<V>> nodes,
			PatternParameter parameter) {
		List<TrieNode<V>> resultNodes = new ArrayList<TrieNode<V>>(nodes.size());

		for (int i = 0; i < nodes.size(); i++) {
			TrieNode<V> node = nodes.get(i).anyCharChild;

			if (node == null) {
				node = new TrieNode<V>();
				nodes.get(i).anyCharChild = node;
			}

			resultNodes.add(node);

			if (parameter != null) {
				node.associatedParameters.add(parameter);
			}
		}

		return resultNodes;
	}

	private List<TrieNode<V>> getOrCreateComplement(List<TrieNode<V>> nodes,
			Character c, PatternParameter parameter) {
		List<TrieNode<V>> resultNodes = new ArrayList<TrieNode<V>>(nodes.size());

		for (int i = 0; i < nodes.size(); i++) {
			TrieNode<V> node = nodes.get(i).complementChildren.get(c);

			if (node == null) {
				node = new TrieNode<V>();
				nodes.get(i).complementChildren.put(c, node);
			}

			resultNodes.add(node);

			if (parameter != null) {
				node.associatedParameters.add(parameter);
			}
		}

		return resultNodes;
	}

	private void mergeIntoNodes(List<TrieNode<V>> nodes, TrieNode<V> node) {
		if (nodes.isEmpty()) {
			return;
		}

		for (Map.Entry<Character, TrieNode<V>> tempNodeEntry : node.children
				.entrySet()) {
			List<TrieNode<V>> newTargetNodes = new ArrayList<TrieNode<V>>();

			for (int i = 0; i < nodes.size(); i++) {
				TrieNode<V> targetNode = nodes.get(i);
				TrieNode<V> tempTargetNode = targetNode.children
						.get(tempNodeEntry.getKey());

				if (tempTargetNode == null) {
					targetNode.children.put(tempNodeEntry.getKey(),
							tempNodeEntry.getValue());
				} else {
					newTargetNodes.add(tempTargetNode);
				}
			}

			mergeIntoNodes(newTargetNodes, tempNodeEntry.getValue());
		}

		for (Map.Entry<Character, TrieNode<V>> tempNodeEntry : node.complementChildren
				.entrySet()) {
			List<TrieNode<V>> newTargetNodes = new ArrayList<TrieNode<V>>();

			for (int i = 0; i < nodes.size(); i++) {
				TrieNode<V> targetNode = nodes.get(i);
				TrieNode<V> tempTargetNode = targetNode.complementChildren
						.get(tempNodeEntry.getKey());

				if (tempTargetNode == null) {
					targetNode.complementChildren.put(tempNodeEntry.getKey(),
							tempNodeEntry.getValue());
				} else {
					newTargetNodes.add(tempTargetNode);
				}
			}

			mergeIntoNodes(newTargetNodes, tempNodeEntry.getValue());
		}

		List<TrieNode<V>> newTargetNodes = new ArrayList<TrieNode<V>>();

		for (int i = 0; i < nodes.size(); i++) {
			TrieNode<V> targetNode = nodes.get(i);
			TrieNode<V> tempTargetNode = targetNode.anyCharChild;

			if (tempTargetNode == null) {
				targetNode.anyCharChild = node.anyCharChild;
			} else {
				newTargetNodes.add(tempTargetNode);
			}

			targetNode.associatedParameters.addAll(node.associatedParameters);
			targetNode.associatedParametersEnd
					.addAll(node.associatedParametersEnd);
		}

		if(node.anyCharChild != null){
			mergeIntoNodes(newTargetNodes, node.anyCharChild);
		}
		
		if (node.inUse) {
			for (int i = 0; i < nodes.size(); i++) {
				for (int j = 0; j < node.value.size(); j++) {
					update(nodes.get(i), node.value.get(j));
				}
			}
		}
	}

	private List<TrieNode<V>> getOrCreatePatternNodes(
			List<TrieNode<V>> lastNodes, PatternParameter parameter,
			ExtendedPattern pattern) {
		/*
		 * Build a deterministic automaton, link the lastNode to the automaton
		 * and return the end state
		 */
		TraverseContext<V> context = new TraverseContext<V>(parameter);

		if (pattern.negated) {
			context.complement();
		}

		traverse(Pattern.parse(pattern.pattern), context, lastNodes);

		List<TrieNode<V>> result = new ArrayList<TrieNode<V>>(context
				.getEndStates().size());

		for (TrieNode<V> endState : context.getEndStates()) {
			endState.associatedParametersEnd.add(parameter);
			result.add(endState);
		}

		return result;
	}

	private static final class TraverseContext<V> {
		private boolean hasMoreRequired = false;
		private boolean complement = false;
		private final Set<TrieNode<V>> endStates = new HashSet<TrieNode<V>>();
		private final PatternParameter parameter;

		public TraverseContext(PatternParameter parameter) {
			this.parameter = parameter;
		}

		public void addEndState(TrieNode<V> state) {
			endStates.add(state);
		}

		public Set<TrieNode<V>> getEndStates() {
			return endStates;
		}

		public boolean isComplement() {
			return complement;
		}

		public void complement() {
			this.complement = !this.complement;
		}

		public boolean hasMoreRequired() {
			return hasMoreRequired;
		}

		public void setHasMoreRequired(boolean hasMoreRequired) {
			this.hasMoreRequired = hasMoreRequired;
		}
	}

	private boolean moreRequiredExists(Node node, TraverseContext<V> context) {
		Node nextNode = node.getNext();

		while (nextNode != null) {
			if (!(nextNode instanceof OptionalNode)
					&& (!(nextNode instanceof RepeatNode) || ((RepeatNode) nextNode)
							.getMin() == 0)) {
				return true;
			}

			nextNode = nextNode.getNext();
		}

		return false;
	}

	private List<TrieNode<V>> traverse(Node node, TraverseContext<V> context,
			List<TrieNode<V>> trieNodes) {
		List<TrieNode<V>> newNodes = null;
		boolean moreRequiredSet = false;

		if (!context.hasMoreRequired()) {
			moreRequiredSet = moreRequiredExists(node, context);
			context.setHasMoreRequired(moreRequiredSet);
		}

		if (node instanceof OrNode) {
			OrNode orNode = (OrNode) node;
			List<Node> nodes = orNode.getNodes();
			newNodes = new ArrayList<TrieNode<V>>(trieNodes.size()
					* nodes.size());

			for (int i = 0; i < nodes.size(); i++) {
				newNodes.addAll(traverse(nodes.get(i), context, trieNodes));
			}
		} else if (node instanceof RepeatNode) {
			RepeatNode repeatNode = (RepeatNode) node;
			TrieNode<V> tempNode = new TrieNode<V>();
			List<TrieNode<V>> tempNodeList = new ArrayList<TrieNode<V>>(1);
			tempNodeList.add(tempNode);

			newNodes = traverse(repeatNode.getDecorated(), context,
					tempNodeList);

			if (repeatNode.getMax() != Integer.MAX_VALUE) {
				List<TrieNode<V>> tempNodes = newNodes;
				newNodes = new ArrayList<TrieNode<V>>();

				for (int i = 1; i < repeatNode.getMax(); i++) {
					tempNodes = traverse(repeatNode.getDecorated(), context,
							tempNodes);

					if (i + 1 >= repeatNode.getMin()) {
						newNodes.addAll(tempNodes);
					}
				}
			} else if (repeatNode.getMin() != 0) {
				List<TrieNode<V>> tempNodes = newNodes;
				newNodes = new ArrayList<TrieNode<V>>();

				for (int i = 1; i < repeatNode.getMin() - 1; i++) {
					tempNodes = traverse(repeatNode.getDecorated(), context,
							tempNodes);
				}
				
				if(repeatNode.getMin() != 1){
					TrieNode<V> minFulfilledNode = new TrieNode<V>();
					List<TrieNode<V>> minFulfilledNodeList = new ArrayList<TrieNode<V>>(1);
					minFulfilledNodeList.add(minFulfilledNode);
					
					newNodes = traverse(repeatNode.getDecorated(), context,
							minFulfilledNodeList);
					
					mergeIntoNodes(tempNodes, minFulfilledNode);
					mergeIntoNodes(newNodes, minFulfilledNode);
				} else {
					newNodes = tempNodes;
					mergeIntoNodes(newNodes, tempNode);
				}
			} else {
				mergeIntoNodes(newNodes, tempNode);
			}

			mergeIntoNodes(trieNodes, tempNode);

			if (repeatNode.getMin() == 0) {
				newNodes.addAll(trieNodes);
			}
		} else if (node instanceof OptionalNode) {
			newNodes = traverse(((OptionalNode) node).getDecorated(), context,
					trieNodes);
			newNodes.addAll(trieNodes);
		} else if (node instanceof ComplementNode) {
			context.complement();
			newNodes = traverse(((ComplementNode) node).getDecorated(),
					context, trieNodes);
			context.complement();
		} else if (node instanceof CharRangeNode) {
			newNodes = new ArrayList<TrieNode<V>>(trieNodes.size());
			CharRangeNode rangeNode = (CharRangeNode) node;
			char start = rangeNode.getStart();
			char end = rangeNode.getEnd();

			if (context.isComplement()) {
				for (int i = start; i <= end; i++) {
					newNodes.addAll(getOrCreateComplement(trieNodes, (char) i,
							context.parameter));
				}
			} else {
				for (int i = start; i <= end; i++) {
					newNodes.addAll(getOrCreate(trieNodes, (char) i,
							context.parameter));
				}
			}
		} else if (node instanceof CharNode) {
			if (context.isComplement()) {
				newNodes = getOrCreateComplement(trieNodes,
						((CharNode) node).getCharacter(), context.parameter);
			} else {
				newNodes = getOrCreate(trieNodes,
						((CharNode) node).getCharacter(), context.parameter);
			}
		} else if (node instanceof DotNode) {
			newNodes = getOrCreateAnyChar(trieNodes, context.parameter);
		} else if (node instanceof EmptyNode) {
			throw new IllegalArgumentException("Empty bracket not allowed");
		} else {
			throw new IllegalArgumentException("Unknown node");
		}

		if (newNodes != null) {
			if (!context.hasMoreRequired()) {
				for (int i = 0; i < newNodes.size(); i++) {
					context.addEndState(newNodes.get(i));
				}
			}

			if (moreRequiredSet) {
				context.setHasMoreRequired(false);
			}

			if (node.getNext() != null) {
				newNodes = traverse(node.getNext(), context, newNodes);
			}
		}

		return newNodes;
	}

	public String toString() {
		Map<PatternParameter, Integer> parameterCount = new HashMap<PatternParameter, Integer>();

		for (List<PatternParameter> params : patternParameters.values()) {
			for (PatternParameter param : params) {
				parameterCount.put(param, 0);
			}
		}

		return toString(root, new StringBuilder(), 0, parameterCount, 100)
				.toString();
	}

	private static boolean anyReachedThreshold(
			Map<PatternParameter, Integer> parameterCount,
			Set<PatternParameter> parameters, int charCountThreshold) {
		for (PatternParameter param : parameters) {
			if (parameterCount.get(param) == charCountThreshold) {
				return true;
			}
		}

		return false;
	}

	private StringBuilder toString(TrieNode<V> node, StringBuilder sb,
			int depth, Map<PatternParameter, Integer> parameterCount,
			int charCountThreshold) {
		if (node.inUse) {
			sb.append(" => ");
			sb.append(node.value == null ? "null" : node.value.toString());
			sb.append('\n');

			depth += 2;

			for (int i = 0; i < depth; i++) {
				sb.append(' ');
			}
		}

		for (PatternParameter param : node.associatedParameters) {
			parameterCount.put(param, parameterCount.get(param) + 1);
		}

		if (node.children.size() == 1 && node.complementChildren.size() == 0
				&& node.anyCharChild == null) {
			Map.Entry<Character, TrieNode<V>> entry = node.children.entrySet()
					.iterator().next();
			sb.append('[');
			sb.append(entry.getKey());
			sb.append(']');

			if (anyReachedThreshold(parameterCount,
					entry.getValue().associatedParameters, charCountThreshold)) {
				sb.append("(...)");
			} else {
				toString(entry.getValue(), sb, depth, parameterCount,
						charCountThreshold);
			}
		} else if (node.children.size() == 0
				&& node.complementChildren.size() == 1
				&& node.anyCharChild == null) {
			Map.Entry<Character, TrieNode<V>> entry = node.complementChildren
					.entrySet().iterator().next();
			sb.append('[').append('^');
			sb.append(entry.getKey());
			sb.append(']');

			if (anyReachedThreshold(parameterCount,
					entry.getValue().associatedParameters, charCountThreshold)) {
				sb.append("(...)");
			} else {
				toString(entry.getValue(), sb, depth, parameterCount,
						charCountThreshold);
			}
		} else if (node.children.size() == 0
				&& node.complementChildren.size() == 0
				&& node.anyCharChild != null) {
			sb.append('.');

			if (anyReachedThreshold(parameterCount,
					node.anyCharChild.associatedParameters, charCountThreshold)) {
				sb.append("(...)");
			} else {
				toString(node.anyCharChild, sb, depth, parameterCount,
						charCountThreshold);
			}
		} else {
			depth += 2;

			for (Map.Entry<Character, TrieNode<V>> entry : node.children
					.entrySet()) {
				sb.append('\n');

				for (int i = 0; i < depth; i++) {
					sb.append(' ');
				}

				sb.append('[');
				sb.append(entry.getKey());
				sb.append(']');

				if (anyReachedThreshold(parameterCount,
						entry.getValue().associatedParameters,
						charCountThreshold)) {
					sb.append("(...)");
				} else {
					toString(entry.getValue(), sb, depth, parameterCount,
							charCountThreshold);
				}
			}

			for (Map.Entry<Character, TrieNode<V>> entry : node.complementChildren
					.entrySet()) {
				sb.append('\n');

				for (int i = 0; i < depth; i++) {
					sb.append(' ');
				}

				sb.append('[').append('^');
				sb.append(entry.getKey());
				sb.append(']');

				if (anyReachedThreshold(parameterCount,
						entry.getValue().associatedParameters,
						charCountThreshold)) {
					sb.append("(...)");
				} else {
					toString(entry.getValue(), sb, depth, parameterCount,
							charCountThreshold);
				}
			}

			if (node.anyCharChild != null) {
				sb.append('\n');

				for (int i = 0; i < depth; i++) {
					sb.append(' ');
				}

				sb.append('.');

				if (anyReachedThreshold(parameterCount,
						node.anyCharChild.associatedParameters,
						charCountThreshold)) {
					sb.append("(...)");
				} else {
					toString(node.anyCharChild, sb, depth, parameterCount,
							charCountThreshold);
				}
			}
		}

		return sb;
	}
}
