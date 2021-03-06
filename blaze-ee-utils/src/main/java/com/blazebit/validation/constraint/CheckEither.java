package com.blazebit.validation.constraint;

import com.blazebit.validation.constraint.validator.CheckEitherValidator;

import javax.validation.Constraint;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * This constraint tries to validate the object of the class which is annotated
 * with this annotation, with each of the given validation groups. If at least
 * one validation with a validation group succeeds, the validation for this
 * constraint also succeeds. The {@link PopulationMode} influences how or which
 * of the {@link ConstraintViolation}s occurred within the sub validation will
 * be passed to the {@link ConstraintValidatorContext}.
 * <p>
 * The default {@link PopulationMode} is {@link PopulationMode#NONE}. The
 * behaviors of the different {@link PopulationMode}s is as follows:
 * <ul>
 * <li>{@link PopulationMode#NONE} will result in not passing the
 * {@link ConstraintViolation}s that occurred in the sub validations to the
 * {@link ConstraintValidatorContext}.</li>
 * <li>{@link PopulationMode#FIRST} will result in passing the first
 * {@link ConstraintViolation}s that occurred in the sub validations to the
 * {@link ConstraintValidatorContext}.</li>
 * <li>{@link PopulationMode#LAST} will result in passing the last
 * {@link ConstraintViolation}s that occurred in the sub validations to the
 * {@link ConstraintValidatorContext}.</li>
 * <li>{@link PopulationMode#ALL} will result in passing the all
 * {@link ConstraintViolation}s that occurred in the sub validations to the
 * {@link ConstraintValidatorContext}.</li>
 * </ul>
 *
 * @author Christian Beikov
 * @see PopulationMode
 * @since 1.0
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE,
        ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckEitherValidator.class)
@Documented
public @interface CheckEither {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The validation group classes for which a validation should be processed.
     *
     * @return
     */
    Class<?>[] value() default {};

    /**
     * The population mode that should be used.
     *
     * @return
     */
    PopulationMode mode() default PopulationMode.NONE;

    /**
     * Defines several @CheckEither annotations on the same element
     *
     * @see (@link CheckEither}
     */
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE,
            ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        CheckEither[] value();
    }
}
