package com.blazebit.validation.constraint;

import static com.blazebit.validation.util.ConstraintValidatorUtils.containsViolation;
import static com.blazebit.validation.util.ConstraintValidatorUtils.getValidator;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import org.junit.Before;
import org.junit.Test;

public class CheckEitherTest {

	private Validator validator;

	@Before
	public void before() {
		validator = getValidator();
	}

	@Test
	public void testSimpleDefaultAndCompositeValidation() {
		Set<ConstraintViolation<SimpleCar>> violations;
		SimpleCar o;

		o = new SimpleCarPopulateNone();
		violations = validator.validate(o);
		assertTrue(violations.size() == 2);
		assertTrue(containsViolation(violations, "name"));

		o = new SimpleCarPopulateFirst();
		violations = validator.validate(o);
		assertTrue(violations.size() == 3);
		assertTrue(containsViolation(violations, "name"));
		assertTrue(containsViolation(violations, "brand"));

		o = new SimpleCarPopulateLast();
		violations = validator.validate(o);
		assertTrue(violations.size() == 3);
		assertTrue(containsViolation(violations, "name"));
		assertTrue(containsViolation(violations, "builder"));

		o = new SimpleCarPopulateAll();
		violations = validator.validate(o);
		assertTrue(violations.size() == 4);
		assertTrue(containsViolation(violations, "name"));
		assertTrue(containsViolation(violations, "brand"));
		assertTrue(containsViolation(violations, "builder"));
	}

	@Test
	public void testSimpleCompositeValidation() {
		Set<ConstraintViolation<SimpleCar>> violations;
		SimpleCar o;

		o = new SimpleCarPopulateNone();
		o.name = "";
		violations = validator.validate(o);
		assertTrue(violations.size() == 1);

		o = new SimpleCarPopulateFirst();
		o.name = "";
		violations = validator.validate(o);
		assertTrue(violations.size() == 2);
		assertTrue(containsViolation(violations, "brand"));

		o = new SimpleCarPopulateLast();
		o.name = "";
		violations = validator.validate(o);
		assertTrue(violations.size() == 2);
		assertTrue(containsViolation(violations, "builder"));

		o = new SimpleCarPopulateAll();
		o.name = "";
		violations = validator.validate(o);
		assertTrue(violations.size() == 3);
		assertTrue(containsViolation(violations, "brand"));
		assertTrue(containsViolation(violations, "builder"));

		o = new SimpleCarPopulateNone();
		o.name = "";
		o.brand = "";
		o.builder = "";
		violations = validator.validate(o);
		assertTrue(violations.size() == 1);

		o = new SimpleCarPopulateFirst();
		o.name = "";
		o.brand = "";
		o.builder = "";
		violations = validator.validate(o);
		assertTrue(violations.size() == 2);
		/* Builder must be null according to the validation rule */
		assertTrue(containsViolation(violations, "builder"));

		o = new SimpleCarPopulateLast();
		o.name = "";
		o.brand = "";
		o.builder = "";
		violations = validator.validate(o);
		assertTrue(violations.size() == 2);
		/* Brand must be null according to the validation rule */
		assertTrue(containsViolation(violations, "brand"));

		o = new SimpleCarPopulateAll();
		o.name = "";
		o.brand = "";
		o.builder = "";
		violations = validator.validate(o);
		assertTrue(violations.size() == 3);
		assertTrue(containsViolation(violations, "brand"));
		assertTrue(containsViolation(violations, "builder"));
	}

