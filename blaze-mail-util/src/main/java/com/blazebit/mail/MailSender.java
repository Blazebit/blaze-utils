/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public interface MailSender {
    
    public void sendMail(final Mail email) throws MailException;
    
    public void sendMail(final Mail email, final MailTransport transport) throws MailException;
    
//    public void setDebug(boolean debug);
    
}
