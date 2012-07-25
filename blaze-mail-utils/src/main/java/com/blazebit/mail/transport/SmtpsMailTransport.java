/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail.transport;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class SmtpsMailTransport extends SmtpMailTransport {

	private MailSSLSocketFactory fact;

	public SmtpsMailTransport() {
		try {
			fact = new MailSSLSocketFactory();
		} catch (GeneralSecurityException ex) {
			throw new IllegalArgumentException(
					"Could not initialize socketFactory!", ex);
		}
	}

	@Override
	public Integer getDefaultPort() {
		return Integer.valueOf(465);
	}

	@Override
	public boolean isSecure() {
		return true;
	}

	@Override
	public void addTrustedHost(String host, boolean permanently) {
		fact.addTrustedHost(host, permanently);
	}

	@Override
	public void removeTrustedHost(String host) {
		fact.removeTrustedHost(host);
	}

	@Override
	public void clearTemporaryTrustedHosts() {
		fact.clearTemporaryTrustedHosts();
	}

	@Override
	public List<String> getTemporaryTrustedHosts() {
		return fact.getTemporaryTrustedHosts();
	}

	@Override
	public List<String> getTrustedHosts() {
		return Collections.unmodifiableList(Arrays.asList(fact
				.getTrustedHosts()));
	}

	@Override
	public void setTrustAllHosts(boolean trustAllHosts) {
		fact.setTrustAllHosts(trustAllHosts);
	}

	@Override
	public boolean isTrustAllHosts() {
		return fact.isTrustAllHosts();
	}

	@Override
	public Properties getDefaultProperties() {
		Properties props = super.getDefaultProperties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.socketFactory", fact);
		props.put("mail.smtp.ssl.socketFactory", fact);
		return props;
	}
}
