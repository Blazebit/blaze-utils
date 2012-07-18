/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class SchedulerUtil {

    private static final Logger log = LoggerFactory.getLogger(SchedulerUtil.class);
    
    public static void start() throws SchedulerException{
        Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
        sched.start();
    }
    
    public static void shutdown() throws SchedulerException{
        Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
        sched.shutdown();
    }
    
    public static void standby() throws SchedulerException{
        Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
        sched.standby();
    }
    
    public static void pause() throws SchedulerException{
        Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
        sched.pauseAll();
    }
    
    public static void resume() throws SchedulerException{
        Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
        sched.resumeAll();
    }
    
    public static boolean isStarted() throws SchedulerException{
        Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
        return sched.isStarted();
    }
    
    public static boolean isStandby() throws SchedulerException{
        Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
        return sched.isInStandbyMode();
    }
    
    public static boolean isShutdown() throws SchedulerException{
        Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
        return sched.isShutdown();
    }
}
