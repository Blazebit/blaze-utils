package com.blazebit.ai.decisiontree;

import com.blazebit.ai.decisiontree.impl.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Christian Beikov
 */
public class SimpleDecisionTreeTest {

    @Test
    public void testCreate() {
        Set<Vehicle> results = new HashSet<Vehicle>();
        Set<Attribute> attributes = new HashSet<Attribute>();
        Vehicle audi = new Vehicle("A3", Brand.AUDI, Type.CAR);
        Vehicle bmw = new Vehicle("M3", Brand.BMW, Type.CAR);

        attributes.add(TestAttributes.BRAND);
        attributes.add(TestAttributes.TYPE);
        results.add(bmw);
        results.add(audi);
        DecisionTree<Vehicle> tree = new SimpleDecisionTree<Vehicle>(attributes, examples(results), new SimpleAttributeSelector());

        assertEquals(2, tree.apply(item(new Vehicle(null, null, Type.CAR))).size());
        assertTrue(results.containsAll(tree.apply(item(new Vehicle(null, null, Type.CAR)))));

        assertEquals(1, tree.apply(item(new Vehicle(null, Brand.AUDI, null))).size());
        assertTrue(tree.apply(item(new Vehicle(null, Brand.AUDI, null))).contains(audi));

        assertEquals(1, tree.apply(item(new Vehicle(null, Brand.BMW, null))).size());
        assertTrue(tree.apply(item(new Vehicle(null, Brand.BMW, null))).contains(bmw));
    }

    private static Item item(Vehicle v) {
        Map<Attribute, AttributeValue> values = new HashMap<Attribute, AttributeValue>();

        if (v.brand != null) {
            values.put(TestAttributes.BRAND, new SimpleAttributeValue(v.brand));
        }

        if (v.type != null) {
            values.put(TestAttributes.TYPE, new SimpleAttributeValue(v.type));
        }

        return new SimpleItem(values);
    }

    private static Set<Example<Vehicle>> examples(Collection<Vehicle> vehicles) {
        Set<Example<Vehicle>> examples = new HashSet<Example<Vehicle>>(vehicles.size());

        for (Vehicle v : vehicles) {
            Map<Attribute, AttributeValue> values = new HashMap<Attribute, AttributeValue>();

            if (v.brand != null) {
                values.put(TestAttributes.BRAND, new SimpleAttributeValue(v.brand));
            }

            if (v.type != null) {
                values.put(TestAttributes.TYPE, new SimpleAttributeValue(v.type));
            }

            examples.add(new SimpleExample<Vehicle>(values, v));
        }

        return examples;
    }

    static class Vehicle {
        String name;
        Brand brand;
        Type type;

        public Vehicle(String name, Brand brand, Type type) {
            this.name = name;
            this.brand = brand;
            this.type = type;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 37 * hash + (this.brand != null ? this.brand.hashCode() : 0);
            hash = 37 * hash + (this.type != null ? this.type.hashCode() : 0);
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
            final Vehicle other = (Vehicle) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            if (this.brand != other.brand) {
                return false;
            }
            if (this.type != other.type) {
                return false;
            }
            return true;
        }
    }

    static enum Type {
        CAR,
        MOTORCYCLE
    }

    static enum Brand {
        AUDI,
        MERCEDES,
        BMW,
        SUZUKI
    }

    static class TestAttributes {
        //public static final Attribute NAME = new SimpleContinuousAttribute("name");
        public static final Attribute BRAND = new SimpleDiscreteAttribute("brand", values(Brand.values()));
        public static final Attribute TYPE = new SimpleDiscreteAttribute("type", values(Type.values()));

        public static Set<AttributeValue> values(Object[] objects) {
            Set<AttributeValue> values = new HashSet<AttributeValue>(objects.length);

            for (Object o : objects) {
                values.add(new SimpleAttributeValue(o));
            }

            return values;
        }
    }
}
