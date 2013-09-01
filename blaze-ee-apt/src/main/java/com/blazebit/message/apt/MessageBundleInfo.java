package com.blazebit.message.apt;

import java.util.Collection;
import java.util.Locale;

public class MessageBundleInfo {
	private final String baseName;
	private final long lastModified;
	private final Collection<Locale> locales;
	private final Collection<String> messages;

	public MessageBundleInfo(String baseName, long lastModified,
	    Collection<Locale> locales, Collection<String> messages) {
		this.baseName = baseName;
		this.lastModified = lastModified;
		this.locales = locales;
		this.messages = messages;
	}

	public String getBaseName() {
		return baseName;
	}

	public long getLastModified() {
		return lastModified;
	}

	public Collection<Locale> getLocales() {
		return locales;
	}

	public Collection<String> getMessages() {
		return messages;
	}
}
