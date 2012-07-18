/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail;

import com.blazebit.mail.impl.SimpleMailSender;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.mail.MessagingException;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class MailUtil {
    
    private static final Logger log = Logger.getLogger(MailUtil.class.getName());
    private static final Pattern mailPattern;
    
    static {
        // RFC 2822 2.2.2 Structured Header Field Bodies
        final String wsp = "[ \\t]"; // space or tab
        final String fwsp = wsp + "*";
        // RFC 2822 3.2.1 Primitive tokens
        final String dquote = "\\\"";
        // ASCII Control characters excluding white space:
        final String noWsCtl = "\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F";
        // all ASCII characters except CR and LF:
        final String asciiText = "[\\x01-\\x09\\x0B\\x0C\\x0E-\\x7F]";
        // RFC 2822 3.2.2 Quoted characters:
        // single backslash followed by a text char
        final String quotedPair = "(\\\\" + asciiText + ")";
        // RFC 2822 3.2.4 Atom:
        final String atext = "[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]";
        final String atom = fwsp + atext + "+" + fwsp;
        final String dotAtomText = atext + "+" + "(" + "\\." + atext + "+)*";
        final String dotAtom = fwsp + "(" + dotAtomText + ")" + fwsp;
        // RFC 2822 3.2.5 Quoted strings:
        // noWsCtl and the rest of ASCII except the doublequote and backslash characters:
        final String qtext = "[" + noWsCtl + "\\x21\\x23-\\x5B\\x5D-\\x7E]";
        final String qcontent = "(" + qtext + "|" + quotedPair + ")";
        final String quotedString = dquote + "(" + fwsp + qcontent + ")*" + fwsp + dquote;
        // RFC 2822 3.2.6 Miscellaneous tokens
        final String word = "((" + atom + ")|(" + quotedString + "))";
        final String phrase = word + "+"; // one or more words.
        // RFC 2822 3.4 Address specification
        // domain text - non white space controls and the rest of ASCII chars not including [, ], or \:
        final String dtext = "[" + noWsCtl + "\\x21-\\x5A\\x5E-\\x7E]";
        final String dcontent = dtext + "|" + quotedPair;
        final String domainLiteral = "\\[" + "(" + fwsp + dcontent + "+)*" + fwsp + "\\]";
        final String rfc2822Domain = "(" + dotAtom + "|" + domainLiteral + ")";
        final String localPart = "((" + dotAtom + ")|(" + quotedString + "))";
        final String addrSpec = localPart + "@" + rfc2822Domain;
        final String angleAddr = "<" + addrSpec + ">";
        final String nameAddr = "(" + phrase + ")?" + fwsp + angleAddr;
        final String pattern = nameAddr + "|" + addrSpec;
        
        mailPattern = Pattern.compile(pattern);
    }
    
    public static boolean validate(final Mail email) throws MailException {
        if (email.getText() == null && email.getHtml() == null) {
            throw new MailException(MailException.MISSING_CONTENT);
        } else if (email.getSubject() == null || email.getSubject().equals("")) {
            throw new MailException(MailException.MISSING_SUBJECT);
        } else if (email.getRecipients().isEmpty()) {
            throw new MailException(MailException.MISSING_RECIPIENT);
        } else if (email.getFrom() == null) {
            throw new MailException(MailException.MISSING_SENDER);
        } else {
            if (!mailPattern.matcher(email.getFrom().getAddress()).matches()) {
                throw new MailException(String.format(MailException.INVALID_SENDER, email));
            }
            for (final Recipient recipient : email.getRecipients()) {
                if (!mailPattern.matcher(recipient.getAddress()).matches()) {
                    throw new MailException(String.format(MailException.INVALID_RECIPIENT, email));
                }
            }
            if (email.getReplyTo() != null) {
                if (!mailPattern.matcher(email.getReplyTo().getAddress()).matches()) {
                    throw new MailException(String.format(MailException.INVALID_REPLYTO, email));
                }
            }
        }
        return true;
    }
    
    public static void sendMessage(
            final String host,
            final Integer port,
            final boolean trustAllCertificates,
            final boolean secure,
            final String from,
            final String[] to,
            final String subject,
            final String text) throws MessagingException {
        sendMessage(host, port, null, null, trustAllCertificates, secure, from, to, subject, text);
    }
    
    public static void sendMessage(
            final String host,
            final Integer port,
            final String user,
            final String password,
            final boolean trustAllCertificates,
            final boolean secure,
            final String from,
            final String[] to,
            final String subject,
            final String text) throws MessagingException {
        sendMessage(host, port, user, password, trustAllCertificates, secure, from, to, subject, text, null);
    }
    
    public static void sendMessage(
            final String host,
            final Integer port,
            final String user,
            final String password,
            final boolean trustAllCertificates,
            final boolean secure,
            final String from,
            final String[] to,
            final String subject,
            final String text,
            final String html) throws MessagingException {
        sendMessage(host, port, user, password, trustAllCertificates, secure, createMessage(from, to, subject, text, html));
    }
    
    public static void sendMessage(
            final String host,
            final Integer port,
            final String user,
            final String password,
            final boolean trustAllCertificates,
            final boolean secure,
            final String from,
            final String[] to,
            final String subject,
            final String text,
            final String html,
            final File[] attachments) throws MessagingException, IOException {
        sendMessage(host, port, user, password, trustAllCertificates, secure, createMessage(from, to, subject, text, html, attachments));
    }
    
    public static void sendMessage(
            final String host,
            final Integer port,
            final String user,
            final String password,
            final boolean trustAllCertificates,
            final boolean secure,
            final Mail m) throws MessagingException {
        
        MailTransport t = secure ? MailTransport.SMTPS : MailTransport.SMTP;
        t.setTrustAllHosts(trustAllCertificates);
        MailSender sender = new SimpleMailSender(host, port, user, password, t);        
        sender.sendMail(m);
    }
    
    public static Mail createMessage(
            final String from,
            final String[] to,
            final String subject,
            final String text,
            final String html) {
        
        Mail m = new Mail();
        m.setFrom(from);
        m.addTo(to);
        m.setSubject(subject);
        m.setText(text);
        m.setHtml(html);
        return m;
    }
    
    public static Mail createMessage(
            final String from,
            final String[] to,
            final String subject,
            final String text,
            final String html,
            final File[] attachments) throws IOException {
        
        Mail m = createMessage(from, to, subject, text, html);
        m.addAttachment(attachments);
        return m;
    }
}
