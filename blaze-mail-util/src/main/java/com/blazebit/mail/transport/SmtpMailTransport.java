/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail.transport;

import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class SmtpMailTransport extends AbstractMailTransport {

    private static final Logger log = Logger.getLogger(SmtpMailTransport.class.getName());

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
