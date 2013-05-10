/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.monitor.quartz.service.impl;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.blazebit.monitor.quartz.model.SchedulerConfiguration;
import com.blazebit.monitor.quartz.service.SchedulerConfigurationService;

/**
 * 
 * @author Christian Beikov
 */
@Stateless
public class SchedulerConfigurationServiceImpl implements
		SchedulerConfigurationService, Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceContext(name = "QuartzManagerPU")
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<SchedulerConfiguration> getAllConfigurations() {
		return em.createQuery("FROM SchedulerConfiguration",
				SchedulerConfiguration.class).getResultList();
	}

	@Override
	public SchedulerConfiguration saveConfiguration(
			SchedulerConfiguration schedulerConfiguration) {
		SchedulerConfiguration elem = em.merge(schedulerConfiguration);
		em.flush();

		return elem;
	}

	@Override
	public void deleteConfiguration(
			SchedulerConfiguration schedulerConfiguration) {
		em.remove(em.getReference(SchedulerConfiguration.class,
				schedulerConfiguration.getId()));
		em.flush();
	}
}
