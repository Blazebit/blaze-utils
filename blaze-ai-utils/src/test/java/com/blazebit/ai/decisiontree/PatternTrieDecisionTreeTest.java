package com.blazebit.ai.decisiontree;

import com.blazebit.ai.decisiontree.impl.ID3AttributeSelector;
import com.blazebit.ai.decisiontree.impl.SimpleAttributeSelector;
import com.blazebit.ai.decisiontree.impl.SimpleAttributeValue;
import static org.junit.Assert.*;

import com.blazebit.ai.decisiontree.impl.NoopDecisionTree;
import com.blazebit.ai.decisiontree.impl.SimpleDiscreteAttribute;
import com.blazebit.ai.decisiontree.impl.SimpleDecisionTree;
import com.blazebit.ai.decisiontree.impl.SimpleExample;
import com.blazebit.ai.decisiontree.impl.SimpleItem;
import com.blazebit.collection.PatternTrie;
import com.blazebit.collection.PatternTrie.ParameterizedValue;
import com.blazebit.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;


/**
 *
 * @author Christian Beikov
 */
public class PatternTrieDecisionTreeTest {
    
	interface Action { String getValue(Object... params); }
	class PathAction implements Action {
		final String value;
		
		public PathAction(String value) { this.value = value; }
		public String getValue(Object... params){
			if(params == null){
				return value;
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(value);
			
			for(Object param : params) {
				sb.append('/').append(param);
			}
			
			return sb.toString();
		}
	}
	
    @Test
    public void testCreate() {
    	/* Split conditions like when(condition1).and(condition2).or(condition3) into
    	 * two examples, one is when(condition1).and(condition2) and the second is
    	 * when(condition3). This needs to be done since decision tree can only handle 
    	 */
    	
    	PatternTrie<DecisionTree<Action>> trie = new PatternTrie<DecisionTree<Action>>();
    	Set<ParameterizedValue<DecisionTree<Action>>> results;
    	ParameterizedValue<DecisionTree<Action>> parameterizedResult;
    	DecisionTree<Action> decisionTreeResult;
    	Action actionResult;
    	
    	Set<Attribute> attributes;
    	Set<AttributeValue> attributeValues;
    	Set<Example<Action>> examples;
    	Map<Attribute, AttributeValue> values;
    	
    	/* Simple test */
    	trie.add("/home", new NoopDecisionTree<Action>(new PathAction("/home")));
    	
    	results = trie.resolve("/home");
    	assertEquals(1, results.size());
    	parameterizedResult = results.iterator().next();
    	assertEquals(0, parameterizedResult.getParameterNames().size());
    	decisionTreeResult = parameterizedResult.getValue();
    	assertNotNull(decisionTreeResult);
    	actionResult = decisionTreeResult.applySingle(null);
    	assertNotNull(actionResult);
    	assertEquals("/home", actionResult.getValue());
    	
    	
    	/* Another simple test but with possible conflict with other rule */
    	trie.add("/projects", new NoopDecisionTree<Action>(new PathAction("/projects")));
    	
    	results = trie.resolve("/projects");
    	assertEquals(1, results.size());
    	parameterizedResult = results.iterator().next();
    	assertEquals(0, parameterizedResult.getParameterNames().size());
    	decisionTreeResult = parameterizedResult.getValue();
    	assertNotNull(decisionTreeResult);
    	actionResult = decisionTreeResult.applySingle(null);
    	assertNotNull(actionResult);
    	assertEquals("/projects", actionResult.getValue());
    	
    	
    	/* Another simple test */
    	trie.add("/about", new NoopDecisionTree<Action>(new PathAction("/about")));
    	
    	results = trie.resolve("/about");
    	assertEquals(1, results.size());
    	parameterizedResult = results.iterator().next();
    	assertEquals(0, parameterizedResult.getParameterNames().size());
    	decisionTreeResult = parameterizedResult.getValue();
    	assertNotNull(decisionTreeResult);
    	actionResult = decisionTreeResult.applySingle(null);
    	assertNotNull(actionResult);
    	assertEquals("/about", actionResult.getValue());
    	

    	/* Trie only test */
    	trie.parameterized("/project/{projectName}", new NoopDecisionTree<Action>(new PathAction("/project")))
    	.matching("projectName", ".+")
    	.add();
    	
    	results = trie.resolve("/project/my-first-project");
    	assertEquals(1, results.size());
    	parameterizedResult = results.iterator().next();
    	assertEquals(1, parameterizedResult.getParameterNames().size());
    	decisionTreeResult = parameterizedResult.getValue();
    	assertNotNull(decisionTreeResult);
    	actionResult = decisionTreeResult.applySingle(null);
    	assertNotNull(actionResult);
    	assertEquals("/project/my-first-project", actionResult.getValue(parameterizedResult.getParameter("projectName")));

    	
    	/* Trie plus decision tree test */
    	attributes = new HashSet<Attribute>();
    	attributeValues = new HashSet<AttributeValue>();
    	attributeValues.add(new SimpleAttributeValue("http"));
    	attributeValues.add(new SimpleAttributeValue("https"));
    	attributes.add(new SimpleDiscreteAttribute("protocol", attributeValues));
    	examples = new HashSet<Example<Action>>();
    	values = new HashMap<Attribute, AttributeValue>();
    	values.put(attributes.iterator().next(), new SimpleAttributeValue("https"));
    	examples.add(new SimpleExample<Action>(values, new PathAction("/news")));
    	decisionTreeResult = new SimpleDecisionTree<Action>(attributes, examples, new SimpleAttributeSelector<Action>());
    	trie.parameterized("/news/{newsTitle}", decisionTreeResult)
    	.matching("newsTitle", ".+")
    	.add();
    	
    	results = trie.resolve("/news/my-first-entry");
    	assertEquals(1, results.size());
    	parameterizedResult = results.iterator().next();
    	assertEquals(1, parameterizedResult.getParameterNames().size());
    	decisionTreeResult = parameterizedResult.getValue();
    	assertNotNull(decisionTreeResult);
    	actionResult = decisionTreeResult.applySingle(new SimpleItem(values));
    	assertNotNull(actionResult);
    	assertEquals("/news/my-first-entry", actionResult.getValue(parameterizedResult.getParameter("newsTitle")));
    	
    	
    }
}
