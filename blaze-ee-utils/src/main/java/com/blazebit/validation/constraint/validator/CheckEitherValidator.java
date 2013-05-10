package com.blazebit.validation.constraint.validator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.blazebit.validation.constraint.CheckEither;
import com.blazebit.validation.constraint.PopulationMode;

public class CheckEitherValidator implements
		ConstraintValidator<CheckEither, Object> {

	private static final ValidatorFactory factory = Validation
			.buildDefaultValidatorFactory();

	private Class<?>[] value;
	private PopulationMode mode;

	/*
	 * Should work with hibernate validator and apache bval, not sure about
	 * other implementations
	 */
	private boolean supportsConstraintComposition = true;

	@Override
	public void initialize(CheckEither constraintAnnotation) {
		value = constraintAnnotation.value();
		mode = constraintAnnotation.mode();
	}

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		if (value.length < 1) {
			return false;
		}

		final Validator validator = factory.getValidator();
		Set<ConstraintViolation<Object>> actualViolations = null;

		for (final Class<?> groupClass : value) {
			final Set<ConstraintViolation<Object>> violations = validator
					.validate(object, groupClass);

			if (violations.isEmpty()) {
				/*
				 * We found a validation group that succeeds, so stop with
				 * success
				 */
				actualViolations = null;
				break;
			} else {
				/* Use population mode rules to preserve the needed violations */
				if (actualViolations == null || mode == PopulationMode.LAST) {
					actualViolations = new HashSet<ConstraintViolation<Object>>();
				}

				if (mode == PopulationMode.ALL
						|| (mode == PopulationMode.FIRST && actualViolations
								.isEmpty()) || mode == PopulationMode.LAST) {
					actualViolations = addViolations(violations,
							actualViolations);
				}
			}
		}

		if (actualViolations != null) {
			passViolations(context, actualViolations);
			return false;
		}

		return true;
	}

	/**
	 * Add the given source violations to the target set and return the target
	 * set. This method will skip violations that have a property path depth
	 * greater than 1.
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	private Set<ConstraintViolation<Object>> addViolations(
			Set<ConstraintViolation<Object>> source,
			Set<ConstraintViolation<Object>> target) {
		if (supportsConstraintComposition) {
			target.addAll(source);
			return target;
		}

		for (final Iterator<ConstraintViolation<Object>> iter = source
				.iterator(); iter.hasNext();) {
			final ConstraintViolation<Object> violation = iter.next();
			final Iterator<Node> nodeIter = violation.getPropertyPath()
					.iterator();

			/*
			 * Only include violations that have no property path or just one
			 * node
			 */
			if (!nodeIter.hasNext()
					|| (nodeIter.next() != null && !nodeIter.hasNext())) {
				target.add(violation);
			}
		}

		return target;
	}

	/**
	 * Pass the given violations to the given context. This method will skip
	 * violations that have a property path depth greater than 1.
	 * 
	 * @param context
	 * @param source
	 */
	private void passViolations(ConstraintValidatorContext context,
			Set<ConstraintViolation<Object>> source) {
		for (final ConstraintViolation<Object> violation : source) {
			final Iterator<Node> nodeIter = violation.getPropertyPath()
					.iterator();
			final ConstraintViolationBuilder builder = context
					.buildConstraintViolationWithTemplate(violation
							.getMessageTemplate());

			if (nodeIter.hasNext()) {
				StringBuilder sb = new StringBuilder(nodeIter.next().getName());

				if (supportsConstraintComposition) {
					while (nodeIter.hasNext()) {
						sb.append('.').append(nodeIter.next());
					}
				}

				builder.addNode(sb.toString());
			}

			builder.addConstraintViolation();
		}
	}

}
