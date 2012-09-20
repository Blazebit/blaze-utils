/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz.job.mail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazebit.mail.Mail;
import com.blazebit.mail.util.MailUtils;
import com.blazebit.quartz.job.AbstractJob;
import com.blazebit.quartz.job.JobParameter;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public abstract class AbstractSendMailJob extends AbstractJob {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(AbstractSendMailJob.class);

	protected Mail createMail(JobDataMap dataMap, String[] to, String subject,
			String text, String html) {
		return MailUtils.createMessage(getRequiredParam(dataMap, "from"), to,
				subject, text, html);
	}

	protected Mail createMail(JobDataMap dataMap, String[] to, String subject,
			String text, String html, File[] files) throws IOException {
		return MailUtils.createMessage(getRequiredParam(dataMap, "from"), to,
				subject, text, html, files);
	}

	protected void sendMail(JobDataMap dataMap, Mail m)
			throws JobExecutionException {
		try {
			MailUtils.sendMessage(getRequiredParam(dataMap, "host"),
					Integer.valueOf(getRequiredParam(dataMap, "port")),
					getRequiredParam(dataMap, "user"),
					getRequiredParam(dataMap, "password"),
					dataMap.getBoolean("trustAllCertificates"),
					dataMap.getBoolean("secure"), m);
		} catch (MessagingException ex) {
			log.error(null, ex);
			throw new JobExecutionException(ex, false);
		}
	}

	@Override
	public List<JobParameter> getParameters() {
		List<JobParameter> l = new ArrayList<JobParameter>(
				super.getParameters());
		l.add(new JobParameter("host", true, String.class));
		l.add(new JobParameter("port", true, Integer.class));
		l.add(new JobParameter("user", true, String.class));
		l.add(new JobParameter("password", true, String.class));
		l.add(new JobParameter("from", true, String.class));
		// optional
		l.add(new JobParameter("trustAllCertificates", false, Boolean.class));
		l.add(new JobParameter("secure", false, Boolean.class));
		return l;
	}
}
