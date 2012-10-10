/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail.util;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.blazebit.mail.Mail;
import com.blazebit.mail.MailException;
import com.blazebit.mail.MailSender;
import com.blazebit.mail.MailTransport;
import com.blazebit.mail.impl.SimpleMailSender;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class MailUtils {

	public static boolean validate(Mail email) throws MailException {
		if (email.getText() == null && email.getHtml() == null) {
			throw new MailException(MailException.MISSING_CONTENT);
		} else if (email.getSubject() == null || email.getSubject().equals("")) {
			throw new MailException(MailException.MISSING_SUBJECT);
		} else if (email.getTo().isEmpty() && email.getBcc().isEmpty()
				&& email.getCc().isEmpty()) {
			throw new MailException(MailException.MISSING_RECIPIENT);
		} else if (email.getFrom() == null) {
			throw new MailException(MailException.MISSING_SENDER);
		} else {
			String exceptionMessage = null;

			try {
				exceptionMessage = MailException.INVALID_SENDER;
				email.getFrom().validate();

				if(email.getReplyTo() != null){
					exceptionMessage = MailException.INVALID_REPLYTO;
					email.getReplyTo().validate();
				}

				exceptionMessage = MailException.INVALID_TO;

				for (InternetAddress a : email.getTo()) {
					a.validate();
				}

				exceptionMessage = MailException.INVALID_BCC;

				for (InternetAddress a : email.getBcc()) {
					a.validate();
				}

				exceptionMessage = MailException.INVALID_CC;

				for (InternetAddress a : email.getCc()) {
					a.validate();
				}
			} catch (AddressException ex) {
				throw new MailException(String.format(exceptionMessage, email),
						ex);
			}
		}

		return true;
	}

	public static void sendMessage(String host, Integer port,
			boolean trustAllCertificates, boolean secure, String from,
			String[] to, String subject, String text) throws MessagingException {
		sendMessage(host, port, null, null, trustAllCertificates, secure, from,
				to, subject, text);
	}

	public static void sendMessage(String host, Integer port, String user,
			String password, boolean trustAllCertificates, boolean secure,
			String from, String[] to, String subject, String text)
			throws MessagingException {
		sendMessage(host, port, user, password, trustAllCertificates, secure,
				from, to, subject, text, null);
	}

	public static void sendMessage(String host, Integer port, String user,
			String password, boolean trustAllCertificates, boolean secure,
			String from, String[] to, String subject, String text, String html)
			throws MessagingException {
		sendMessage(host, port, user, password, trustAllCertificates, secure,
				createMessage(from, to, subject, text, html));
	}

	public static void sendMessage(String host, Integer port, String user,
			String password, boolean trustAllCertificates, boolean secure,
			String from, String[] to, String subject, String text, String html,
			File[] attachments) throws MessagingException, IOException {
		sendMessage(host, port, user, password, trustAllCertificates, secure,
				createMessage(from, to, subject, text, html, attachments));
	}

	public static void sendMessage(String host, Integer port, String user,
			String password, boolean trustAllCertificates, boolean secure,
			Mail m) throws MessagingException {

		MailTransport t = secure ? MailTransport.SMTPS : MailTransport.SMTP;
		t.setTrustAllHosts(trustAllCertificates);
		sendMessage(host, port, user, password, t, m);
	}

	public static void sendMessage(String host, Integer port, String user,
			String password, MailTransport t,
			Mail m) throws MessagingException {

		MailSender sender = new SimpleMailSender(host, port, user, password, t);
		sender.sendMail(m);
	}

	public static Mail createMessage(String from, String[] to, String subject,
			String text, String html) {

		Mail m = new Mail();
		m.setFrom(from);
		m.addTo(to);
		m.setSubject(subject);
		m.setText(text);
		m.setHtml(html);
		return m;
	}

	public static Mail createMessage(String from, String[] to, String subject,
			String text, String html, File[] attachments) throws IOException {

		Mail m = createMessage(from, to, subject, text, html);
		m.addAttachment(attachments);
		return m;
	}
}
