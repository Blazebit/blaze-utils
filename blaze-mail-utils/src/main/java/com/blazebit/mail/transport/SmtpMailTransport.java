/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail.transport;

/**
 * @author Christian Beikov
 * @since 0.1.2
 */
public class SmtpMailTransport extends AbstractMailTransport {

    @Override
    public String getProtocol() {
        return "smtp";
    }

    @Override
    public Integer getDefaultPort() {
        return Integer.valueOf(25);
    }

    @Override
    public boolean isSecure() {
        return false;
    }
}
