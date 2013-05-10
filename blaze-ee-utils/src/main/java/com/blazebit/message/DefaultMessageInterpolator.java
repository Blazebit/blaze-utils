package com.blazebit.message;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;

import javax.enterprise.inject.Specializes;

@Specializes
public class DefaultMessageInterpolator extends
		org.apache.deltaspike.core.impl.message.DefaultMessageInterpolator {

	private static final long serialVersionUID = 1L;

	@Override
	public String interpolate(String messageTemplate, Serializable[] arguments,
			Locale locale) {
		if (arguments == null || arguments.length == 0) {
			return messageTemplate;
		} else {
			return MessageFormat.format(messageTemplate, (Object[]) arguments);
		}
	}

}
