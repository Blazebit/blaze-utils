/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz.job;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.quartz.JobDataMap;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public abstract class AbstractJob implements GenericJob {

	private static final long serialVersionUID = 1L;

	protected String getRequiredParam(JobDataMap data, String property) {
		String value = getOptionalParam(data, property);

		if (value == null) {
			throw new IllegalArgumentException(property + " not specified.");
		}

		return value;
	}

	protected String getOptionalParam(JobDataMap data, String property) {
		String value = data.getString(property);

		if ((value != null) && (value.trim().length() == 0)) {
			return null;
		}

		return value;
	}

	@Override
	public List<JobParameter> getParameters() {
		return Collections.emptyList();
	}

	protected Map<String, Object> getUndefinedParameters(JobDataMap data) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<JobParameter> params = getParameters();
		boolean found = false;

		for (Map.Entry<String, Object> entry : data.getWrappedMap().entrySet()) {
			for (JobParameter param : params) {
				if (param.getName().equals(entry.getKey())) {
					found = true;
					break;
				}
			}

			if (found) {
				found = false;
			} else {
				map.put(entry.getKey(), entry.getValue());
			}
		}

		return map;
	}
}
