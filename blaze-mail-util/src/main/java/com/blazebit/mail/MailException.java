/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail;

import javax.mail.MessagingException;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public final class MailException extends RuntimeException {

	public static final String GENERIC_ERROR = "Generic error: %s";
	public static final String MISSING_HOST = "Can't send an email without host";
	public static final String MISSING_USERNAME = "Can't have a password without username";
	public static final String INVALID_ENCODING = "Encoding not accepted: %s";
	public static final String INVALID_RECIPIENT = "Invalid TO address: %s";
	public static final String INVALID_REPLYTO = "Invalid REPLY TO address: %s";
	public static final String INVALID_SENDER = "Invalid FROM address: %s";
	public static final String MISSING_SENDER = "Email is not valid: missing sender";
	public static final String MISSING_RECIPIENT = "Email is not valid: missing recipients";
	public static final String MISSING_SUBJECT = "Email is not valid: missing subject";
	public static final String MISSING_CONTENT = "Email is not valid: missing content body";

	public MailException(final String message) {
		super(message);
	}

	public MailException(final String message, final MessagingException cause) {
		super(message, cause);
	}
}