	@Test
	public void testSimpleSuccessfulValidation() {
		Set<ConstraintViolation<SimpleCar>> violations;
		SimpleCar o;

		o = new SimpleCarPopulateNone();
		o.name = "";
		o.brand = "";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new SimpleCarPopulateFirst();
		o.name = "";
		o.brand = "";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new SimpleCarPopulateLast();
		o.name = "";
		o.brand = "";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new SimpleCarPopulateAll();
		o.name = "";
		o.brand = "";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new SimpleCarPopulateNone();
		o.name = "";
		o.builder = "";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new SimpleCarPopulateFirst();
		o.name = "";
		o.builder = "";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new SimpleCarPopulateLast();
		o.name = "";
		o.builder = "";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new SimpleCarPopulateAll();
		o.name = "";
		o.builder = "";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testComplexDefaultAndCompositeValidation() {
		Set<ConstraintViolation<ComplexCar>> violations;
		ComplexCar o;

		o = new ComplexCarPopulateNone();
		violations = validator.validate(o);
		assertTrue(violations.size() == 2);
		assertTrue(containsViolation(violations, "name"));

		o = new ComplexCarPopulateFirst();
		o.brand = new Brand();
		violations = validator.validate(o);
		assertTrue(violations.size() == 3);
		assertTrue(containsViolation(violations, "name"));
		assertTrue(containsViolation(violations, "brand.name"));

		o = new ComplexCarPopulateLast();
		o.builder = new Builder();
		violations = validator.validate(o);
		assertTrue(violations.size() == 3);
		assertTrue(containsViolation(violations, "name"));
		assertTrue(containsViolation(violations, "builder.name"));

		/*
		 * This case is difficult, since contradicting constraint messages will
		 * also be included. Anyways we cover this case to clarify behavior.
		 */
		o = new ComplexCarPopulateAll();
		o.brand = new Brand();
		o.builder = new Builder();
		violations = validator.validate(o);
		assertTrue(violations.size() == 6);
		assertTrue(containsViolation(violations, "name"));
		/* Brand must be null according to the validation rule */
		assertTrue(containsViolation(violations, "brand"));
		assertTrue(containsViolation(violations, "brand.name"));
		/* Builder must be null according to the validation rule */
		assertTrue(containsViolation(violations, "builder"));
		assertTrue(containsViolation(violations, "builder.name"));
	}

	@Test
	public void testComplexCompositeValidation() {
		Set<ConstraintViolation<ComplexCar>> violations;
		ComplexCar o;

		o = new ComplexCarPopulateNone();
		o.name = "";
		violations = validator.validate(o);
		assertTrue(violations.size() == 1);

		o = new ComplexCarPopulateFirst();
		o.name = "";
		o.brand = new Brand();
		violations = validator.validate(o);
		assertTrue(violations.size() == 2);
		assertTrue(containsViolation(violations, "brand.name"));

		o = new ComplexCarPopulateLast();
		o.name = "";
		o.builder = new Builder();
		violations = validator.validate(o);
		assertTrue(violations.size() == 2);
		assertTrue(containsViolation(violations, "builder.name"));

		o = new ComplexCarPopulateNone();
		o.name = "";
		o.brand = new Brand();
		o.builder = new Builder();
		violations = validator.validate(o);
		assertTrue(violations.size() == 1);

		o = new ComplexCarPopulateFirst();
		o.name = "";
		o.brand = new Brand();
		o.builder = new Builder();
		violations = validator.validate(o);
		assertTrue(violations.size() == 3);
		assertTrue(containsViolation(violations, "brand.name"));
		assertTrue(containsViolation(violations, "builder"));

		o = new ComplexCarPopulateLast();
		o.name = "";
		o.brand = new Brand();
		o.builder = new Builder();
		violations = validator.validate(o);
		assertTrue(violations.size() == 3);
		assertTrue(containsViolation(violations, "builder.name"));
		assertTrue(containsViolation(violations, "brand"));

		/*
		 * This case is difficult, since contradicting constraint messages will
		 * also be included. Anyways we cover this case to clarify behavior.
		 */
		o = new ComplexCarPopulateAll();
		o.name = "";
		o.brand = new Brand();
		o.builder = new Builder();
		violations = validator.validate(o);
		assertTrue(violations.size() == 5);
		/* Brand must be null according to the validation rule */
		assertTrue(containsViolation(violations, "brand"));
		assertTrue(containsViolation(violations, "brand.name"));
		/* Builder must be null according to the validation rule */
		assertTrue(containsViolation(violations, "builder"));
		assertTrue(containsViolation(violations, "builder.name"));

		/*
		 * This case is difficult, since contradicting constraint messages will
		 * also be included. Anyways we cover this case to clarify behavior.
		 */
		o = new ComplexCarPopulateAll();
		o.name = "";
		o.brand = new Brand();
		o.brand.name = "A";
		o.builder = new Builder();
		o.builder.name = "A";
		violations = validator.validate(o);
		assertTrue(violations.size() == 3);
		/* Brand must be null according to the validation rule */
		assertTrue(containsViolation(violations, "brand"));
		/* Builder must be null according to the validation rule */
		assertTrue(containsViolation(violations, "builder"));
	}

	@Test
	public void testComplexSuccessfulValidation() {
		Set<ConstraintViolation<ComplexCar>> violations;
		ComplexCar o;

		o = new ComplexCarPopulateNone();
		o.name = "";
		o.brand = new Brand();
		o.brand.name = "A";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new ComplexCarPopulateFirst();
		o.name = "";
		o.brand = new Brand();
		o.brand.name = "A";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new ComplexCarPopulateLast();
		o.name = "";
		o.brand = new Brand();
		o.brand.name = "A";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new ComplexCarPopulateAll();
		o.name = "";
		o.brand = new Brand();
		o.brand.name = "A";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new ComplexCarPopulateNone();
		o.name = "";
		o.builder = new Builder();
		o.builder.name = "A";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new ComplexCarPopulateFirst();
		o.name = "";
		o.builder = new Builder();
		o.builder.name = "A";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new ComplexCarPopulateLast();
		o.name = "";
		o.builder = new Builder();
		o.builder.name = "A";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());

		o = new ComplexCarPopulateAll();
		o.name = "";
		o.builder = new Builder();
		o.builder.name = "A";
		violations = validator.validate(o);
		assertTrue(violations.isEmpty());
	}

