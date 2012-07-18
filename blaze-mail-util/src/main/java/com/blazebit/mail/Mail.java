/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.Message.RecipientType;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class Mail {

    private Recipient from;
    private Recipient replyTo;
    private List<Recipient> recipients = new ArrayList<Recipient>();
    private String subject;
    private String text;
    private String html;
    private final List<MailResource> embeddedImages = new ArrayList<MailResource>();
    private final List<MailResource> attachments = new ArrayList<MailResource>();
    private final Map<String, String> headers = new HashMap<String, String>();

    public List<Recipient> getBcc() {
        return getRecipients(RecipientType.BCC);
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
        addRecipient(address, name, RecipientType.BCC);
    }

    public List<Recipient> getCc() {
        return getRecipients(RecipientType.CC);
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
        addRecipient(address, name, RecipientType.CC);
    }

    public List<Recipient> getTo() {
        return getRecipients(RecipientType.TO);
    }

    public void addTo(String address) {
        addCc(address, null);
    }

    public void addTo(String[] addresses) {
        for (String address : addresses) {
            addCc(address, null);
        }
    }

    public void addTo(String address, String name) {
        addRecipient(address, name, RecipientType.TO);
    }

    private void addRecipient(String address, String name, RecipientType type) {
        this.recipients.add(new Recipient(name, address, type));
    }

    private List<Recipient> getRecipients(RecipientType type) {
        List<Recipient> ret = new ArrayList<Recipient>();

        for (Recipient r : recipients) {
            if (r.getType().equals(type)) {
                ret.add(r);
            }
        }

        return Collections.unmodifiableList(ret);
    }

    public List<Recipient> getRecipients() {
        return Collections.unmodifiableList(recipients);
    }

    public Recipient getFrom() {
        return from;
    }

    public void setFrom(String address) {
        setFrom(address, null);
    }

    public void setFrom(String address, String name) {
        this.from = new Recipient(name, address, null);
    }

    public Recipient getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String address) {
        setReplyTo(address, null);
    }

    public void setReplyTo(String address, String name) {
        this.replyTo = new Recipient(name, address, null);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public void addHeader(String name, Object value) {
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
        return Collections.unmodifiableList(embeddedImages);
    }

    public void addEmbeddedImage(File f) throws FileNotFoundException, IOException {
        addEmbeddedImage(f.getName(), new FileInputStream(f));
    }

    public void addEmbeddedImages(File[] files) throws FileNotFoundException, IOException {
        for (File f : files) {
            addEmbeddedImage(f.getName(), new FileInputStream(f));
        }
    }

    public void addEmbeddedImage(String fileName, File f) throws FileNotFoundException, IOException {
        addEmbeddedImage(fileName, new FileInputStream(f));
    }

    public void addEmbeddedImage(File f, String mimeType) throws FileNotFoundException, IOException {
        addEmbeddedImage(f.getName(), new FileInputStream(f), mimeType);
    }

    public void addEmbeddedImage(String fileName, File f, String mimeType) throws FileNotFoundException, IOException {
        addEmbeddedImage(fileName, new FileInputStream(f), mimeType);
    }

    public void addEmbeddedImage(String fileName, InputStream f) throws IOException {
        addEmbeddedImage(fileName, IOUtils.toByteArray(f));
    }

    public void addEmbeddedImage(String fileName, InputStream f, String mimeType) throws IOException {
        addEmbeddedImage(fileName, IOUtils.toByteArray(f), mimeType);
    }

    public void addEmbeddedImage(String fileName, byte[] f) {
        addEmbeddedImage(fileName, f, "application/octet-stream");
    }

    public void addEmbeddedImage(String fileName, byte[] f, String mimeType) {
        ByteArrayDataSource ds = new ByteArrayDataSource(f, mimeType);
        ds.setName(fileName);
        embeddedImages.add(new MailResource(fileName, ds));
    }

    public List<MailResource> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public void addAttachment(File f) throws FileNotFoundException, IOException {
        addAttachment(f.getName(), new FileInputStream(f));
    }

    public void addAttachment(File[] files) throws FileNotFoundException, IOException {
        for (File f : files) {
            addAttachment(f.getName(), new FileInputStream(f));
        }
    }

    public void addAttachment(String fileName, File f) throws FileNotFoundException, IOException {
        addAttachment(fileName, new FileInputStream(f));
    }

    public void addAttachment(File f, String mimeType) throws FileNotFoundException, IOException {
        addAttachment(f.getName(), new FileInputStream(f), mimeType);
    }

    public void addAttachment(String fileName, File f, String mimeType) throws FileNotFoundException, IOException {
        addAttachment(fileName, new FileInputStream(f), mimeType);
    }

    public void addAttachment(String fileName, InputStream f) throws IOException {
        addAttachment(fileName, IOUtils.toByteArray(f));
    }

    public void addAttachment(String fileName, InputStream f, String mimeType) throws IOException {
        addAttachment(fileName, IOUtils.toByteArray(f), mimeType);
    }

    public void addAttachment(String fileName, byte[] f) {
        addAttachment(fileName, f, "application/octet-stream");
    }

    public void addAttachment(String fileName, byte[] f, String mimeType) {
        ByteArrayDataSource ds = new ByteArrayDataSource(f, mimeType);
        ds.setName(fileName);
        attachments.add(new MailResource(fileName, ds));
    }
}
