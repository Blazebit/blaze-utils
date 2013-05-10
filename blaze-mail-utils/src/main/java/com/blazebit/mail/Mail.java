/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class Mail {

	private static final String STREAM_MIME_TYPE = "application/octet-stream";

	private InternetAddress from;
	private InternetAddress replyTo;
	private List<InternetAddress> to;
	private List<InternetAddress> cc;
	private List<InternetAddress> bcc;
	private String subject;
	private String text;
	private String html;
	private List<MailResource> embeddedImages;
	private List<MailResource> attachments;
	private Map<String, String> headers;

	public List<InternetAddress> getBcc() {
		if (bcc == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(bcc);
	}

	public void setBcc(List<InternetAddress> bcc) {
		this.bcc = new ArrayList<InternetAddress>(bcc);
	}

	public void setBcc(String bcc) {
		try {
			this.bcc = Arrays.asList(InternetAddress.parse(bcc));
		} catch (AddressException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public void addBcc(InternetAddress address) {
		if (bcc == null) {
			bcc = new ArrayList<InternetAddress>();
		}

		bcc.add(address);
	}

	public void addBcc(String address) {
		addBcc(address, null);
	}

	public void addBcc(String[] addresses) {
		for (String address : addresses) {
			addBcc(address, null);
		}
	}

	public void addBcc(String address, String name) {
		addBcc(create(address, name));
	}

	public List<InternetAddress> getCc() {
		if (cc == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(cc);
	}

	public void setCc(List<InternetAddress> cc) {
		this.cc = new ArrayList<InternetAddress>(cc);
	}

	public void setCc(String cc) {
		try {
			this.cc = Arrays.asList(InternetAddress.parse(cc));
		} catch (AddressException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public void addCc(InternetAddress address) {
		if (cc == null) {
			cc = new ArrayList<InternetAddress>();
		}

		cc.add(address);
	}

	public void addCc(String address) {
		addCc(address, null);
	}

	public void addCc(String[] addresses) {
		for (String address : addresses) {
			addCc(address, null);
		}
	}

	public void addCc(String address, String name) {
		addCc(create(address, name));
	}

	public List<InternetAddress> getTo() {
		if (to == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(to);
	}

	public void setTo(List<InternetAddress> to) {
		this.to = new ArrayList<InternetAddress>(to);
	}

	public void setTo(String to) {
		try {
			this.to = Arrays.asList(InternetAddress.parse(to));
		} catch (AddressException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public void addTo(InternetAddress address) {
		if (to == null) {
			to = new ArrayList<InternetAddress>();
		}

		to.add(address);
	}

	public void addTo(String address) {
		addTo(address, null);
	}

	public void addTo(String[] addresses) {
		for (String address : addresses) {
			addTo(address, null);
		}
	}

	public void addTo(String address, String name) {
		addTo(create(address, name));
	}

	public InternetAddress getFrom() {
		return from;
	}

	public void setFrom(String address) {
		setFrom(address, null);
	}

	public void setFrom(String address, String name) {
		setFrom(create(address, name));
	}

	public void setFrom(InternetAddress from) {
		this.from = from;
	}

	public InternetAddress getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String address) {
		setReplyTo(address, null);
	}

	public void setReplyTo(String address, String name) {
		setReplyTo(create(address, name));
	}

	public void setReplyTo(InternetAddress replyTo) {
		this.replyTo = replyTo;
	}

	private static InternetAddress create(String address, String name) {
		String addressString;

		if (address == null || address.isEmpty()
				|| (addressString = address.trim()).isEmpty()) {
			throw new IllegalArgumentException("Empty or no address");
		}

		try {
			if (name == null) {
				return new InternetAddress(address);
			}

			if (addressString.indexOf('<') == -1) {
				addressString = new StringBuilder(addressString.length()
						+ name.length() + 2).append(name).append('<')
						.append(addressString).append('>').toString();
			} else {
				addressString = name + addressString;
			}

			return new InternetAddress(addressString);
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Map<String, String> getHeaders() {
		if (headers == null) {
			return Collections.emptyMap();
		}
		return Collections.unmodifiableMap(headers);
	}

	public void addHeader(String name, Object value) {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}

		headers.put(name, String.valueOf(value));
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public List<MailResource> getEmbeddedImages() {
		if (embeddedImages == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(embeddedImages);
	}

	public void addEmbeddedImage(File f) throws FileNotFoundException,
			IOException {
		addEmbeddedImage(f.getName(), new FileInputStream(f));
	}

	public void addEmbeddedImages(File[] files) throws FileNotFoundException,
			IOException {
		for (File f : files) {
			addEmbeddedImage(f.getName(), new FileInputStream(f));
		}
	}

	public void addEmbeddedImage(String fileName, File f)
			throws FileNotFoundException, IOException {
		addEmbeddedImage(fileName, new FileInputStream(f));
	}

	public void addEmbeddedImage(File f, String mimeType)
			throws FileNotFoundException, IOException {
		addEmbeddedImage(f.getName(), new FileInputStream(f), mimeType);
	}

	public void addEmbeddedImage(String fileName, File f, String mimeType)
			throws FileNotFoundException, IOException {
		addEmbeddedImage(fileName, new FileInputStream(f), mimeType);
	}

	public void addEmbeddedImage(String fileName, InputStream f)
			throws IOException {
		addEmbeddedImage(fileName, new InputStreamDataSource(fileName,
				STREAM_MIME_TYPE, f));
	}

	public void addEmbeddedImage(String fileName, InputStream f, String mimeType)
			throws IOException {
		addEmbeddedImage(fileName, new InputStreamDataSource(fileName,
				mimeType, f));
	}

	public void addEmbeddedImage(String fileName, byte[] f) {
		addEmbeddedImage(fileName, f, STREAM_MIME_TYPE);
	}

	public void addEmbeddedImage(String fileName, byte[] f, String mimeType) {
		ByteArrayDataSource ds = new ByteArrayDataSource(f, mimeType);
		ds.setName(fileName);
		addEmbeddedImage(fileName, ds);
	}

	private void addEmbeddedImage(String fileName, DataSource ds) {
		if (embeddedImages == null) {
			embeddedImages = new ArrayList<MailResource>();
		}

		embeddedImages.add(new MailResource(fileName, ds));
	}

	public List<MailResource> getAttachments() {
		if (attachments == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(attachments);
	}

	public void addAttachment(File f) throws FileNotFoundException, IOException {
		addAttachment(f.getName(), new FileInputStream(f));
	}

	public void addAttachment(File[] files) throws FileNotFoundException,
			IOException {
		for (File f : files) {
			addAttachment(f.getName(), new FileInputStream(f));
		}
	}

	public void addAttachment(String fileName, File f)
			throws FileNotFoundException, IOException {
		addAttachment(fileName, new FileInputStream(f));
	}

	public void addAttachment(File f, String mimeType)
			throws FileNotFoundException, IOException {
		addAttachment(f.getName(), new FileInputStream(f), mimeType);
	}

	public void addAttachment(String fileName, File f, String mimeType)
			throws FileNotFoundException, IOException {
		addAttachment(fileName, new FileInputStream(f), mimeType);
	}

	public void addAttachment(String fileName, InputStream f)
			throws IOException {
		addAttachment(fileName, new InputStreamDataSource(fileName,
				STREAM_MIME_TYPE, f));
	}

	public void addAttachment(String fileName, InputStream f, String mimeType)
			throws IOException {
		addAttachment(fileName,
				new InputStreamDataSource(fileName, mimeType, f));
	}

	public void addAttachment(String fileName, byte[] f) {
		addAttachment(fileName, f, STREAM_MIME_TYPE);
	}

	public void addAttachment(String fileName, byte[] f, String mimeType) {
		ByteArrayDataSource ds = new ByteArrayDataSource(f, mimeType);
		ds.setName(fileName);
		addAttachment(fileName, ds);
	}

	private void addAttachment(String fileName, DataSource ds) {
		if (attachments == null) {
			attachments = new ArrayList<MailResource>();
		}

		attachments.add(new MailResource(fileName, ds));
	}

	private static class InputStreamDataSource implements DataSource {

		private String name;
		private String contentType;
		private InputStream is;

		public InputStreamDataSource(String name, String contentType,
				InputStream is) {
			this.name = name;
			this.contentType = contentType;
			this.is = is;
		}

		@Override
		public String getContentType() {
			return contentType;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return is;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			return null;
		}

	}
}
