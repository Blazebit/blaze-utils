/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.blazefaces.validator;

import java.util.HashSet;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

/**
 * 
 * @author Christian Beikov
 */
@FacesValidator("onSubmitValidator")
public class OnSubmitValidator extends AbstractDelegatingValidator {

	private static final String PERFORM_VALIDATION = "performValidation";
	private static final String REQUIRED = "onSubmitRequired";
	private static final String FALSE = "false";

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if (!FALSE.equals(context.getExternalContext().getRequestParameterMap()
				.get(PERFORM_VALIDATION))) {
			Set<FacesMessage> messages = new HashSet<FacesMessage>();

			if (Boolean.TRUE.equals(component.getAttributes().get(REQUIRED))) {
				try {
					lookup(context, "javax.faces.Required").validate(context,
							component, value);
				} catch (ValidatorException e) {
					if (e.getFacesMessages() != null) {
						messages.addAll(e.getFacesMessages());
					} else {
						messages.add(e.getFacesMessage());
					}
				}
			}

			if (component.getAttributes().get(VALIDATOR_ID) != null) {
				try {
					super.validate(context, component, value);
				} catch (ValidatorException e) {
					if (e.getFacesMessages() != null) {
						messages.addAll(e.getFacesMessages());
					} else {
						messages.add(e.getFacesMessage());
					}
				}
			}

			// if there are any messages - throw validation exception
			if (!messages.isEmpty()) {
				throw new ValidatorException(messages);
			}
		}
	}
}
