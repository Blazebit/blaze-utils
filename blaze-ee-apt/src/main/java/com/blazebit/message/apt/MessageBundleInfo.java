package com.blazebit.message.apt;

import java.util.List;
import java.util.Locale;

public class MessageBundleInfo {
	private final String baseName;
	private final long lastModified;
	private final List<Locale> locales;
	private final List<String> messages;

	public MessageBundleInfo(String baseName, long lastModified,
			List<Locale> locales, List<String> messages) {
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

	public List<Locale> getLocales() {
		return locales;
	}

	public List<String> getMessages() {
		return messages;
	}
}
