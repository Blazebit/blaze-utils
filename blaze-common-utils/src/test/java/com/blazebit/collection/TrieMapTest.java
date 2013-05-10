package com.blazebit.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * 
 * @author Christian Beikov
 */
public class TrieMapTest {

	public TrieMap<String> map() {
		TrieMap<String> map = new TrieMap<String>();
		map.put("/page/main", "/main.xhtml");
		map.put("/page/main/home", "/pages/home.xhtml");
		map.put("/page/main/pictures", "/pages/pictures.xhtml");
		map.put("/page/main/projects", "/pages/projects.xhtml");
		map.put("/page/main/projects/triemap", "/pages/projectDetails.xhtml");
		map.put("/page/admin", "/admin.xhtml");
		map.put("/page/sitemap", "/sitemap.xhtml");
		map.put("/file/picture", "/picture.xhtml");
		return map;
	}

	@Test
	public void testPutAndGet() throws Exception {
		TrieMap<String> map = new TrieMap<String>();
		map.put("/page/main", null);

		assertEquals(1, map.size());
		assertNull(map.get("page/main"));

		/* Replace value */
		map.put("/page/main", "test");

		assertEquals(1, map.size());
		assertEquals("test", map.get("/page/main"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullPut() {
		TrieMap<String> map = new TrieMap<String>();
		map.put(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullGet() {
		TrieMap<String> map = new TrieMap<String>();
		map.get(null);
	}

	@Test
	public void testGetBestMatch() throws Exception {
		assertEquals("/page/main/projects/",
				map().getBestMatch("/page/main/projects/ai-utils"));
	}

	@Test
	public void testRemove() throws Exception {
		TrieMap<String> map = map();

		assertEquals(8, map.size());
		map.remove("/page/main");

		assertEquals(7, map.size());
		assertNull(map.get("/page/main"));
		assertFalse(map.containsKey("/page/main"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullRemove() {
		TrieMap<String> map = new TrieMap<String>();
		map.remove(null);
	}

	@Test
	public void testClear() throws Exception {
		TrieMap<String> map = map();

		assertFalse(map.isEmpty());
		map.clear();
		assertTrue(map.isEmpty());
	}

	@Test
	public void testContainsKey() throws Exception {
		TrieMap<String> map = map();

		assertTrue(map.containsKey("/page/main"));
		assertFalse(map.containsKey("something that does not exist"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullContainsKey() {
		TrieMap<String> map = new TrieMap<String>();
		map.containsKey(null);
	}

	@Test
	public void testContainsKeyPrefix() throws Exception {
		assertTrue(map().containsKeyPrefix("/p"));
		assertTrue(map().containsKeyPrefix("/page"));
		assertTrue(map().containsKeyPrefix("/page/"));
		assertTrue(map().containsKeyPrefix("/page/main"));
		assertFalse(map().containsKeyPrefix("something that does not exist"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullContainsKeyPrefix() {
		TrieMap<String> map = new TrieMap<String>();
		map.containsKeyPrefix(null);
	}

	@Test
	public void testContainsValue() throws Exception {
		TrieMap<String> map = map();

		assertTrue(map.containsValue("/main.xhtml"));
		assertFalse(map.containsValue("something that does not exist"));
		assertFalse(map.containsValue(null));
		map.put("null holding node", null);
		assertTrue(map.containsValue(null));
	}

	@Test
	public void testInsertOrder1() throws Exception {
		TrieMap<String> expectedMap1 = new TrieMap<String>();
		expectedMap1.put("/page/main", null);
		expectedMap1.put("/page/main/projects", "/pages/projects.xhtml");
		expectedMap1.put("/page/main/pictures", "/pages/pictures.xhtml");
		expectedMap1.remove("/page/main");

		TrieMap<String> expectedMap2 = new TrieMap<String>();
		expectedMap2.put("/page/main/projects", "/pages/projects.xhtml");
		expectedMap2.put("/page/main", null);
		expectedMap2.put("/page/main/pictures", "/pages/pictures.xhtml");
		expectedMap2.remove("/page/main");

		TrieMap<String> splitMap = new TrieMap<String>();

		splitMap.put("/page/main/projects", "/pages/projects.xhtml");
		splitMap.put("/page/main/pictures", "/pages/pictures.xhtml");

		assertEquals(expectedMap1, splitMap);
		assertEquals(expectedMap2, splitMap);
	}

	@Test
	public void testInsertOrder2() throws Exception {
		TrieMap<String> splitMap = new TrieMap<String>();

		splitMap.put("/page/main/projects/triemap",
				"/pages/projectDetails.xhtml");
		splitMap.put("/page/main/projects", "/pages/projects.xhtml");
		splitMap.put("/page/main/pictures", "/pages/pictures.xhtml");
		splitMap.put("/page/main/home", "/pages/home.xhtml");
		splitMap.put("/page/main", "/main.xhtml");
		splitMap.put("/page/admin", "/admin.xhtml");
		splitMap.put("/page/sitemap", "/sitemap.xhtml");
		splitMap.put("/file/picture", "/picture.xhtml");

		assertEquals(map(), splitMap);
	}

	@Test
	public void testSize() throws Exception {
		assertEquals(8, map().size());
	}

	@Test
	public void testKeySet() throws Exception {
		TrieMap<String> map = new TrieMap<String>();
		Map<CharSequence, String> expectedMap = new HashMap<CharSequence, String>();
		expectedMap.put("/page/main/projects/triemap",
				"/pages/projectDetails.xhtml");
		expectedMap.put("/page/main/projects", "/pages/projects.xhtml");
		map.put("/page/main/projects/triemap", "/pages/projectDetails.xhtml");
		map.put("/page/main/projects", "/pages/projects.xhtml");
		assertEquals(expectedMap.keySet(), map.keySet());
	}

	@Test
	public void testEntrySet() throws Exception {
		TrieMap<String> map = new TrieMap<String>();
		Map<CharSequence, String> expectedMap = new HashMap<CharSequence, String>();
		expectedMap.put("/page/main/projects/triemap",
				"/pages/projectDetails.xhtml");
		expectedMap.put("/page/main/projects", "/pages/projects.xhtml");
		map.put("/page/main/projects/triemap", "/pages/projectDetails.xhtml");
		map.put("/page/main/projects", "/pages/projects.xhtml");
		assertEquals(expectedMap.entrySet(), map.entrySet());
	}

	@Test
	public void testValues() throws Exception {
		TrieMap<String> map = new TrieMap<String>();
		Map<CharSequence, String> expectedMap = new HashMap<CharSequence, String>();
		expectedMap.put("/page/main/projects/triemap",
				"/pages/projectDetails.xhtml");
		expectedMap.put("/page/main/projects", "/pages/projects.xhtml");
		map.put("/page/main/projects/triemap", "/pages/projectDetails.xhtml");
		map.put("/page/main/projects", "/pages/projects.xhtml");
		assertTrue(expectedMap.values().containsAll(map.values()));
	}

	@Test
	public void testSubMapPutAndGet() throws Exception {
		TrieMap<String> map = new TrieMap<String>();
		TrieMap<String> subMap = map.subMap("/page/");

		map.put("/page/main", "main");

		assertEquals(1, map.size());
		assertEquals(1, subMap.size());
		assertNull(map.get("page/main"));
		assertNull(subMap.get("/main"));
		assertEquals("main", map.get("/page/main"));
		assertEquals("main", subMap.get("main"));

		subMap.put("main/sub", "sub");

		assertEquals(2, map.size());
		assertEquals(2, subMap.size());
		assertEquals("sub", map.get("/page/main/sub"));
		assertEquals("sub", subMap.get("main/sub"));

		/* Replace value */
		subMap.put("main", "test");

		assertEquals(2, map.size());
		assertEquals(2, subMap.size());
		assertEquals("test", map.get("/page/main"));
		assertEquals("test", subMap.get("main"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubMapNullPut() {
		map().subMap("/page").put(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubMapNullGet() {
		map().subMap("/page").get(null);
	}

	@Test
	public void testSubMapGetBestMatch() throws Exception {
		assertEquals("/main/projects/",
				map().subMap("/page").getBestMatch("/main/projects/ai-utils"));
	}

	@Test
	public void testSubMapRemove() throws Exception {
		TrieMap<String> map = map();
		TrieMap<String> subMap = map.subMap("/page/");

		assertEquals(8, map.size());
		assertEquals(7, subMap.size());

		map.remove("/page/main");

		assertEquals(7, map.size());
		assertEquals(6, subMap.size());

		assertNull(map.get("/page/main"));
		assertNull(subMap.get("main"));
		assertFalse(map.containsKey("/page/main"));
		assertFalse(subMap.containsKey("main"));

		subMap.remove("main/home");

		assertEquals(6, map.size());
		assertEquals(5, subMap.size());

		assertNull(map.get("/page/main/home"));
		assertNull(subMap.get("main/home"));
		assertFalse(map.containsKey("/page/main/home"));
		assertFalse(subMap.containsKey("main/home"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubMapNullRemove() {
		map().subMap("/page").remove(null);
	}

	@Test
	public void testSubMapClear() throws Exception {
		TrieMap<String> map = map();
		TrieMap<String> subMap = map.subMap("/page/");

		assertFalse(map.isEmpty());
		assertFalse(subMap.isEmpty());

		map.clear();

		assertTrue(map.isEmpty());
		assertTrue(subMap.isEmpty());

		map = map();
		subMap = map.subMap("/page/");

		assertFalse(map.isEmpty());
		assertFalse(subMap.isEmpty());

		subMap.clear();

		assertFalse(map.isEmpty());
		assertEquals(1, map.size());
		assertTrue(subMap.isEmpty());
	}

	@Test
	public void testSubMapContainsKey() throws Exception {
		TrieMap<String> map = map();
		TrieMap<String> subMap = map.subMap("/page/");

		assertTrue(map.containsKey("/page/main"));
		assertFalse(map.containsKey("something that does not exist"));
		assertTrue(subMap.containsKey("main"));
		assertFalse(subMap.containsKey("something that does not exist"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubMapNullContainsKey() {
		map().subMap("/page/").containsKey(null);
	}

	@Test
	public void testSubMapContainsKeyPrefix() throws Exception {
		assertTrue(map().subMap("/").containsKeyPrefix("p"));
		assertTrue(map().subMap("/").containsKeyPrefix("page"));
		assertTrue(map().subMap("/").containsKeyPrefix("page/"));
		assertTrue(map().subMap("/").containsKeyPrefix("page/main"));
		assertFalse(map().subMap("/").containsKeyPrefix(
				"something that does not exist"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubMapNullContainsKeyPrefix() {
		map().subMap("/page/").containsKeyPrefix(null);
	}

	@Test
	public void testSubMapContainsValue() throws Exception {
		TrieMap<String> map = map().subMap("/page/");

		assertTrue(map.containsValue("/main.xhtml"));
		assertFalse(map.containsValue("something that does not exist"));
		assertFalse(map.containsValue(null));
		map.put("null holding node", null);
		assertTrue(map.containsValue(null));
	}

	@Test
	public void testSubMapInsertOrder1() throws Exception {
		TrieMap<String> expectedMap1 = new TrieMap<String>();
		TrieMap<String> expectedSubMap1 = expectedMap1.subMap("/");

		expectedSubMap1.put("page/main", null);
		expectedSubMap1.put("page/main/projects", "/pages/projects.xhtml");
		expectedSubMap1.put("page/main/pictures", "/pages/pictures.xhtml");
		expectedSubMap1.remove("page/main");

		TrieMap<String> expectedMap2 = new TrieMap<String>();
		TrieMap<String> expectedSubMap2 = expectedMap2.subMap("/");

		expectedSubMap2.put("page/main/projects", "/pages/projects.xhtml");
		expectedSubMap2.put("page/main", null);
		expectedSubMap2.put("page/main/pictures", "/pages/pictures.xhtml");
		expectedSubMap2.remove("page/main");

		TrieMap<String> splitMap = new TrieMap<String>();

		splitMap.put("/page/main/projects", "/pages/projects.xhtml");
		splitMap.put("/page/main/pictures", "/pages/pictures.xhtml");

		assertEquals(expectedMap1, splitMap);
		assertEquals(expectedMap2, splitMap);
	}

	@Test
	public void testSubMapInsertOrder2() throws Exception {
		TrieMap<String> splitMap = new TrieMap<String>();
		TrieMap<String> splitSubMap = splitMap.subMap("/");

		splitSubMap.put("page/main/projects/triemap",
				"/pages/projectDetails.xhtml");
		splitSubMap.put("page/main/projects", "/pages/projects.xhtml");
		splitSubMap.put("page/main/pictures", "/pages/pictures.xhtml");
		splitSubMap.put("page/main/home", "/pages/home.xhtml");
		splitSubMap.put("page/main", "/main.xhtml");
		splitSubMap.put("page/admin", "/admin.xhtml");
		splitSubMap.put("page/sitemap", "/sitemap.xhtml");
		splitSubMap.put("file/picture", "/picture.xhtml");

		assertEquals(map(), splitMap);
	}

	@Test
	public void testSubMapSize() throws Exception {
		assertEquals(7, map().subMap("/page").size());
		assertEquals(5, map().subMap("/page").subMap("/main").size());
	}

	@Test
	public void testSubMapKeySet() throws Exception {
		Map<CharSequence, String> expectedMap = new HashMap<CharSequence, String>();
		expectedMap.put("/triemap", "/pages/projectDetails.xhtml");
		expectedMap.put("", "/pages/projects.xhtml");
		assertEquals(expectedMap.keySet(), map().subMap("/page/main/projects")
				.keySet());
	}

	@Test
	public void testSubMapEntrySet() throws Exception {
		Map<CharSequence, String> expectedMap = new HashMap<CharSequence, String>();
		expectedMap.put("/triemap", "/pages/projectDetails.xhtml");
		expectedMap.put("", "/pages/projects.xhtml");
		assertEquals(expectedMap.entrySet(), map()
				.subMap("/page/main/projects").entrySet());
	}

	@Test
	public void testSubMapValues() throws Exception {
		Map<CharSequence, String> expectedMap = new HashMap<CharSequence, String>();
		expectedMap.put("/triemap", "/pages/projectDetails.xhtml");
		expectedMap.put("", "/pages/projects.xhtml");
		assertTrue(expectedMap.values().containsAll(
				map().subMap("/page/main/projects").values()));
	}

	@Test
	public void testSubMap() throws Exception {
		assertEquals(map().subMap("/page/main"),
				map().subMap("/page").subMap("/main"));

		TrieMap<String> originalMap = map();
		int originalSize = originalMap.size();
		TrieMap<String> subMap = originalMap.subMap("/page/main/projects/");

		assertEquals(1, subMap.size());
		assertTrue(subMap.containsKey("triemap"));

		subMap.put("ai-utils", "/pages/aiUtils.xhtml");

		assertEquals(originalSize + 1, originalMap.size());
		assertEquals(2, subMap.size());
		assertTrue(subMap.containsKey("ai-utils"));

		assertEquals("/pages/aiUtils.xhtml",
				originalMap.get("/page/main/projects/ai-utils"));
		assertEquals("/pages/aiUtils.xhtml", subMap.get("ai-utils"));
	}
}
