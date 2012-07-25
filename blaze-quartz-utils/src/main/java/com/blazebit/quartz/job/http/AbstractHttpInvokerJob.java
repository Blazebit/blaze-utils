/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blazebit.quartz.job.http;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.blazebit.quartz.job.AbstractJob;
import com.blazebit.quartz.job.JobParameter;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public abstract class AbstractHttpInvokerJob extends AbstractJob {

	private static final long serialVersionUID = 1L;

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();
		String url = getRequiredParam(map, "url");
		String urlSuffix = getOptionalParam(map, "urlSuffix");
		String parameterEncoding = "UTF-8";
		StringBuilder urlBuilder = new StringBuilder(url);

		if (urlSuffix != null) {
			if (!url.endsWith("/") && !urlSuffix.startsWith("/")) {
				urlBuilder.append("/");
			}

			urlBuilder.append(urlSuffix);
		}

		URLConnection con = null;
		InputStream is = null;

		try {

			/*
			 * Build up the post parameter string for the request Only use
			 * parameters that are not explicitly defined for this job
			 */
			StringBuilder data = new StringBuilder();

			for (Map.Entry<String, Object> entry : getUndefinedParameters(map)
					.entrySet()) {
				if (data.length() != 0) {
					data.append("&");
				}

				data.append(URLEncoder.encode(entry.getKey(), parameterEncoding));
				data.append("=");
				data.append(URLEncoder.encode(entry.getValue().toString(),
						parameterEncoding));
			}

			con = createConnection(urlBuilder.toString(), data.toString());

			/*
			 * Read the response from the request
			 */

			con.connect();
			is = con.getInputStream();

			/*
			 * Handle the response of the request, default is print to
			 * System.out
			 */

			handleResponse(is);
		} catch (MalformedURLException ex) {
			throw new JobExecutionException("Invalid URL given", ex, false);
		} catch (FileNotFoundException ex) {
			throw new JobExecutionException("URL can not be reached", ex, false);
		} catch (IOException ex) {
			throw new JobExecutionException(
					"Error occured during the invocation of the URL", ex, false);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					// Ignore
				}
			}
		}
	}

	@Override
	public List<JobParameter> getParameters() {
		List<JobParameter> params = new ArrayList<JobParameter>(
				super.getParameters());
		params.add(new JobParameter("url", true, String.class));
		params.add(new JobParameter("urlSuffix", false, String.class));
		return params;
	}

	protected abstract URLConnection createConnection(String url,
			String parameterString) throws IOException;

	protected void handleResponse(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;

		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}

}
