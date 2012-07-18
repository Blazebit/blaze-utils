/*
 * Copyright 2011 Blazebit
 */

package com.blazebit.cdi;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;

/**
 * Literal for annotation, that can be used for extensions.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 * @see Default
 */
public class DefaultLiteral extends AnnotationLiteral<Default> implements
		Default {
	private static final long serialVersionUID = 1L;
}
