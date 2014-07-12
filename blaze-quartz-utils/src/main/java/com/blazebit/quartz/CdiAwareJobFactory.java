/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz;

import com.blazebit.cdi.CdiUtils;
import com.blazebit.quartz.job.CdiAwareJob;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class CdiAwareJobFactory implements JobFactory {

	private static final Logger log = LoggerFactory
			.getLogger(CdiAwareJobFactory.class);

	public Job newJob(TriggerFiredBundle bundle, Scheduler Scheduler)
			throws SchedulerException {
		JobDetail jobDetail = bundle.getJobDetail();
		Class<? extends Job> jobClass = jobDetail.getJobClass();

		try {
			if (log.isDebugEnabled()) {
				log.debug("Producing instance of Job '" + jobDetail.getKey()
						+ "', class=" + jobClass.getName());
			}

			for (Class<?> interfaceClass : jobClass.getInterfaces()) {
				if (CdiAwareJob.class.equals(interfaceClass)) {
					BeanManager bm = null;

					try {
						InitialContext initialContext = new InitialContext();
						bm = (BeanManager) initialContext
								.lookup("java:comp/BeanManager");
					} catch (NamingException ex) {
						log.error(
								"BeanManager could not be found! The reason for this can be that the thread which creates the job is not managed by the application server! CDI is disabled for jobs!",
								ex);
					}

					if (bm == null)
						break;
					return CdiUtils.getBean(bm, jobClass);
				}
			}

			return jobClass.newInstance();
		} catch (Exception e) {
			SchedulerException se = new SchedulerException(
					"Problem instantiating class '"
							+ jobDetail.getJobClass().getName() + "'", e);
			throw se;
		}
	}
}
