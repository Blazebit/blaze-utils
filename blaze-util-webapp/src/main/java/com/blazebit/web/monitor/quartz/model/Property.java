/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.web.monitor.quartz.model;

import java.io.Serializable;

/**
 * 
 * @author Christian Beikov
 */
public class Property implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private Object value;
	private boolean required;
	private Class<?> type;
	private String description;

	public Property(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public Property(String name, boolean required, Class<?> type,
			String description) {
		this.name = name;
		this.required = required;
		this.type = type;
		this.description = description;
	}

	public Property(String name, Object value, boolean required, Class<?> type,
			String description) {
		this.name = name;
		this.value = value;
		this.required = required;
		this.type = type;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public boolean isRequired() {
		return required;
	}

	public Class<?> getType() {
		return type;
	}
}
