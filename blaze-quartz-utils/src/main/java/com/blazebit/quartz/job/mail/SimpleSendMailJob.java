/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz.job.mail;

import com.blazebit.quartz.job.CdiAwareJob;
import com.blazebit.quartz.job.JobParameter;
import java.util.ArrayList;
import java.util.List;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class SimpleSendMailJob extends AbstractSendMailJob implements
		CdiAwareJob {

	private static final long serialVersionUID = 1L;

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		sendMail(
				jec.getMergedJobDataMap(),
				createMail(jec.getMergedJobDataMap(),
						getRequiredParam(jec.getMergedJobDataMap(), "to")
								.split(","),
						getRequiredParam(jec.getMergedJobDataMap(), "subject"),
						getRequiredParam(jec.getMergedJobDataMap(), "text"),
						getOptionalParam(jec.getMergedJobDataMap(), "html")));
	}

	@Override
	public List<JobParameter> getParameters() {
		List<JobParameter> l = new ArrayList<JobParameter>(
				super.getParameters());
		l.add(new JobParameter("to", true, String.class));
		l.add(new JobParameter("subject", true, String.class));
		l.add(new JobParameter("text", true, String.class));
		// optional
		l.add(new JobParameter("html", false, String.class));
		return l;
	}
}
