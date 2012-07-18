/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.monitor.quartz.service;

import com.blazebit.monitor.quartz.model.SchedulerConfiguration;
import java.util.List;

/**
 *
 * @author Christian Beikov
 */
public interface SchedulerConfigurationService {

    public List<SchedulerConfiguration> getAllConfigurations();

    public SchedulerConfiguration saveConfiguration(SchedulerConfiguration schedulerConfiguration);

    public void deleteConfiguration(SchedulerConfiguration scheduler);
}
