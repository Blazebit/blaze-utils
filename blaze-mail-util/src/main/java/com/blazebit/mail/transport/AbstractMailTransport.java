/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail.transport;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.blazebit.mail.MailTransport;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public abstract class AbstractMailTransport implements MailTransport {

	protected String protocolProperty = "mail.transport.protocol";
	protected String hostProperty = new StringBuilder("mail.")
			.append(getProtocol()).append(".host").toString();
	protected String portProperty = new StringBuilder("mail.")
			.append(getProtocol()).append(".port").toString();
	protected String authentificationProperty = new StringBuilder("mail.")
			.append(getProtocol()).append(".auth").toString();
	protected String userProperty = new StringBuilder("mail.")
			.append(getProtocol()).append(".username").toString();
	protected String passwordProperty = new StringBuilder("mail.")
			.append(getProtocol()).append(".password").toString();

	@Override
	public String getHostProperty() {
		return hostProperty;
	}

	@Override
	public String getPortProperty() {
		return portProperty;
	}

	@Override
	public String getAuthentificationProperty() {
		return authentificationProperty;
	}

	@Override
	public String getUserProperty() {
		return userProperty;
	}

	@Override
	public String getPasswordProperty() {
		return passwordProperty;
	}

	@Override
	public void addTrustedHost(String host, boolean permanently) {
		// Noop
	}

	@Override
	public void removeTrustedHost(String host) {
		// Noop
	}

	@Override
	public void clearTemporaryTrustedHosts() {
		// Noop
	}

	@Override
	public List<String> getTemporaryTrustedHosts() {
		return Collections.emptyList();
	}

	@Override
	public List<String> getTrustedHosts() {
		return Collections.emptyList();
	}

	@Override
	public void setTrustAllHosts(boolean trustAllHosts) {
		// Noop
	}

	@Override
	public boolean isTrustAllHosts() {
		return false;
	}

	@Override
	public Properties getDefaultProperties() {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", getProtocol());
		return props;
	}

}
