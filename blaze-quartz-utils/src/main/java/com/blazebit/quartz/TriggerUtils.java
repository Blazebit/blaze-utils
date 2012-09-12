/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class TriggerUtils {

	public static Trigger simple(String triggerName, String triggerGroup,
			JobDetail job, Map<String, ? extends Object> dataMap, Date start,
			Date end) {
		return simple(triggerName, triggerGroup, job.getKey().getName(), job
				.getKey().getGroup(), dataMap, start, end);
	}

	public static Trigger simple(String triggerName, String triggerGroup,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap, Date start, Date end) {
		return simple(triggerName, triggerGroup, jobName, jobGroup, dataMap,
				start, end, 0);
	}

	public static Trigger simple(String triggerName, String triggerGroup,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap, Date start, Date end,
			int repeat) {
		TriggerBuilder<Trigger> tb = TriggerBuilder.newTrigger().forJob(
				jobName, jobGroup);

		if (triggerName != null && !triggerName.isEmpty()) {
			if (triggerGroup != null && !triggerGroup.isEmpty()) {
				tb.withIdentity(triggerName, triggerGroup);
			} else {
				tb.withIdentity(triggerName);
			}
		} else if (triggerGroup != null && !triggerGroup.isEmpty()) {
			tb.withIdentity(UUID.randomUUID().toString(), triggerGroup);
		}

		if (start == null || start.before(new Date())) {
			tb.startNow();
		} else {
			tb.startAt(start);
		}

		if (end != null && end.after(new Date())) {
			tb.endAt(end);
		}

		if (dataMap != null && !dataMap.isEmpty()) {
			tb.usingJobData(new JobDataMap(dataMap));
		}

		if (repeat > 0) {
			tb.withSchedule(SimpleScheduleBuilder.simpleSchedule()
					.withRepeatCount(repeat));
		}

		return tb.build();
	}

	public static Trigger second(String triggerName, String triggerGroup,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap, int interval, Date start,
			Date end) {
		return interval(triggerName, triggerGroup, jobName, jobGroup, dataMap,
				interval, IntervalUnit.SECOND, start, end);
	}

	public static Trigger minute(String triggerName, String triggerGroup,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap, int interval, Date start,
			Date end) {
		return interval(triggerName, triggerGroup, jobName, jobGroup, dataMap,
				interval, IntervalUnit.MINUTE, start, end);
	}

	public static Trigger hour(String triggerName, String triggerGroup,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap, int interval, Date start,
			Date end) {
		return interval(triggerName, triggerGroup, jobName, jobGroup, dataMap,
				interval, IntervalUnit.HOUR, start, end);
	}

	public static Trigger day(String triggerName, String triggerGroup,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap, int interval, Date start,
			Date end) {
		return interval(triggerName, triggerGroup, jobName, jobGroup, dataMap,
				interval, IntervalUnit.DAY, start, end);
	}

	public static Trigger week(String triggerName, String triggerGroup,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap, int interval, Date start,
			Date end) {
		return interval(triggerName, triggerGroup, jobName, jobGroup, dataMap,
				interval, IntervalUnit.WEEK, start, end);
	}

	public static Trigger month(String triggerName, String triggerGroup,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap, int interval, Date start,
			Date end) {
		return interval(triggerName, triggerGroup, jobName, jobGroup, dataMap,
				interval, IntervalUnit.MONTH, start, end);
	}

	public static Trigger year(String triggerName, String triggerGroup,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap, int interval, Date start,
			Date end) {
		return interval(triggerName, triggerGroup, jobName, jobGroup, dataMap,
				interval, IntervalUnit.YEAR, start, end);
	}

	public static Trigger cron(String triggerName, String triggerGroup,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap, String cronExpression)
			throws ParseException {
		TriggerBuilder<CronTrigger> tb = TriggerBuilder.newTrigger()
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.forJob(jobName, jobGroup);

		if (triggerName != null && !triggerName.isEmpty()) {
			if (triggerGroup != null && !triggerGroup.isEmpty()) {
				tb.withIdentity(triggerName, triggerGroup);
			} else {
				tb.withIdentity(triggerName);
			}
		}

		if (dataMap != null && !dataMap.isEmpty()) {
			tb.usingJobData(new JobDataMap(dataMap));
		}

		return tb.build();
	}

	private static Trigger interval(String triggerName, String triggerGroup,
			String jobName, String jobGroup,
			Map<String, ? extends Object> dataMap, int interval,
			IntervalUnit unit, Date start, Date end) {
		TriggerBuilder<CalendarIntervalTrigger> tb = TriggerBuilder
				.newTrigger()
				.withSchedule(
						CalendarIntervalScheduleBuilder
								.calendarIntervalSchedule().withInterval(
										interval, unit))
				.forJob(jobName, jobGroup);

		if (triggerName != null && !triggerName.isEmpty()) {
			if (triggerGroup != null && !triggerGroup.isEmpty()) {
				tb.withIdentity(triggerName, triggerGroup);
			} else {
				tb.withIdentity(triggerName);
			}
		}

		if (start != null && start.after(new Date())) {
			tb.startAt(start);
		}

		if (end != null && end.after(new Date())) {
			tb.endAt(end);
		}

		if (dataMap != null && !dataMap.isEmpty()) {
			tb.usingJobData(new JobDataMap(dataMap));
		}

		return tb.build();
	}

	public static void schedule(Trigger trigger) throws SchedulerException {
		Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
		sched.scheduleJob(trigger);
	}

	public static void pause(Trigger trigger) throws SchedulerException {
		pause(trigger.getKey());
	}

	public static void pause(TriggerKey key) throws SchedulerException {
		Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
		sched.pauseTrigger(key);
	}

	public static void delete(Trigger trigger) throws SchedulerException {
		delete(trigger.getKey());
	}

	public static void delete(TriggerKey key) throws SchedulerException {
		Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
		sched.unscheduleJob(key);
	}

	public static void resume(Trigger trigger) throws SchedulerException {
		resume(trigger.getKey());
	}

	public static void resume(TriggerKey key) throws SchedulerException {
		Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
		sched.resumeTrigger(key);
	}

	public static Trigger.TriggerState getState(Trigger trigger)
			throws SchedulerException {
		return getState(trigger.getKey());
	}

	public static Trigger.TriggerState getState(TriggerKey key)
			throws SchedulerException {
		Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
		return sched.getTriggerState(key);
	}

	public static List<Trigger> getTriggers() throws SchedulerException {
		Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
		List<String> triggerGroups = sched.getTriggerGroupNames();
		List<Trigger> triggers = new ArrayList<Trigger>();

		for (int i = 0; i < triggerGroups.size(); i++) {
			String name = triggerGroups.get(i);
			Set<TriggerKey> keys = sched.getTriggerKeys(GroupMatcher
					.triggerGroupEquals(name));
			Iterator<TriggerKey> iter = keys.iterator();

			while (iter.hasNext()) {
				triggers.add(sched.getTrigger(iter.next()));
			}
		}

		return triggers;
	}
}
