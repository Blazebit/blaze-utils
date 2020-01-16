package com.blazebit.reflection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertyPathExpressionFieldTest {

    private PropertyPathExpression<Car, String> nameExpression = new PropertyPathExpression<Car, String>(
            Car.class, "name", true);
    private PropertyPathExpression<Car, String> vendorNameExpression = new PropertyPathExpression<Car, String>(
            Car.class, "vendor.name", true);

    @Test
    public void testSetValue() {
        Car o = new Car(null);
        nameExpression.setValue(o, "Test");
        assertEquals(o.name, "Test");
    }

    @Test
    public void testGetValue() {
        Car o;

        o = new Car(null);
        assertEquals(o.name, nameExpression.getValue(o));

        o = new Car("Test");
        assertEquals(o.name, nameExpression.getValue(o));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetValueWithUnresolveableTypeParameters() {
        GenericId<String> genericId = new GenericId<String>("test");

        assertEquals("test",
                new PropertyPathExpression<GenericId<String>, String>(
                        (Class<GenericId<String>>) (Class<?>) GenericId.class,
                        "id", true).getValue(genericId));
    }

    @Test
    public void testGetNullSafeValue() {
        Car o;

        o = new Car(null);
        assertEquals(null, vendorNameExpression.getNullSafeValue(o));

        o = new Car("Test", "Test");
        assertEquals(o.vendor.name,
                vendorNameExpression.getNullSafeValue(o));
    }

    @Test(expected = NullPointerException.class)
    public void testGetValueExpectNPE() {
        vendorNameExpression.getValue(new Car(null));
    }

    @Test(expected = NullPointerException.class)
    public void testSetValueWithNull() {
        vendorNameExpression.setValue(new Car(null), "Test");
    }

    public class GenericId<X> {
        X id;

        public GenericId(X id) {
            this.id = id;
        }
    }

    public class Vendor {
        String name;

        public Vendor(String name) {
            this.name = name;
        }
    }

    public class Car {
        String name;
        Vendor vendor;

        public Car() {

        }

        public Car(String name) {
            this.name = name;
        }

        public Car(String name, String vendorName) {
            this.name = name;
            this.vendor = new Vendor(vendorName);
        }
    }

}
