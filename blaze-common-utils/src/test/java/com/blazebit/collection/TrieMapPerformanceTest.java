package com.blazebit.collection;

import java.util.Map;
import org.junit.Test;

/**
 *
 * @author Christian Beikov
 */
public final class TrieMapPerformanceTest {

    private static final Object VALUE = new Object();
    private static final int MAX_DEPTH = 4;
    private static final int WARMUP_RUNS = 20;
    private static final int TEST_RUNS = 15;
    
    @Test
    public void testPerformance() throws Exception {
        final int warmup = WARMUP_RUNS;
        final int runs = TEST_RUNS;
        final Map<CharSequence, Object> map = map(new TrieMap<Object>());
        final long sumGet;
        final long sumPut;
        long start;
        
        /* Warmup */
        for (int i = 0; i < warmup; i++) {
            get(map(new TrieMap<Object>()));
        }
        
        /* Test put performance */
        start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            map(new TrieMap<Object>());
        }
        sumPut = System.nanoTime() - start;
        
        /* Test get performance */
        start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            get(map);
        }
        sumGet = System.nanoTime() - start;

        /* Output */
        final int operations = (int) Math.pow(26, MAX_DEPTH);
        System.out.println("TrieMap.put - " + ((sumPut / runs) / 1000000) + " ms for " + operations + " operations. Average per operation - " + ((sumPut / runs) / operations) + " ns");
        System.out.println("TrieMap.get - " + ((sumGet / runs) / 1000000) + " ms for " + operations + " operations. Average per operation - " + ((sumGet / runs) / operations) + " ns");
    }

    private Map<CharSequence, Object> map(final Map<CharSequence, Object> map) {
        fill(map, "", 0, MAX_DEPTH);

        return map;
    }

    private void fill(Map<CharSequence, Object> map, String prefix, int depth, int maxDepth) {
        if (depth == maxDepth) {
            return;
        }

        final Object value = VALUE;
        final StringBuilder sb = new StringBuilder(prefix.length() + 1);
        sb.append(prefix);
        sb.append(' ');

        for (int i = 0; i < 26; i++) {
            sb.setCharAt(prefix.length(), (char) ('a' + i));
            final String newPrefix = sb.toString();
            map.put(newPrefix, value);
            fill(map, newPrefix, depth + 1, maxDepth);
        }
    }

    private void get(Map<CharSequence, Object> map) {
        get(map, "", 0, MAX_DEPTH);
    }

    private void get(Map<CharSequence, Object> map, String prefix, int depth, int maxDepth) {
        if (depth == maxDepth) {
            return;
        }

        final StringBuilder sb = new StringBuilder(prefix.length() + 1);
        sb.append(prefix);
        sb.append(' ');

        for (int i = 0; i < 26; i++) {
            sb.setCharAt(prefix.length(), (char) ('a' + i));
            final String newPrefix = sb.toString();
            map.get(newPrefix);
            get(map, newPrefix, depth + 1, maxDepth);
        }
    }
}
