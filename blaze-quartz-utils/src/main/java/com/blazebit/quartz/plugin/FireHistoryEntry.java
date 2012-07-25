/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz.plugin;

import java.io.Serializable;
import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class FireHistoryEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private String schedulerName;
	private TriggerKey triggerKey;
	private JobKey jobKey;
	private Date scheduledTime;
	private Date firedTime;
	private Trigger.TriggerState state;
	private long runTime;
	private JobDataMap dataMap;

	public FireHistoryEntry(String schedulerName, TriggerKey triggerKey,
			JobKey jobKey, Date scheduledTime, Date firedTime,
			TriggerState state, long runTime, JobDataMap dataMap) {
		this.schedulerName = schedulerName;
		this.triggerKey = triggerKey;
		this.jobKey = jobKey;
		this.scheduledTime = scheduledTime;
		this.firedTime = firedTime;
		this.state = state;
		this.runTime = runTime;
		this.dataMap = dataMap;
	}

	public TriggerState getState() {
		return state;
	}

	public String getSchedulerName() {
		return schedulerName;
	}

	public JobDataMap getDataMap() {
		return dataMap;
	}

	public Date getFiredTime() {
		return firedTime;
	}

	public JobKey getJobKey() {
		return jobKey;
	}

	public long getRunTime() {
		return runTime;
	}

	public Date getScheduledTime() {
		return scheduledTime;
	}

	public TriggerKey getTriggerKey() {
		return triggerKey;
	}

}
