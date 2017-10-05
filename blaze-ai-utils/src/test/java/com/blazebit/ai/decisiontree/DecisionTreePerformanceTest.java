package com.blazebit.ai.decisiontree;

import com.blazebit.ai.decisiontree.impl.*;
import org.junit.Test;

import java.util.*;

/**
 * @author Christian Beikov
 */
public class DecisionTreePerformanceTest {

    private static final Set<Example<Boolean>> examples;
    private static final Set<Attribute> attributes = new HashSet<Attribute>();
    private static final Item example = item(new OutlookExample(Outlook.OVERCAST, Temperature.HOT, Humidity.HIGH, Wind.STRONG));

    static {
        attributes.add(Outlook.ATTRIBUTE);
        attributes.add(Temperature.ATTRIBUTE);
        attributes.add(Humidity.ATTRIBUTE);
        attributes.add(Wind.ATTRIBUTE);

        final Map<OutlookExample, Boolean> results = new LinkedHashMap<OutlookExample, Boolean>();
        results.put(new OutlookExample(Outlook.SUNNY, Temperature.HOT, Humidity.HIGH, Wind.WEAK), false);
        results.put(new OutlookExample(Outlook.SUNNY, Temperature.HOT, Humidity.HIGH, Wind.STRONG), false);
        results.put(new OutlookExample(Outlook.OVERCAST, Temperature.HOT, Humidity.HIGH, Wind.WEAK), true);
        results.put(new OutlookExample(Outlook.RAIN, Temperature.MILD, Humidity.HIGH, Wind.WEAK), true);
        results.put(new OutlookExample(Outlook.RAIN, Temperature.COOL, Humidity.NORMAL, Wind.WEAK), true);
        results.put(new OutlookExample(Outlook.RAIN, Temperature.COOL, Humidity.NORMAL, Wind.STRONG), false);
        results.put(new OutlookExample(Outlook.OVERCAST, Temperature.COOL, Humidity.NORMAL, Wind.STRONG), true);
        results.put(new OutlookExample(Outlook.SUNNY, Temperature.MILD, Humidity.HIGH, Wind.WEAK), false);
        results.put(new OutlookExample(Outlook.SUNNY, Temperature.COOL, Humidity.NORMAL, Wind.WEAK), true);
        results.put(new OutlookExample(Outlook.RAIN, Temperature.MILD, Humidity.NORMAL, Wind.WEAK), true);
        results.put(new OutlookExample(Outlook.SUNNY, Temperature.MILD, Humidity.NORMAL, Wind.STRONG), true);
        results.put(new OutlookExample(Outlook.OVERCAST, Temperature.MILD, Humidity.HIGH, Wind.STRONG), true);
        results.put(new OutlookExample(Outlook.OVERCAST, Temperature.HOT, Humidity.NORMAL, Wind.WEAK), true);
        results.put(new OutlookExample(Outlook.RAIN, Temperature.MILD, Humidity.HIGH, Wind.STRONG), false);
        examples = examples(results);
    }

    private static final int WARMUP_RUNS = 100000;
    private static final int TEST_RUNS = 1000;

    @Test
    public void testPerformance() throws Exception {
        final int warmup = WARMUP_RUNS;
        final int runs = TEST_RUNS;
        final DecisionTree<Boolean> tree = create();
        final long sumApply;
        final long sumCreate;
        long start;

        /* Warmup */
        for (int i = 0; i < warmup; i++) {
            apply(create());
        }

        /* Test put performance */
        start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            create();
        }
        sumCreate = System.nanoTime() - start;

        /* Test get performance */
        start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            apply(tree);
        }
        sumApply = System.nanoTime() - start;


        /* Output */
        System.out.println("DecisionTree.create - Average time per operation " + (sumCreate / runs) + " ns for " + TEST_RUNS + " operations.");
        System.out.println("DecisionTree.apply - Average time per operation " + (sumApply / runs) + " ns for " + TEST_RUNS + " operations.");
    }

    public DecisionTree<Boolean> create() {
        return new SimpleDecisionTree<Boolean>(attributes, examples, new ID3AttributeSelector());
    }

    public void apply(DecisionTree<Boolean> tree) {
        tree.apply(example);
    }

    private static Item item(OutlookExample v) {
        return new SimpleItem(valueMap(v));
    }

    private static Set<Example<Boolean>> examples(Map<OutlookExample, Boolean> examples) {
        Set<Example<Boolean>> exampleSet = new HashSet<Example<Boolean>>(examples.size());

        for (Map.Entry<OutlookExample, Boolean> entry : examples.entrySet()) {
            final OutlookExample restaurantExample = entry.getKey();
            exampleSet.add(new SimpleExample<Boolean>(valueMap(restaurantExample), entry.getValue()));
        }

        return exampleSet;
    }

    private static Map<Attribute, AttributeValue> valueMap(OutlookExample v) {
        Map<Attribute, AttributeValue> values = new HashMap<Attribute, AttributeValue>();

        if (v.outlook != null) {
            values.put(Outlook.ATTRIBUTE, new SimpleAttributeValue(v.outlook));
        }

        if (v.temperature != null) {
            values.put(Temperature.ATTRIBUTE, new SimpleAttributeValue(v.temperature));
        }

        if (v.humidity != null) {
            values.put(Humidity.ATTRIBUTE, new SimpleAttributeValue(v.humidity));
        }

        if (v.wind != null) {
            values.put(Wind.ATTRIBUTE, new SimpleAttributeValue(v.wind));
        }

        return values;
    }

    static class OutlookExample {

        Outlook outlook;
        Temperature temperature;
        Humidity humidity;
        Wind wind;

        public OutlookExample(Outlook outlook, Temperature temperature, Humidity humidity, Wind wind) {
            this.outlook = outlook;
            this.temperature = temperature;
            this.humidity = humidity;
            this.wind = wind;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + (this.outlook != null ? this.outlook.hashCode() : 0);
            hash = 67 * hash + (this.temperature != null ? this.temperature.hashCode() : 0);
            hash = 67 * hash + (this.humidity != null ? this.humidity.hashCode() : 0);
            hash = 67 * hash + (this.wind != null ? this.wind.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final OutlookExample other = (OutlookExample) obj;
            if (this.outlook != other.outlook) {
                return false;
            }
            if (this.temperature != other.temperature) {
                return false;
            }
            if (this.humidity != other.humidity) {
                return false;
            }
            if (this.wind != other.wind) {
                return false;
            }
            return true;
        }
    }

    static enum Outlook {

        SUNNY,
        OVERCAST,
        RAIN;
        public static final Attribute ATTRIBUTE = new SimpleDiscreteAttribute("outlook", attributeValues(values()));
    }

    static enum Temperature {

        HOT,
        MILD,
        COOL;
        public static final Attribute ATTRIBUTE = new SimpleDiscreteAttribute("temperature", attributeValues(values()));
    }

    static enum Humidity {

        HIGH,
        NORMAL;
        public static final Attribute ATTRIBUTE = new SimpleDiscreteAttribute("humidity", attributeValues(values()));
    }

    static enum Wind {

        WEAK,
        STRONG;
        public static final Attribute ATTRIBUTE = new SimpleDiscreteAttribute("wind", attributeValues(values()));
    }

    public static Set<AttributeValue> attributeValues(Object[] objects) {
        Set<AttributeValue> values = new HashSet<AttributeValue>(objects.length);

        for (Object o : objects) {
            values.add(new SimpleAttributeValue(o));
        }

        return values;
    }
}
