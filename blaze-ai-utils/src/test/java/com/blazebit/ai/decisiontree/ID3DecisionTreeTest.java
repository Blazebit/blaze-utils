package com.blazebit.ai.decisiontree;

import com.blazebit.ai.decisiontree.impl.*;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;


/**
 * @author Christian Beikov
 */
public class ID3DecisionTreeTest {

    @Ignore
    @Test
    public void testCreate() {
        Map<RestaurantExample, Boolean> results = new LinkedHashMap<RestaurantExample, Boolean>();
        Set<Attribute> attributes = new HashSet<Attribute>();

        attributes.add(Bool.forName("alternate"));
        attributes.add(Bool.forName("bar"));
        attributes.add(Bool.forName("friday"));
        attributes.add(Bool.forName("hungry"));
        attributes.add(Bool.forName("raining"));
        attributes.add(Bool.forName("reservation"));
        attributes.add(Patron.ATTRIBUTE);
        attributes.add(Price.ATTRIBUTE);
        attributes.add(Type.ATTRIBUTE);
        attributes.add(WaitEstimate.ATTRIBUTE);

        results.put(new RestaurantExample(true, false, false, true, Patron.SOME, Price.EXPENSIVE, false, true, Type.FRENCH, WaitEstimate.ZERO_TO_TEN), true);
        results.put(new RestaurantExample(true, false, false, true, Patron.FULL, Price.CHEAP, false, false, Type.THAI, WaitEstimate.THIRTY_TO_SIXTY), false);
        results.put(new RestaurantExample(false, true, false, false, Patron.SOME, Price.CHEAP, false, false, Type.BURGER, WaitEstimate.ZERO_TO_TEN), true);
        results.put(new RestaurantExample(true, false, true, true, Patron.FULL, Price.CHEAP, false, false, Type.THAI, WaitEstimate.TEN_TO_THIRTY), true);
        results.put(new RestaurantExample(true, false, true, false, Patron.FULL, Price.EXPENSIVE, false, true, Type.FRENCH, WaitEstimate.GREATER_THAN_SIXTY), false);
        results.put(new RestaurantExample(false, true, false, true, Patron.SOME, Price.MEDIUM, true, true, Type.ITALIAN, WaitEstimate.ZERO_TO_TEN), true);
        results.put(new RestaurantExample(false, true, false, false, Patron.NONE, Price.CHEAP, true, false, Type.BURGER, WaitEstimate.ZERO_TO_TEN), false);
        results.put(new RestaurantExample(false, false, false, true, Patron.SOME, Price.MEDIUM, true, true, Type.THAI, WaitEstimate.ZERO_TO_TEN), true);
        results.put(new RestaurantExample(false, true, true, false, Patron.FULL, Price.CHEAP, true, false, Type.BURGER, WaitEstimate.GREATER_THAN_SIXTY), false);
        results.put(new RestaurantExample(true, true, true, true, Patron.FULL, Price.EXPENSIVE, false, true, Type.ITALIAN, WaitEstimate.TEN_TO_THIRTY), false);
        results.put(new RestaurantExample(false, false, false, false, Patron.NONE, Price.CHEAP, false, false, Type.THAI, WaitEstimate.ZERO_TO_TEN), false);
        results.put(new RestaurantExample(true, true, true, true, Patron.FULL, Price.CHEAP, false, false, Type.BURGER, WaitEstimate.THIRTY_TO_SIXTY), true);
        DecisionTree<Boolean> tree = new SimpleDecisionTree<Boolean>(attributes, examples(results), new ID3AttributeSelector());

        assertEquals(1, tree.apply(item(new RestaurantExample(null, null, null, null, Patron.NONE, null, null, null, null, null))).size());
        assertFalse(tree.apply(item(new RestaurantExample(null, null, null, null, Patron.NONE, null, null, null, null, null))).iterator().next());

        assertEquals(1, tree.apply(item(new RestaurantExample(null, null, null, null, Patron.SOME, null, null, null, null, null))).size());
        assertTrue(tree.apply(item(new RestaurantExample(null, null, null, null, Patron.SOME, null, null, null, null, null))).iterator().next());

        assertEquals(2, tree.apply(item(new RestaurantExample(null, null, null, false, Patron.FULL, null, null, null, null, null))).size());
        assertFalse(tree.apply(item(new RestaurantExample(null, null, null, false, Patron.FULL, null, null, null, null, null))).iterator().next());
        
        /* Make clear we have both options in this general case */
        assertEquals(2, tree.apply(item(new RestaurantExample(null, null, null, true, Patron.FULL, null, null, null, null, null))).size());
        
        /* Make clear we can not make decisions in this case */
        assertEquals(0, tree.apply(item(new RestaurantExample(null, null, null, true, Patron.FULL, null, null, null, Type.FRENCH, null))).size());

        assertEquals(1, tree.apply(item(new RestaurantExample(null, null, null, true, Patron.FULL, null, null, null, Type.ITALIAN, null))).size());
        assertFalse(tree.apply(item(new RestaurantExample(null, null, null, true, Patron.FULL, null, null, null, Type.ITALIAN, null))).iterator().next());

        assertEquals(1, tree.apply(item(new RestaurantExample(null, null, null, true, Patron.FULL, null, null, null, Type.BURGER, null))).size());
        assertTrue(tree.apply(item(new RestaurantExample(null, null, null, true, Patron.FULL, null, null, null, Type.BURGER, null))).iterator().next());
        
        /* Hungry -> go to restaurant */
        assertEquals(1, tree.apply(item(new RestaurantExample(null, null, true, true, Patron.FULL, null, null, null, Type.THAI, null))).size());
        assertTrue(tree.apply(item(new RestaurantExample(null, null, true, true, Patron.FULL, null, null, null, Type.THAI, null))).iterator().next());
        
        /* Not hungry -> do not go to restaurant */
        assertEquals(1, tree.apply(item(new RestaurantExample(null, null, false, true, Patron.FULL, null, null, null, Type.THAI, null))).size());
        assertFalse(tree.apply(item(new RestaurantExample(null, null, false, true, Patron.FULL, null, null, null, Type.THAI, null))).iterator().next());
        
        /* We can add any additional attributes, since the attributes that are used for making the decision are fulfilled the rest does not affect anything */
        assertEquals(1, tree.apply(item(new RestaurantExample(null, true, false, true, Patron.FULL, null, null, null, Type.THAI, null))).size());
        assertFalse(tree.apply(item(new RestaurantExample(null, true, false, true, Patron.FULL, null, null, null, Type.THAI, null))).iterator().next());
    }

