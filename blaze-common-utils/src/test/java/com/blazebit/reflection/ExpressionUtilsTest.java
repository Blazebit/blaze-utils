package com.blazebit.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ExpressionUtilsTest {

	@Test
	public void testExpressionCaching() {
		assertTrue(ExpressionUtils.getExpression(Car.class, "name") == ExpressionUtils
				.getExpression(Car.class, "name"));
		assertTrue(ExpressionUtils.getExpression(Car.class, "name",
				String.class) == ExpressionUtils.getExpression(Car.class,
				"name", String.class));
	}

	@Test
	public void testGetExpression() {
		assertNotNull(ExpressionUtils.getExpression(Car.class, "name"));
	}

	@Test
	public void testGetExpressionWithValueClass() {
		assertNotNull(ExpressionUtils.getExpression(Car.class, "name",
				String.class));
	}

	/***********************
	 * Set value *
	 ***********************/

	@Test
	public void testSetValue() {
		Car o = new Car(null);
		ExpressionUtils.setValue(o, "name", "Test");
		assertEquals(o.getName(), "Test");
	}

	@Test
	public void testSetValueWithSourceClass() {
		Car o = new Car(null);
		ExpressionUtils.setValue(Car.class, o, "name", "Test");
		assertEquals(o.getName(), "Test");
	}

	/***********************
	 * Normal value access *
	 ***********************/

	@Test
	public void testGetValueHolder() {
		Car o;

		o = new Car(null);
		assertEquals(o.getName(),
				ExpressionUtils.getValueHolder(Car.class, o, "name").getValue());

		o = new Car("Test");
		assertEquals(o.getName(),
				ExpressionUtils.getValueHolder(Car.class, o, "name").getValue());
	}

	@Test
	public void testGetValueHolderWithValueClass() {
		Car o;

		o = new Car(null);
		assertEquals(
				o.getName(),
				ExpressionUtils.getValueHolder(Car.class, o, "name",
						String.class).getValue());

		o = new Car("Test");
		assertEquals(
				o.getName(),
				ExpressionUtils.getValueHolder(Car.class, o, "name",
						String.class).getValue());
	}

	@Test
	public void testGetValueWithSourceClass() {
		Car o;

		o = new Car(null);
		assertEquals(o.getName(),
				ExpressionUtils.getValue(Car.class, o, "name"));

		o = new Car("Test");
		assertEquals(o.getName(),
				ExpressionUtils.getValue(Car.class, o, "name"));
	}

	@Test
	public void testGetValueWithSourceClassAndValueClass() {
		Car o;

		o = new Car(null);
		assertEquals(o.getName(),
				ExpressionUtils.getValue(Car.class, o, "name", String.class));

		o = new Car("Test");
		assertEquals(o.getName(),
				ExpressionUtils.getValue(Car.class, o, "name", String.class));
	}

	@Test
	public void testGetValue() {
		Car o;

		o = new Car(null);
		assertEquals(o.getName(), ExpressionUtils.getValue(o, "name"));

		o = new Car("Test");
		assertEquals(o.getName(), ExpressionUtils.getValue(o, "name"));
	}

	@Test
	public void testGetValueWithValueClass() {
		Car o;

		o = new Car(null);
		assertEquals(o.getName(),
				ExpressionUtils.getValue(o, "name", String.class));

		o = new Car("Test");
		assertEquals(o.getName(),
				ExpressionUtils.getValue(o, "name", String.class));
	}

	/*****************************
	 * Null safe value access *
	 *****************************/

	@Test
	public void testGetNullSafeValueHolder() {
		Car o;

		o = new Car(null);
		assertEquals(null,
				ExpressionUtils.getValueHolder(Car.class, o, "vendor.name")
						.getNullSafeValue());

		o = new Car("Test", "Test");
		assertEquals(o.getVendor().getName(),
				ExpressionUtils.getValueHolder(Car.class, o, "vendor.name")
						.getNullSafeValue());
	}

	@Test
	public void testGetNullSafeValueHolderWithValueClass() {
		Car o;

		o = new Car(null);
		assertEquals(
				null,
				ExpressionUtils.getValueHolder(Car.class, o, "vendor.name",
						String.class).getNullSafeValue());

		o = new Car("Test", "Test");
		assertEquals(
				o.getVendor().getName(),
				ExpressionUtils.getValueHolder(Car.class, o, "vendor.name",
						String.class).getNullSafeValue());
	}

	@Test
	public void testGetNullSafeValueWithSourceClass() {
		Car o;

		o = new Car(null);
		assertEquals(null,
				ExpressionUtils.getNullSafeValue(Car.class, o, "vendor.name"));

		o = new Car("Test", "Test");
		assertEquals(o.getVendor().getName(),
				ExpressionUtils.getNullSafeValue(Car.class, o, "vendor.name"));
	}

	@Test
	public void testGetNullSafeValueWithSourceClassAndValueClass() {
		Car o;

		o = new Car(null);
		assertEquals(null, ExpressionUtils.getNullSafeValue(Car.class, o,
				"vendor.name", String.class));

		o = new Car("Test", "Test");
		assertEquals(o.getVendor().getName(), ExpressionUtils.getNullSafeValue(
				Car.class, o, "vendor.name", String.class));
	}

	@Test
	public void testGetNullSafeValue() {
		Car o;

		o = new Car(null);
		assertEquals(null, ExpressionUtils.getNullSafeValue(o, "vendor.name"));

		o = new Car("Test", "Test");
		assertEquals(o.getVendor().getName(),
				ExpressionUtils.getNullSafeValue(o, "vendor.name"));
	}

	@Test
	public void testGetNullSafeValueWithValueClass() {
		Car o;

		o = new Car(null);
		assertEquals(null, ExpressionUtils.getNullSafeValue(o, "vendor.name",
				String.class));

		o = new Car("Test", "Test");
		assertEquals(o.getVendor().getName(), ExpressionUtils.getNullSafeValue(
				o, "vendor.name", String.class));
	}

	public class Vendor {
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
