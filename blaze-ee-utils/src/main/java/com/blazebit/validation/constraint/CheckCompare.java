package com.blazebit.validation.constraint;

import com.blazebit.validation.constraint.validator.CheckCompareValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.Comparator;

/**
 * @author Christian Beikov
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckCompareValidator.class)
@Documented
public @interface CheckCompare {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return
     */
    String[] value();

    /**
     * @return
     */
    ComparisonMode mode() default ComparisonMode.EQUAL;

    /**
     * @return
     */
    Class<? extends Comparator<?>> comparator() default EqualsComparator.class;

    /**
     * Defines several @CheckCompare annotations on the same element
     *
     * @see (@link CheckCompare}
     */
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE,
            ElementType.CONSTRUCTOR, ElementType.PARAMETER})
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