/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.web.monitor.quartz.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;

import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.blazebit.quartz.JobUtils;
import com.blazebit.quartz.job.GenericJob;
import com.blazebit.quartz.job.JobParameter;
import com.blazebit.quartz.job.http.HttpGetInvokerJob;
import com.blazebit.quartz.job.http.HttpPostInvokerJob;
import com.blazebit.quartz.job.mail.SimpleSendMailJob;
import com.blazebit.web.monitor.quartz.model.Property;

/**
 * 
 * @author Christian Beikov
 */
@Named("jobBean")
@SessionScoped
public class JobBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Scheduler scheduler;
	private String jobName;
	private String jobGroup;
	private Class<? extends GenericJob> jobType;
	private JobDetail selectedJob;
	private List<JobDetail> jobs;
	private List<Trigger> jobTriggers;
	private List<Class<? extends GenericJob>> jobTypes;
	private Map<Class<? extends GenericJob>, List<JobParameter>> propertyMap;
	private List<Property> jobDataMap;

	public JobBean() {
		jobTypes = new ArrayList<Class<? extends GenericJob>>();
		jobTypes.add(SimpleSendMailJob.class);
		jobTypes.add(HttpGetInvokerJob.class);
		jobTypes.add(HttpPostInvokerJob.class);
		jobType = SimpleSendMailJob.class;

		propertyMap = new HashMap<Class<? extends GenericJob>, List<JobParameter>>();
		propertyMap.put(SimpleSendMailJob.class,
				new SimpleSendMailJob().getParameters());
		propertyMap.put(HttpGetInvokerJob.class,
				new HttpGetInvokerJob().getParameters());
		propertyMap.put(HttpPostInvokerJob.class,
				new HttpPostInvokerJob().getParameters());
		jobDataMap = getCopiedProperties(jobType);
	}

	public void preRender() throws SchedulerException {
		jobs = JobUtils.getJobs(scheduler);

		if (selectedJob != null) {
			jobTriggers = JobUtils.getTriggers(scheduler, selectedJob);
		}
	}

	private List<Property> getCopiedProperties(
			Class<? extends GenericJob> jobClass) {
		List<Property> copyList = new ArrayList<Property>();

		if (jobTypes.contains(jobClass)) {
			for (JobParameter p : propertyMap.get(jobClass)) {
				copyList.add(new Property(p.getName(), "", p.isRequired(), p
						.getType(), p.getDescription()));
			}
		}

		return copyList;
	}

	private Map<String, Object> getAsMap(List<Property> properties) {
		Map<String, Object> map = new HashMap<String, Object>();

		for (Property p : properties) {
			if (p.getName() != null
					&& !p.getName().isEmpty()
					&& p.getValue() != null
					&& (((p.getValue() instanceof String) && !((String) p
							.getValue()).isEmpty()) || !(p.getValue() instanceof String))) {
				map.put(p.getName(), p.getValue());
			}
		}

		return map;
	}

	public List<Property> getSelectedJobDataMap() {
		if (selectedJob == null) {
			return Collections.emptyList();
		}
		List<Property> dataMap = new ArrayList<Property>();

		for (Map.Entry<String, Object> entry : selectedJob.getJobDataMap()
				.entrySet()) {
			dataMap.add(new Property(entry.getKey(), entry.getValue()));
		}

		return dataMap;
	}

	public void addParameter(ActionEvent event) {
		jobDataMap.add(new Property("", "", false, String.class, ""));
	}

	public String addJob() throws SchedulerException {
		try {
			JobUtils.add(scheduler, jobType, jobName, jobGroup,
					getAsMap(jobDataMap));
		} catch (ObjectAlreadyExistsException ex) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Job mit diesem Namen und dieser Gruppe existiert bereits!",
									null));
		} catch (SchedulerException ex) {
			throw ex;
		}
		selectedJob = null;
		jobType = SimpleSendMailJob.class;
		jobName = null;
		jobGroup = null;
		jobDataMap = getCopiedProperties(jobType);
		return "";
	}

	public String deleteJob(JobDetail job) throws SchedulerException {
		JobUtils.delete(scheduler, job);
		selectedJob = null;

		return "";
	}

	public String triggerJob(JobDetail job) throws SchedulerException {
		JobUtils.trigger(scheduler, job);
		return "";
	}

	public String pauseJob(JobDetail job) throws SchedulerException {
		JobUtils.pause(scheduler, job);
		return "";
	}

	public String resumeJob(JobDetail job) throws SchedulerException {
		JobUtils.resume(scheduler, job);
		return "";
	}

	public List<JobDetail> getJobs() {
		return jobs;
	}

	public void setJobs(List<JobDetail> jobs) {
		this.jobs = jobs;
	}

	public List<Trigger> getJobTriggers() {
		return jobTriggers;
	}

	public void setJobTriggers(List<Trigger> jobTriggers) {
		this.jobTriggers = jobTriggers;
	}

	public JobDetail getSelectedJob() {
		return selectedJob;
	}

	public void setSelectedJob(JobDetail selectedJob) {
		this.selectedJob = selectedJob;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Class<? extends GenericJob> getJobType() {
		return jobType;
	}

	@SuppressWarnings("unchecked")
	public void jobTypeChanged(ValueChangeEvent event) {
		jobDataMap = getCopiedProperties((Class<? extends GenericJob>) event
				.getNewValue());
	}

	public void setJobType(Class<? extends GenericJob> jobType) {
		this.jobType = jobType;
	}

	public List<Class<? extends GenericJob>> getJobTypes() {
		return jobTypes;
	}

	public void setJobTypes(List<Class<? extends GenericJob>> jobTypes) {
		this.jobTypes = jobTypes;
	}

	public List<Property> getJobDataMap() {
		return jobDataMap;
	}

	public void setJobDataMap(List<Property> jobDataMap) {
		this.jobDataMap = jobDataMap;
	}

	public String getSchedulerName() throws SchedulerException {
		return scheduler == null ? null : scheduler.getSchedulerName();
	}

	public void setSchedulerName(String schedulerName)
			throws SchedulerException {
		this.scheduler = new StdSchedulerFactory().getScheduler(schedulerName);

		if (this.scheduler == null) {
			throw new IllegalArgumentException("Scheduler can not be found!");
		}
	}
}
