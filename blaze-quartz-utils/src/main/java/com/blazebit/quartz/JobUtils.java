/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class JobUtils {

	public static void add(Scheduler sched, Class<? extends Job> clazz,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap) throws SchedulerException {
		sched.addJob(create(clazz, jobName, jobGroup, dataMap, true), false);
	}

	public static void schedule(Scheduler sched, Class<? extends Job> clazz,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap) throws SchedulerException {
		Trigger trigger = TriggerUtils.simple(null, null, jobName, jobGroup,
				null, null, null);
		schedule(sched, clazz, jobName, jobGroup, dataMap, trigger);
	}

	public static void schedule(Scheduler sched, Class<? extends Job> clazz,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap, Trigger trigger)
			throws SchedulerException {
		add(sched, clazz, jobName, jobGroup, dataMap);
		TriggerUtils.schedule(trigger);
	}

	public static void pause(Scheduler sched, JobDetail job)
			throws SchedulerException {
		pause(sched, job.getKey());
	}

	public static void pause(Scheduler sched, JobKey key)
			throws SchedulerException {
		sched.pauseJob(key);
	}

	public static void resume(Scheduler sched, JobDetail job)
			throws SchedulerException {
		resume(sched, job.getKey());
	}

	public static void resume(Scheduler sched, JobKey key)
			throws SchedulerException {
		sched.resumeJob(key);
	}

	public static void delete(Scheduler sched, JobDetail job)
			throws SchedulerException {
		delete(sched, job.getKey());
	}

	public static void delete(Scheduler sched, JobKey key)
			throws SchedulerException {
		sched.deleteJob(key);
	}

	public static void trigger(Scheduler sched, JobDetail job)
			throws SchedulerException {
		trigger(sched, job.getKey());
	}

	public static void trigger(Scheduler sched, JobKey key)
			throws SchedulerException {
		sched.triggerJob(key);
	}

	public static List<JobDetail> getJobs(Scheduler sched)
			throws SchedulerException {
		List<JobDetail> jobDetails = new ArrayList<JobDetail>();
		List<String> jobGroups = sched.getJobGroupNames();

		for (int i = 0; i < jobGroups.size(); i++) {
			String name = jobGroups.get(i);
			Set<JobKey> keys = sched.getJobKeys(GroupMatcher
					.jobGroupEquals(name));
			Iterator<JobKey> iter = keys.iterator();

			while (iter.hasNext()) {
				jobDetails.add(sched.getJobDetail(iter.next()));
			}
		}

		return jobDetails;
	}

	public static List<Trigger> getTriggers(Scheduler sched, JobDetail job)
			throws SchedulerException {
		return getTriggers(sched, job.getKey());
	}

	@SuppressWarnings("unchecked")
	public static List<Trigger> getTriggers(Scheduler sched, JobKey key)
			throws SchedulerException {
		return (List<Trigger>) sched.getTriggersOfJob(key);
	}

	private static JobDetail create(Class<? extends Job> clazz, String jobName,
			String jobGroup, Map<String, ? extends Object> dataMap,
			boolean durable) throws SchedulerException {
		return JobBuilder.newJob(clazz).withIdentity(jobName, jobGroup)
				.usingJobData(new JobDataMap(dataMap)).storeDurably(durable)
				.build();
	}
}
