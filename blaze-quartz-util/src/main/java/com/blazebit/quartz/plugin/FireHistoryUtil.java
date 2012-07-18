/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz.plugin;

import java.util.List;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class FireHistoryUtil {

	public static List<FireHistoryEntry> getEntries() throws SchedulerException {
		Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
		FireHistoryPlugin plugin = (FireHistoryPlugin) sched
				.getListenerManager().getTriggerListener("FireHistoryPlugin");

		if (plugin == null)
			throw new SchedulerException("Plugin not installed!");

		return plugin.getEntries();
	}
}
