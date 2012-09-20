package com.blazebit.validation.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;
import javax.validation.Validation;
import javax.validation.Validator;

import com.blazebit.lang.StringUtils;
import com.blazebit.lang.ValueRetriever;

public final class ConstraintValidatorUtils {

	private ConstraintValidatorUtils() {
	}

	public static Validator getValidator() {
		return Validation.buildDefaultValidatorFactory().getValidator();
	}

	public static <T> boolean containsViolation(
			Set<ConstraintViolation<T>> violations, String name) {
		if (name == null) {
			return false;
		}

		for (ConstraintViolation<T> violation : violations) {
			if(name.equals(StringUtils.join(".", violation.getPropertyPath(), nodeNameValueRetriever))){
				return true;
			}
		}

		return false;
	}
	
	private static final ValueRetriever<Node, String> nodeNameValueRetriever = new ValueRetriever<Node, String>(){

		@Override
		public String getValue(Node target) {
			final String name = target.getName();
			return name == null ? "" : name;
		}
		
	};
}