/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.web.mail.bean;

import java.io.File;
import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import com.blazebit.mail.Mail;
import com.blazebit.mail.MailTransport;
import com.blazebit.mail.impl.SimpleMailSender;

/**
 * 
 * @author Christian Beikov
 */
@Named
@RequestScoped
public class MailBean {

	// private static final Logger log =
	// LoggerFactory.getLogger(MailBean.class);
	private String from = "christian@blazebit.com";
	private String to = "christian@blazebit.com";
	private String subject = "test";
	private String text = "Testtext";

	public String send() throws IOException {
		Mail m = new Mail();
		m.setFrom(from);
		m.addTo(to);
		m.setSubject(subject);
		m.setText(text);
		m.setHtml("<p>" + text + "</p><br/><img src=\"cid:MyAvatar\">");
		m.addEmbeddedImage("MyAvatar",
				new File("D:/Eigene Bilder/avatar1.jpg"), "image/jpeg");
		m.addAttachment("YourAvatar", new File("D:/Eigene Bilder/avatar.jpg"),
				"image/jpeg");

		MailTransport mt = MailTransport.SMTPS;
		mt.addTrustedHost("web118.ip-projects.de", true);
		mt.addTrustedHost("blazebit.com", true);

		new SimpleMailSender("smtp.blazebit.com", 465,
				"christian@blazebit.com", "aA159732684!", mt).sendMail(m);
		return "";
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

}
