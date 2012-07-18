/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.blazefaces.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * 
 * @author Christian Beikov
 */
public abstract class AbstractDelegatingValidator implements Validator {

	protected static final String VALIDATOR_ID = "validatorId";

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent,
			Object value) throws ValidatorException {
		String validatorId = (String) uiComponent.getAttributes().get(
				VALIDATOR_ID);
		Validator validator = lookup(facesContext, validatorId);
		validator.validate(facesContext, uiComponent, value);
	}

	protected Validator lookup(FacesContext facesContext, String validatorId) {
		return facesContext.getApplication().createValidator(validatorId);
	}
}
