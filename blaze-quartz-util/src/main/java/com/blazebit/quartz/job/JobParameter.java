/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz.job;

import java.io.Serializable;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class JobParameter implements Serializable {

    private String name;
    private boolean required;
    private Class type;
    private String description;

    public JobParameter(String name, boolean required, Class type) {
        this.name = name;
        this.required = required;
        this.type = type;
    }

    public JobParameter(String name, boolean required, Class type, String description) {
        this.name = name;
        this.required = required;
        this.type = type;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public Class getType() {
        return type;
    }
    
}
