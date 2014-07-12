package com.blazebit.collection;

import com.blazebit.collection.PatternTrie.ParameterizedValue;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * 
 * @author Christian Beikov
 */
public class PatternTrieTest {

	private static final boolean SKIP_RESOLVE = false;

	// @Test(expected=StackOverflowError.class)
	// public void testSimilarParameterPattern(){
	// PatternTrie<String> trie = new PatternTrie<String>();
	//
	// trie.parameterized("/{param}", "/aStart.xhtml")
	// .matching("param", "a*")
	// .add();
	//
	// trie.parameterized("/{param}", "/aPlus.xhtml")
	// .matching("param", "a+")
	// .add();
	// }
	//
	// @Test
	// public void testStatic() throws Exception {
	// PatternTrie<String> trie = new PatternTrie<String>();
	// trie.add("/dashboard", "/dashboard.xhtml");
	// assertTrieContains(trie, "/dashboard", 1, "/dashboard.xhtml");
	// }
	//
	// @Test
	// public void testOr() throws Exception {
	// PatternTrie<String> trie = new PatternTrie<String>();
	//
	// trie.parameterized("/{page}", "/page.xhtml")
	// .matching("page", "index|about|impressum")
	// .add();
	// assertTrieContains(trie, "/index", 1, "/page.xhtml", "page", "index");
	// }
	//
	// @Test
	// public void testDot() throws Exception {
	// PatternTrie<String> trie = new PatternTrie<String>();
	//
	// trie.parameterized("/{bla}", "/bla.xhtml")
	// .matching("bla", ".")
	// .add();
	// assertTrieContains(trie, "/i", 1, "/bla.xhtml", "bla", "i");
	// }

	@Test
	public void testNegation() throws Exception {
		PatternTrie<String> trie = new PatternTrie<String>();

		trie.parameterized("/{negationTest}", "/negationTest.xhtml")
				.matchingNot("negationTest", "admin").add();
		/* Fix me, this will probably fail because of a bug in pattern */
		// assertTrieContainsNot(trie, "/admin", 0, "/negationTest.xhtml");
		// assertTrieContains(trie, "/admire", 1, "/negationTest.xhtml",
		// "negationTest", "admire");
		// assertTrieContains(trie, "/foo", 1, "/negationTest.xhtml",
		// "negationTest", "foo");
	}

