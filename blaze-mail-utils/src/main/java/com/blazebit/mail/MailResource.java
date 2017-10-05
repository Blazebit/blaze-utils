/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail;

import javax.activation.DataSource;

/**
 * @author Christian Beikov
 * @since 0.1.2
 */
public class MailResource {

    private final String name;
    private final DataSource dataSource;

    public MailResource(String name, DataSource dataSource) {
        this.name = name;
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getName() {
        return name;
    }
}
