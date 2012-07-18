/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.quartz.SchedulerConfigException;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class PropertiesUtil {

	public static Properties getProperties() throws SchedulerException {
		String requestedFile = System
				.getProperty(StdSchedulerFactory.PROPERTIES_FILE);
		String propFileName = requestedFile != null ? requestedFile
				: "quartz.properties";
		File propFile = new File(propFileName);

		Properties props = new Properties();

		InputStream in = null;

		try {
			if (propFile.exists()) {
				try {
					in = new BufferedInputStream(new FileInputStream(
							propFileName));
					props.load(in);

				} catch (IOException ioe) {
					throw new SchedulerException("Properties file: '"
							+ propFileName + "' could not be read.", ioe);
				}
			} else if (requestedFile != null) {
				in = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(requestedFile);

				if (in == null) {
					throw new SchedulerException("Properties file: '"
							+ requestedFile + "' could not be found.");
				}

				in = new BufferedInputStream(in);
				try {
					props.load(in);
				} catch (IOException ioe) {
					throw new SchedulerException("Properties file: '"
							+ requestedFile + "' could not be read.", ioe);
				}

			} else {
				ClassLoader cl = PropertiesUtil.class.getClassLoader();

				if (cl == null) {
					cl = findClassloader();
				}
				if (cl == null) {
					throw new SchedulerConfigException(
							"Unable to find a class loader on the current thread or class.");
				}

				in = cl.getResourceAsStream("quartz.properties");

				if (in == null) {
					in = cl.getResourceAsStream("/quartz.properties");
				}
				if (in == null) {
					in = cl.getResourceAsStream("org/quartz/quartz.properties");
				}
				if (in == null) {
					throw new SchedulerException(
							"Default quartz.properties not found in class path");
				}
				try {
					props.load(in);
				} catch (IOException ioe) {
					throw new SchedulerException(
							"Resource properties file: 'org/quartz/quartz.properties' could not be read from the classpath.",
							ioe);
				}
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignore) { /* ignore */
				}
			}
		}

		return props;
	}

	private static ClassLoader findClassloader() {
		// work-around set context loader for windows-service started jvms
		// (QUARTZ-748)
		if (Thread.currentThread().getContextClassLoader() == null
				&& PropertiesUtil.class.getClassLoader() != null) {
			Thread.currentThread().setContextClassLoader(
					PropertiesUtil.class.getClassLoader());
		}
		return Thread.currentThread().getContextClassLoader();
	}
}
