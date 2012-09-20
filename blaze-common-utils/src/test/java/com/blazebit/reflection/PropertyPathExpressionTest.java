package com.blazebit.reflection;

import static org.junit.Assert.*;

import org.junit.Test;

public class PropertyPathExpressionTest {

	private PropertyPathExpression<Car, String> nameExpression = new PropertyPathExpression<Car, String>(Car.class, "name");
	private PropertyPathExpression<Car, String> vendorNameExpression = new PropertyPathExpression<Car, String>(Car.class, "vendor.name");
	
	@Test
	public void testSetValue() {
		Car o = new Car(null);
		nameExpression.setValue(o, "Test");
		assertEquals(o.getName(), "Test");
	}
	
	@Test
	public void testGetValue() {
		Car o;
		
		o = new Car(null);
		assertEquals(o.getName(), nameExpression.getValue(o));
		
		o = new Car("Test");
		assertEquals(o.getName(), nameExpression.getValue(o));
	}
	
	@Test
	public void testGetNullSafeValue() {
		Car o;
		
		o = new Car(null);
		assertEquals(null, vendorNameExpression.getNullSafeValue(o));

		o = new Car("Test", "Test");
		assertEquals(o.getVendor().getName(), vendorNameExpression.getNullSafeValue(o));
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetValueExpectNPE() {
		vendorNameExpression.getValue(new Car(null));
	}

	@Test(expected=NullPointerException.class)
	public void testSetValueWithNull() {
		vendorNameExpression.setValue(new Car(null), "Test");
	}
	
	public class Vendor{
		String name;

		public Vendor(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	public class Car{
		String name;
		Vendor vendor;

		public Car(){
			
		}
		
		public Car(String name) {
			this.name = name;
		}
		
		public Car(String name, String vendorName) {
			this.name = name;
			this.vendor = new Vendor(vendorName);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Vendor getVendor() {
			return vendor;
		}

		public void setVendor(Vendor vendor) {
			this.vendor = vendor;
		}
	}

}