    private static Item item(RestaurantExample v) {
        return new SimpleItem(valueMap(v));
    }

    private static Set<Example<Boolean>> examples(Map<RestaurantExample, Boolean> restaurantExamples) {
        Set<Example<Boolean>> examples = new HashSet<Example<Boolean>>(restaurantExamples.size());

        for (Map.Entry<RestaurantExample, Boolean> entry : restaurantExamples.entrySet()) {
            final RestaurantExample restaurantExample = entry.getKey();
            examples.add(new SimpleExample<Boolean>(valueMap(restaurantExample), entry.getValue()));
        }

        return examples;
    }

    private static Map<Attribute, AttributeValue> valueMap(RestaurantExample v) {
        Map<Attribute, AttributeValue> values = new HashMap<Attribute, AttributeValue>();

        if (v.alternate != null) {
            values.put(Bool.forName("alternate"), Bool.value(v.alternate));
        }

        if (v.bar != null) {
            values.put(Bool.forName("bar"), Bool.value(v.bar));
        }

        if (v.friday != null) {
            values.put(Bool.forName("friday"), Bool.value(v.friday));
        }

        if (v.hungry != null) {
            values.put(Bool.forName("hungry"), Bool.value(v.hungry));
        }

        if (v.raining != null) {
            values.put(Bool.forName("raining"), Bool.value(v.raining));
        }

        if (v.reservation != null) {
            values.put(Bool.forName("reservation"), Bool.value(v.reservation));
        }

        if (v.patron != null) {
            values.put(Patron.ATTRIBUTE, new SimpleAttributeValue(v.patron));
        }

        if (v.price != null) {
            values.put(Price.ATTRIBUTE, new SimpleAttributeValue(v.price));
        }

        if (v.type != null) {
            values.put(Type.ATTRIBUTE, new SimpleAttributeValue(v.type));
        }

        if (v.estimate != null) {
            values.put(WaitEstimate.ATTRIBUTE, new SimpleAttributeValue(v.estimate));
        }

        return values;
    }

