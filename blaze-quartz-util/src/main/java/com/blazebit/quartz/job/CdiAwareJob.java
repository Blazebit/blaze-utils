/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz.job;

import java.io.Serializable;
import org.quartz.Job;

/**
 * Marker interface for Jobs that should be instantiated as beans.
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public interface CdiAwareJob extends Job, Serializable {
    
}
