/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail.impl;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.blazebit.mail.Mail;
import com.blazebit.mail.MailException;
import com.blazebit.mail.MailResource;
import com.blazebit.mail.MailSender;
import com.blazebit.mail.MailTransport;
import com.blazebit.mail.util.MailUtils;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class SimpleMailSender implements MailSender {

	private static final Logger log = Logger.getLogger(SimpleMailSender.class
			.getName());
	private Session session;
	private MailTransport transport;

	public SimpleMailSender(Session session) {
		this.session = session;
	}

	public SimpleMailSender(String host, Integer port, String username,
			String password) {
		this(host, port, username, password, MailTransport.SMTP);
	}

	public SimpleMailSender(String host, Integer port, String username,
			String password, MailTransport transport) {

		if (host == null || host.trim().equals("")) {
			throw new MailException(MailException.MISSING_HOST);
		} else if ((password != null && !password.trim().equals(""))
				&& (username == null || username.trim().equals(""))) {
			throw new MailException(MailException.MISSING_USERNAME);
		}

		this.transport = transport;
		this.session = createMailSession(host, port, username, password);
	}

	public Session createMailSession(String host, Integer port,
			String username, String password) {
		if (transport == null) {
			log.warning("MailTransport not set, using plain SMTP strategy instead!");
			transport = MailTransport.SMTP;
		}

		Properties props = transport.getDefaultProperties();
		props.put(transport.getHostProperty(), host);

		if (port != null) {
			props.put(transport.getPortProperty(), String.valueOf(port));
		} else if (transport.getDefaultPort() != null) {
			props.put(transport.getPortProperty(),
					String.valueOf(transport.getDefaultPort()));
		}

		if (username != null) {
			props.put(transport.getUserProperty(), username);
		}

		if (password != null) {
			props.put(transport.getPasswordProperty(), password);
			props.put(transport.getAuthentificationProperty(), "true");

			return Session.getInstance(props, new SimplePasswordAuthenticator(
					username, password));
		} else {
			return Session.getInstance(props);
		}
	}

	public void setDebug(boolean debug) {
		session.setDebug(debug);
	}

	@Override
	public void sendMail(Mail email) throws MailException {
		sendMail(email, transport);
	}

	@Override
	public void sendMail(Mail email, MailTransport transport)
			throws MailException {
		if (MailUtils.validate(email)) {
			try {
				Message message = prepareMessage(email);
				message.saveChanges();

				Transport t = session.getTransport(transport.getProtocol());
				t.connect();
				t.sendMessage(message, message.getAllRecipients());
				t.close();
			} catch (UnsupportedEncodingException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
				throw new MailException(String.format(
						MailException.INVALID_ENCODING, e.getMessage()));
			} catch (MessagingException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
				throw new MailException(String.format(
						MailException.GENERIC_ERROR, e.getMessage()), e);
			} finally {
				transport.clearTemporaryTrustedHosts();
			}
		}
	}

	private Message prepareMessage(Mail email) throws MessagingException,
			UnsupportedEncodingException {
		MimeMultipart multipartRoot = new MimeMultipart("mixed");
		MimeBodyPart contentRelated = new MimeBodyPart();
		MimeMultipart multipartRelated = new MimeMultipart("related");
		MimeBodyPart contentAlternativeMessages = new MimeBodyPart();
		MimeMultipart multipartAlternativeMessages = new MimeMultipart(
				"alternative");

		multipartRoot.addBodyPart(contentRelated);
		contentRelated.setContent(multipartRelated);

		multipartRelated.addBodyPart(contentAlternativeMessages);
		contentAlternativeMessages.setContent(multipartAlternativeMessages);

		Message message = new MimeMessage(session);
		message.setSubject(email.getSubject());
		message.setFrom(email.getFrom());

		setReplyTo(email, message);
		setRecipients(email, message);
		setTexts(email, multipartAlternativeMessages);

		setEmbeddedImages(email, multipartRelated);
		setAttachments(email, multipartRoot);

		message.setContent(multipartRoot);
		setHeaders(email, message);
		message.setSentDate(new Date());

		return message;
	}

	private void setRecipients(Mail email, Message message)
			throws UnsupportedEncodingException, MessagingException {
		for (InternetAddress recipient : email.getTo()) {
			message.addRecipient(RecipientType.TO, recipient);
		}
		for (InternetAddress recipient : email.getBcc()) {
			message.addRecipient(RecipientType.BCC, recipient);
		}
		for (InternetAddress recipient : email.getCc()) {
			message.addRecipient(RecipientType.CC, recipient);
		}
	}

	private void setReplyTo(Mail email, Message message)
			throws UnsupportedEncodingException, MessagingException {
		InternetAddress replyToRecipient = email.getReplyTo();

		if (replyToRecipient != null) {
			message.setReplyTo(new Address[] { replyToRecipient });
		}
	}

	private void setTexts(Mail email, MimeMultipart multipartAlternativeMessages)
			throws MessagingException {
		if (email.getText() != null) {
			MimeBodyPart messagePart = new MimeBodyPart();
			messagePart.setText(email.getText(), "UTF-8");
			multipartAlternativeMessages.addBodyPart(messagePart);
		}

		if (email.getHtml() != null) {
			MimeBodyPart messagePartHTML = new MimeBodyPart();
			messagePartHTML.setContent(email.getHtml(),
					"text/html; charset=\"UTF-8\"");
			multipartAlternativeMessages.addBodyPart(messagePartHTML);
		}
	}

	private void setEmbeddedImages(Mail email, MimeMultipart multipartRelated)
			throws MessagingException {
		for (MailResource embeddedImage : email.getEmbeddedImages()) {
			multipartRelated.addBodyPart(getBodyPartFromDatasource(
					embeddedImage, Part.INLINE));
		}
	}

	private void setAttachments(Mail email, MimeMultipart multipartRoot)
			throws MessagingException {
		for (MailResource resource : email.getAttachments()) {
			multipartRoot.addBodyPart(getBodyPartFromDatasource(resource,
					Part.ATTACHMENT));
		}
	}

	private void setHeaders(Mail email, Message message)
			throws UnsupportedEncodingException, MessagingException {
		for (Map.Entry<String, String> header : email.getHeaders().entrySet()) {
			String headerName = header.getKey();
			String headerValue = MimeUtility.encodeText(header.getValue(),
					"UTF-8", null);
			String foldedHeaderValue = MimeUtility.fold(
					headerName.length() + 2, headerValue);
			message.addHeader(header.getKey(), foldedHeaderValue);
		}
	}

	private BodyPart getBodyPartFromDatasource(MailResource resource,
			String dispositionType) throws MessagingException {
		BodyPart attachmentPart = new MimeBodyPart();
		DataSource ds = resource.getDataSource();

		attachmentPart
				.setDataHandler(new DataHandler(resource.getDataSource()));
		attachmentPart.setFileName(resource.getName());
		attachmentPart.setHeader("Content-Type", ds.getContentType()
				+ "; filename=" + ds.getName() + "; name=" + ds.getName());
		attachmentPart.setHeader("Content-ID",
				String.format("<%s>", ds.getName()));
		attachmentPart.setDisposition(dispositionType + "; size=0");
		return attachmentPart;
	}
}