	//
	// @Test
	// public void testOptional() throws Exception {
	// PatternTrie<String> trie = new PatternTrie<String>();
	//
	// trie.parameterized("/{negationCharClassTest}",
	// "/negationCharClassTest.xhtml")
	// .matching("negationCharClassTest", "[^abc]?d")
	// .add();
	// assertTrieContainsNot(trie, "/ad", 1, "/negationCharClassTest.xhtml");
	// assertTrieContains(trie, "/d", 1, "/negationCharClassTest.xhtml",
	// "negationCharClassTest", "d");
	// assertTrieContains(trie, "/dd", 1, "/negationCharClassTest.xhtml",
	// "negationCharClassTest", "dd");
	// }
	//
	// @Test
	// public void testRepeatOptional() throws Exception {
	// PatternTrie<String> trie = new PatternTrie<String>();
	//
	// trie.parameterized("/{repeatOptionalTest}", "/repeatOptionalTest.xhtml")
	// .matching("repeatOptionalTest", "a*d")
	// .add();
	// assertTrieContains(trie, "/d", 1, "/repeatOptionalTest.xhtml",
	// "repeatOptionalTest", "d");
	// assertTrieContains(trie, "/ad", 1, "/repeatOptionalTest.xhtml",
	// "repeatOptionalTest", "ad");
	// assertTrieContains(trie, "/aad", 1, "/repeatOptionalTest.xhtml",
	// "repeatOptionalTest", "aad");
	// }
	//
	// @Test
	// public void testRepeatTwo() throws Exception {
	// PatternTrie<String> trie = new PatternTrie<String>();
	//
	// trie.add("/a", "/a.xhtml");
	// trie.parameterized("/{repeatTwoTest}", "/repeatTwoTest.xhtml")
	// .matching("repeatTwoTest", "a{2}d")
	// .add();
	// assertTrieContains(trie, "/aad", 1, "/repeatTwoTest.xhtml",
	// "repeatTwoTest", "aad");
	// assertTrieContainsNot(trie, "/ad", 0, null);
	// assertTrieContainsNot(trie, "/aaad", 0, null);
	// }
	//
	// @Test
	// public void testRepeatTwoOrMore() throws Exception {
	// PatternTrie<String> trie = new PatternTrie<String>();
	//
	// trie.parameterized("/{repeatTwoOrMoreTest}",
	// "/repeatTwoOrMoreTest.xhtml")
	// .matching("repeatTwoOrMoreTest", "b{2,}d")
	// .add();
	// assertTrieContainsNot(trie, "/bd", 0, "/repeatTwoOrMoreTest.xhtml");
	// assertTrieContains(trie, "/bbd", 1, "/repeatTwoOrMoreTest.xhtml",
	// "repeatTwoOrMoreTest", "bbd");
	// assertTrieContains(trie, "/bbbd", 1, "/repeatTwoOrMoreTest.xhtml",
	// "repeatTwoOrMoreTest", "bbbd");
	// }
	//
	// @Test
	// public void testRepeatTwoToFour() throws Exception {
	// PatternTrie<String> trie = new PatternTrie<String>();
	// trie.parameterized("/{repeatTwoToFourTest}",
	// "/repeatTwoToFourTest.xhtml")
	// .matching("repeatTwoToFourTest", "c{2,4}d")
	// .add();
	// assertTrieContainsNot(trie, "/cd", 0, "/repeatTwoToFourTest.xhtml");
	// assertTrieContains(trie, "/ccd", 1, "/repeatTwoToFourTest.xhtml",
	// "repeatTwoToFourTest", "ccd");
	// assertTrieContains(trie, "/cccd", 1, "/repeatTwoToFourTest.xhtml",
	// "repeatTwoToFourTest", "cccd");
	// assertTrieContains(trie, "/ccccd", 1, "/repeatTwoToFourTest.xhtml",
	// "repeatTwoToFourTest", "ccccd");
	// assertTrieContainsNot(trie, "/cccccd", 0, "/repeatTwoToFourTest.xhtml");
	// }
	//
	// @Test
	// public void testMultipleParameters() throws Exception {
	// PatternTrie<String> trie = new PatternTrie<String>();
	// trie.add("/page/main", "/main.xhtml");
	// trie.add("/page/main/home", "/pages/home.xhtml");
	// trie.add("/page/main/pictures", "/pages/pictures.xhtml");
	// trie.add("/page/main/projects", "/pages/projects.xhtml");
	// trie.add("/page/main/projects/hot", "/pages/hotProjects.xhtml");
	// trie.add("/page/admin", "/admin.xhtml");
	// trie.add("/page/sitemap", "/sitemap.xhtml");
	// trie.add("/file/picture", "/picture.xhtml");
	//
	// trie.parameterized("/page/main/projects/{projectName}",
	// "/pages/projectDetails.xhtml")
	// .matching("projectName", "[a-zA-Z\\-]+")
	// .add();
	//
	// trie.parameterized("/page/main/{pageName}", "/pages/indexPage.xhtml")
	// .matching("pageName", "(users|articles|gallery)")
	// .add();
	//
	// trie.parameterized("/page/main/{pageName}/{pageNumber}",
	// "/pages/indexPage.xhtml")
	// .matching("pageName", "(users|articles|gallery)")
	// .matching("pageNumber", "[1-9][0-9]*")
	// .add();
	//
	// trie.parameterized("/page/main/{pageName}", "/pages/invalidModule.xhtml")
	// .matchingNot("pageName", "(users|articles|gallery)")
	// .add();
	//
	// trie.parameterized("/page/main/{pageName}/{pageNumber}",
	// "/pages/invalidModule.xhtml")
	// .matchingNot("pageName", "(users|articles|gallery)")
	// .matching("pageNumber", "[1-9][0-9]*")
	// .add();
	//
	// /* Resolve a static entry */
	// assertTrieContains(trie, "/page/main", 1, "/main.xhtml");
	//
	// /* Resolve entries which applies to a static key and a pattern key, the
	// set is ordered and static entries must come first */
	// assertTrieContains(trie, "/page/main/home", 2, "/pages/home.xhtml");
	// assertTrieContains(trie, "/page/main/home", 2,
	// "/pages/invalidModule.xhtml", "pageName", "home");
	//
	// /* Resolve an entry and extract parameter values */
	// // assertTrieContains(trie, "/page/main/home/1", 1,
	// "/pages/invalidModule.xhtml", "pageName", "home", "pageNumber", "1");
	// }

	private void assertTrieContains(PatternTrie<String> trie, String test,
			int size, String expectedValue, String... keyValues) {
		assertTrie(trie, test, size, expectedValue, true, keyValues);
	}

	private void assertTrieContainsNot(PatternTrie<String> trie, String test,
			int size, String expectedValue, String... keyValues) {
		assertTrie(trie, test, size, expectedValue, false, keyValues);
	}

	private void assertTrie(PatternTrie<String> trie, String test, int size,
			String expectedValue, boolean checkTrue, String... keyValues) {
		if (SKIP_RESOLVE) {
			return;
		}

		List<ParameterizedValue<String>> values = new ArrayList<ParameterizedValue<String>>(
				trie.resolve(test));
		boolean contains = false;

		assertEquals(size, values.size());

		for (ParameterizedValue<String> value : values) {
			boolean containsNot = false;

			if (expectedValue.equals(value.getValue())) {
				if (keyValues.length / 2 != value.getParameterNames().size()) {
					containsNot = true;
					break;
				} else {
					for (int i = 0; i < keyValues.length; i += 2) {
						if (!keyValues[i + 1].equals(value
								.getParameter(keyValues[i]))) {
							containsNot = true;
							break;
						}
					}
				}
			} else {
				continue;
			}

			if (!containsNot) {
				contains = true;
				break;
			}
		}

		if (checkTrue) {
			assertTrue(contains);
		} else {
			assertFalse(contains);
		}
	}
}
