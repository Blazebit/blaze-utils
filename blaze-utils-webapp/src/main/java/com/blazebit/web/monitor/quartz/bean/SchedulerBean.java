/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.web.monitor.quartz.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.quartz.SchedulerException;
import org.quartz.impl.SchedulerRepository;

import com.blazebit.monitor.quartz.model.SchedulerConfiguration;
import com.blazebit.monitor.quartz.service.SchedulerConfigurationService;

/**
 * 
 * @author Christian Beikov
 */
@Named("schedulerBean")
@ViewAccessScoped
public class SchedulerBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<SchedulerConfiguration> schedulers = new ArrayList<SchedulerConfiguration>();
	private SchedulerConfiguration scheduler;

	@Inject
	private SchedulerConfigurationService service;

	public void preRender() throws SchedulerException {
		schedulers = service.getAllConfigurations();
		scheduler = new SchedulerConfiguration();
	}

	public String getSchedulerStatus(Object schedulerConfig)
			throws SchedulerException {
		return getSchedulerStatus((SchedulerConfiguration) schedulerConfig);
	}

	public String getSchedulerStatus(SchedulerConfiguration schedulerConfig)
			throws SchedulerException {
		try {
			if (schedulerConfig.getScheduler() != null) {
				if (schedulerConfig.getScheduler().isInStandbyMode()) {
					return "Standby";
				} else if (schedulerConfig.getScheduler().isShutdown()) {
					return "Shutdown";
				} else if (schedulerConfig.getScheduler().isStarted()) {
					return "Started";
				}
			}

			return "Shutdown";
		} catch (SchedulerException ex) {
			return "Shutdown";
		}
	}

	public String newScheduler(ActionEvent event) {
		scheduler = new SchedulerConfiguration();
		return "";
	}

	public String saveScheduler() {
		service.saveConfiguration(scheduler);
		return "";
	}

	public String deleteScheduler(SchedulerConfiguration schedulerConfig) {
		service.deleteConfiguration(schedulerConfig);

		try {
			SchedulerRepository.getInstance().remove(
					schedulerConfig.getSchedulerName());
		} catch (Exception ex) {
			// Don't care
		}

		return "";
	}

	public String startScheduler(SchedulerConfiguration schedulerConfig)
			throws SchedulerException {
		if (schedulerConfig.getScheduler() != null) {
			schedulerConfig.getScheduler().start();
		}

		return "";
	}

	public String shutdownScheduler(SchedulerConfiguration schedulerConfig)
			throws SchedulerException {
		if (schedulerConfig.getScheduler() != null) {
			schedulerConfig.getScheduler().shutdown();
		}

		return "";
	}

	public String standbyScheduler(SchedulerConfiguration schedulerConfig)
			throws SchedulerException {
		if (schedulerConfig.getScheduler() != null) {
			schedulerConfig.getScheduler().standby();
		}

		return "";
	}

	public String resumeScheduler(SchedulerConfiguration schedulerConfig)
			throws SchedulerException {
		if (schedulerConfig.getScheduler() != null) {
			schedulerConfig.getScheduler().resumeAll();
		}

		return "";
	}

	public String pauseScheduler(SchedulerConfiguration schedulerConfig)
			throws SchedulerException {
		if (schedulerConfig.getScheduler() != null) {
			schedulerConfig.getScheduler().pauseAll();
		}

		return "";
	}

	public List<SchedulerConfiguration> getSchedulers() {
		return new ArrayList<SchedulerConfiguration>(schedulers);
	}

	public SchedulerConfiguration getScheduler() {
		return scheduler;
	}

	public void setScheduler(SchedulerConfiguration scheduler) {
		this.scheduler = scheduler;
	}
}