	class Car {
		@NotNull
		String name;
	}

	class SimpleCar extends Car {
		@Null(groups = BuilderGroup.class)
		@NotNull(groups = BrandGroup.class)
		String brand;
		@Null(groups = BrandGroup.class)
		@NotNull(groups = BuilderGroup.class)
		String builder;
	}

	@CheckEither(value = { BrandGroup.class, BuilderGroup.class }, message = "Either brand or builder have to be not null", mode = PopulationMode.NONE)
	class SimpleCarPopulateNone extends SimpleCar {
	}

	@CheckEither(value = { BrandGroup.class, BuilderGroup.class }, message = "Either brand or builder have to be not null", mode = PopulationMode.FIRST)
	class SimpleCarPopulateFirst extends SimpleCar {
	}

	@CheckEither(value = { BrandGroup.class, BuilderGroup.class }, message = "Either brand or builder have to be not null", mode = PopulationMode.LAST)
	class SimpleCarPopulateLast extends SimpleCar {
	}

	@CheckEither(value = { BrandGroup.class, BuilderGroup.class }, message = "Either brand or builder have to be not null", mode = PopulationMode.ALL)
	class SimpleCarPopulateAll extends SimpleCar {
	}

	class ComplexCar extends Car {
		@Null(groups = BuilderGroup.class)
		@NotNull(groups = BrandGroup.class)
		@Valid
		Brand brand;
		@Null(groups = BrandGroup.class)
		@NotNull(groups = BuilderGroup.class)
		@Valid
		Builder builder;
	}

	@CheckEither(value = { BrandGroup.class, BuilderGroup.class }, message = "Either brand or builder have to be not null", mode = PopulationMode.NONE)
	class ComplexCarPopulateNone extends ComplexCar {
	}

	@CheckEither(value = { BrandGroup.class, BuilderGroup.class }, message = "Either brand or builder have to be not null", mode = PopulationMode.FIRST)
	class ComplexCarPopulateFirst extends ComplexCar {
	}

	@CheckEither(value = { BrandGroup.class, BuilderGroup.class }, message = "Either brand or builder have to be not null", mode = PopulationMode.LAST)
	class ComplexCarPopulateLast extends ComplexCar {
	}

	@CheckEither(value = { BrandGroup.class, BuilderGroup.class }, message = "Either brand or builder have to be not null", mode = PopulationMode.ALL)
	class ComplexCarPopulateAll extends ComplexCar {
	}

	class Builder {
		@NotNull(groups = BuilderGroup.class)
		@Size(min = 1, groups = BuilderGroup.class)
		String name;
	}

	class Brand {
		@NotNull(groups = BrandGroup.class)
		@Size(min = 1, groups = BrandGroup.class)
		String name;
	}

	interface BrandGroup {
	}

	interface BuilderGroup {
	}

}
