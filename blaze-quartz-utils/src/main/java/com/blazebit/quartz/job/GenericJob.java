/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz.job;

import java.io.Serializable;
import java.util.List;
import org.quartz.Job;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public interface GenericJob extends Job, Serializable {

	public List<JobParameter> getParameters();

}
