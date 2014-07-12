package com.blazebit.validation.constraint;

import com.blazebit.validation.constraint.validator.CheckCompareValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 
 * @author Christian Beikov
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckCompareValidator.class)
@Documented
public @interface CheckCompare {

	String message() default "";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * 
	 * @return
	 */
	String[] value();

	/**
	 * 
	 * @return
	 */
	ComparisonMode mode() default ComparisonMode.EQUAL;

	/**
	 * 
	 * @return
	 */
	Class<? extends Comparator<?>> comparator() default EqualsComparator.class;

	/**
	 * Defines several @CheckCompare annotations on the same element
	 * 
	 * @see (@link CheckCompare}
	 */
	@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE,
			ElementType.CONSTRUCTOR, ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		CheckCompare[] value();
	}

	class EqualsComparator implements Comparator<Object> {

		@Override
		public int compare(Object o1, Object o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o1 == null || o2 == null) {
				return -1;
			}

			return o1.equals(o2) ? 0 : -1;
		}

	}
}