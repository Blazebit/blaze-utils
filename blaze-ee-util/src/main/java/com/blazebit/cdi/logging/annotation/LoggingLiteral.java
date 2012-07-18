/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.logging.annotation;

import javax.enterprise.util.AnnotationLiteral;

/**
 * Literal for annotation, that can be used for extensions.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 * @see Logging
 */
public class LoggingLiteral extends AnnotationLiteral<Logging> implements
		Logging {
	private static final long serialVersionUID = 1L;

}