    static class RestaurantExample {
        Boolean alternate;
        Boolean bar;
        Boolean friday;
        Boolean hungry;
        Patron patron;
        Price price;
        Boolean raining;
        Boolean reservation;
        Type type;
        WaitEstimate estimate;

        public RestaurantExample(Boolean alternate, Boolean bar, Boolean friday, Boolean hungry, Patron patron, Price price, Boolean raining, Boolean reservation, Type type, WaitEstimate estimate) {
            this.alternate = alternate;
            this.bar = bar;
            this.friday = friday;
            this.hungry = hungry;
            this.patron = patron;
            this.price = price;
            this.raining = raining;
            this.reservation = reservation;
            this.type = type;
            this.estimate = estimate;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + (this.alternate != null ? this.alternate.hashCode() : 0);
            hash = 89 * hash + (this.bar != null ? this.bar.hashCode() : 0);
            hash = 89 * hash + (this.friday != null ? this.friday.hashCode() : 0);
            hash = 89 * hash + (this.hungry != null ? this.hungry.hashCode() : 0);
            hash = 89 * hash + (this.raining != null ? this.raining.hashCode() : 0);
            hash = 89 * hash + (this.reservation != null ? this.reservation.hashCode() : 0);
            hash = 89 * hash + (this.patron != null ? this.patron.hashCode() : 0);
            hash = 89 * hash + (this.price != null ? this.price.hashCode() : 0);
            hash = 89 * hash + (this.type != null ? this.type.hashCode() : 0);
            hash = 89 * hash + (this.estimate != null ? this.estimate.hashCode() : 0);
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
            final RestaurantExample other = (RestaurantExample) obj;
            if (this.alternate != other.alternate && (this.alternate == null || !this.alternate.equals(other.alternate))) {
                return false;
            }
            if (this.bar != other.bar && (this.bar == null || !this.bar.equals(other.bar))) {
                return false;
            }
            if (this.friday != other.friday && (this.friday == null || !this.friday.equals(other.friday))) {
                return false;
            }
            if (this.hungry != other.hungry && (this.hungry == null || !this.hungry.equals(other.hungry))) {
                return false;
            }
            if (this.raining != other.raining && (this.raining == null || !this.raining.equals(other.raining))) {
                return false;
            }
            if (this.reservation != other.reservation && (this.reservation == null || !this.reservation.equals(other.reservation))) {
                return false;
            }
            if (this.patron != other.patron) {
                return false;
            }
            if (this.price != other.price) {
                return false;
            }
            if (this.type != other.type) {
                return false;
            }
            if (this.estimate != other.estimate) {
                return false;
            }
            return true;
        }
    }

    static enum Bool {
        YES,
        NO;

        public static AttributeValue value(Boolean value) {
            if (value == null) {
                return null;
            }

            return new SimpleAttributeValue(value ? YES : NO);
        }

        public static Attribute forName(String name) {
            return new SimpleDiscreteAttribute(name, attributeValues(values()));
        }
    }

    static enum Patron {
        NONE,
        SOME,
        FULL;

        public static final Attribute ATTRIBUTE = new SimpleDiscreteAttribute("patron", attributeValues(values()));
    }

    static enum Price {
        CHEAP,
        MEDIUM,
        EXPENSIVE;

        public static final Attribute ATTRIBUTE = new SimpleDiscreteAttribute("price", attributeValues(values()));
    }

    static enum Type {
        FRENCH,
        THAI,
        BURGER,
        ITALIAN;

        public static final Attribute ATTRIBUTE = new SimpleDiscreteAttribute("type", attributeValues(values()));
    }

    static enum WaitEstimate {
        ZERO_TO_TEN,
        TEN_TO_THIRTY,
        THIRTY_TO_SIXTY,
        GREATER_THAN_SIXTY;

        public static final Attribute ATTRIBUTE = new SimpleDiscreteAttribute("waitEstimate", attributeValues(values()));
    }

    public static Set<AttributeValue> attributeValues(Object[] objects) {
        Set<AttributeValue> values = new HashSet<AttributeValue>(objects.length);

        for (Object o : objects) {
            values.add(new SimpleAttributeValue(o));
        }

        return values;
    }
}
