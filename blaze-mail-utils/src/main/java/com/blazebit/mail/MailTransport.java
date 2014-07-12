/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail;

import com.blazebit.mail.transport.SmtpMailTransport;
import com.blazebit.mail.transport.SmtpsMailTransport;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public interface MailTransport {

	public static final MailTransport SMTP = new SmtpMailTransport();
	public static final MailTransport SMTPS = new SmtpsMailTransport();

	public String getHostProperty();

	public String getPortProperty();

	public String getUserProperty();

	public String getPasswordProperty();

	public String getAuthentificationProperty();

	public Properties getDefaultProperties();

	public Integer getDefaultPort();

	public boolean isSecure();

	public String getProtocol();

	public void addTrustedHost(String host, boolean permanently);

	public void removeTrustedHost(String host);

	public List<String> getTemporaryTrustedHosts();

	public List<String> getTrustedHosts();

	public void setTrustAllHosts(boolean trustAllHosts);

	public boolean isTrustAllHosts();

	public void clearTemporaryTrustedHosts();
}
