package com.blazebit.validation.constraint.validator;

import java.util.Comparator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext;

import com.blazebit.lang.StringUtils;
import com.blazebit.reflection.ExpressionUtils;
import com.blazebit.validation.constraint.CheckCompare;
import com.blazebit.validation.constraint.ComparisonMode;

public class CheckCompareValidator implements
		ConstraintValidator<CheckCompare, Object> {

	private String[] propertyPaths;
	private ComparisonMode comparisonMode;
	private Comparator<Object> comparator;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(CheckCompare constraintAnnotation) {
		this.propertyPaths = constraintAnnotation.value();
		this.comparisonMode = constraintAnnotation.mode();

		if (this.propertyPaths.length < 2) {
			throw new IllegalArgumentException(
					"At least two property paths have to be given for the check constraint to work properly!");
		}

		try {
			this.comparator = (Comparator<Object>) constraintAnnotation
					.comparator().newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Could not instantiate comparator", e);
		}
	}

	@Override
	public boolean isValid(Object target, ConstraintValidatorContext context) {
		boolean isValid = true;

		try {
			Object lastValue = ExpressionUtils.getNullSafeValue(target,
					propertyPaths[0]);

			for (int i = 1; i < propertyPaths.length; i++) {
				Object current = ExpressionUtils.getNullSafeValue(target,
						propertyPaths[i]);

				if ((comparisonMode == ComparisonMode.EQUAL && comparator
						.compare(lastValue, current) != 0)
						|| (comparisonMode == ComparisonMode.NOT_EQUAL && comparator
								.compare(lastValue, current) == 0)) {
					isValid = false;
					break;
				}

				lastValue = current;
			}

			if (!isValid) {
				/*
				 * if custom message was provided, don't touch it, otherwise
				 * build the default message
				 */
				String message = context.getDefaultConstraintMessageTemplate();
				message = (message.isEmpty()) ? resolveMessage() : message;

				context.disableDefaultConstraintViolation();
				ConstraintViolationBuilder violationBuilder = context
						.buildConstraintViolationWithTemplate(message);

				for (String propertyName : propertyPaths) {
					NodeBuilderDefinedContext nbdc = violationBuilder
							.addNode(propertyName);
					nbdc.addConstraintViolation();
				}
			}
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}

		return isValid;
	}

	private String resolveMessage() {
		final StringBuilder sb = new StringBuilder(propertyPaths.length * 10);

		sb.append('[');
		StringUtils.join(sb, ", ", propertyPaths);
		sb.append(']');
		sb.append(" must");

		switch (comparisonMode) {
		case EQUAL:
			sb.append(" be equal");
			break;
		case NOT_EQUAL:
			sb.append(" not be equal");
			break;
		}

		sb.append('.');
		return sb.toString();
	}
}