/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.monitor.quartz.service.impl;

import com.blazebit.cdi.transaction.annotation.Transactional;
import com.blazebit.monitor.quartz.model.SchedulerConfiguration;
import com.blazebit.monitor.quartz.service.SchedulerConfigurationService;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Christian Beikov
 */
public class SchedulerConfigurationServiceImpl implements SchedulerConfigurationService, Serializable {
    private static final long serialVersionUID = 1L;
    
    @PersistenceContext(name="QuartzManagerPU")
    private EntityManager em;
    
    @Override
    public List<SchedulerConfiguration> getAllConfigurations(){
        return em.createQuery("FROM SchedulerConfiguration", SchedulerConfiguration.class).getResultList();
    }
    
    @Transactional
    @Override
    public SchedulerConfiguration saveConfiguration(SchedulerConfiguration schedulerConfiguration){
        SchedulerConfiguration elem = em.merge(schedulerConfiguration);
        em.flush();
        
        return elem;
    }

    @Transactional
    @Override
    public void deleteConfiguration(SchedulerConfiguration schedulerConfiguration) {
        em.remove(em.getReference(SchedulerConfiguration.class, schedulerConfiguration.getId()));
        em.flush();
    }
}
