/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.mail.transport;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 0.1.2
 */
public class MailSSLSocketFactory extends SSLSocketFactory {

    private boolean trustAllHosts = false;
    private List<String> trustedHosts = new ArrayList<String>();
    private List<String> temporaryTrustedHosts = new ArrayList<String>();
    private SSLContext sslContext;
    private KeyManager[] keyManagers;
    private TrustManager[] trustManagers;
    private SecureRandom secureRandom;
    private SSLSocketFactory delegateFactory = null;

    public MailSSLSocketFactory() throws GeneralSecurityException {
        this("TLS");
    }

    public MailSSLSocketFactory(String protocol)
            throws GeneralSecurityException {
        trustAllHosts = false;
        sslContext = SSLContext.getInstance(protocol);
        keyManagers = null;
        trustManagers = new TrustManager[]{new MailTrustManager()};
        secureRandom = null;

        createDelegateFactory();
    }

    private synchronized void createDelegateFactory()
            throws KeyManagementException {
        sslContext.init(keyManagers, trustManagers, secureRandom);
        delegateFactory = (SSLSocketFactory) sslContext.getSocketFactory();
    }

    public synchronized KeyManager[] getKeyManagers() {
        return (KeyManager[]) keyManagers.clone();
    }

    public synchronized void setKeyManagers(KeyManager[] keyManagers)
            throws GeneralSecurityException {
        this.keyManagers = (KeyManager[]) keyManagers.clone();
        createDelegateFactory();
    }

    public synchronized SecureRandom getSecureRandom() {
        return secureRandom;
    }

    public synchronized void setSecureRandom(SecureRandom secureRandom)
            throws GeneralSecurityException {
        this.secureRandom = secureRandom;
        createDelegateFactory();
    }

    public synchronized TrustManager[] getTrustManagers() {
        return trustManagers;
    }

    public synchronized void setTrustManagers(TrustManager[] trustManagers)
            throws GeneralSecurityException {
        this.trustManagers = trustManagers;
        createDelegateFactory();
    }

    public synchronized boolean isTrustAllHosts() {
        return trustAllHosts;
    }

    public synchronized void setTrustAllHosts(boolean trustAllHosts) {
        this.trustAllHosts = trustAllHosts;
    }

    public synchronized String[] getTrustedHosts() {
        return trustedHosts.toArray(new String[0]);
    }

    public synchronized void setTrustedHosts(String[] trustedHosts) {
        this.trustedHosts = Arrays.asList(trustedHosts);
    }

    public synchronized void removeTrustedHost(String trustedHost) {
        this.trustedHosts.remove(trustedHost);
        this.temporaryTrustedHosts.remove(trustedHost);
    }

    public synchronized void addTrustedHost(String host, boolean permanently) {
        if (!permanently) {
            temporaryTrustedHosts.add(host);
        } else {
            trustedHosts.add(host);
        }
    }

    public synchronized void clearTemporaryTrustedHosts() {
        temporaryTrustedHosts.clear();
    }

    public synchronized List<String> getTemporaryTrustedHosts() {
        return Collections.unmodifiableList(temporaryTrustedHosts);
    }

    @Override
    public synchronized Socket createSocket(Socket socket, String s, int i,
                                            boolean flag) throws IOException {
        return delegateFactory.createSocket(socket, s, i, flag);
    }

    @Override
    public synchronized String[] getDefaultCipherSuites() {
        return delegateFactory.getDefaultCipherSuites();
    }

    @Override
    public synchronized String[] getSupportedCipherSuites() {
        return delegateFactory.getSupportedCipherSuites();
    }

    @Override
    public synchronized Socket createSocket() throws IOException {
        return delegateFactory.createSocket();
    }

    @Override
    public synchronized Socket createSocket(InetAddress inetAddress, int i,
                                            InetAddress inetAddress1, int j) throws IOException {
        return delegateFactory.createSocket(inetAddress, i, inetAddress1, j);
    }

    @Override
    public synchronized Socket createSocket(InetAddress inetAddress, int i)
            throws IOException {
        return delegateFactory.createSocket(inetAddress, i);
    }

    @Override
    public synchronized Socket createSocket(String s, int i,
                                            InetAddress inetAddress, int j) throws IOException,
            UnknownHostException {
        return delegateFactory.createSocket(s, i, inetAddress, j);
    }

    @Override
    public synchronized Socket createSocket(String s, int i)
            throws IOException, UnknownHostException {
        return delegateFactory.createSocket(s, i);
    }

    private class MailTrustManager implements X509TrustManager {

        private X509TrustManager delegateTrustManager = null;

        private MailTrustManager() throws GeneralSecurityException {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init((KeyStore) null);
            delegateTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            delegateTrustManager.checkClientTrusted(certs, authType);
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            if (isTrustAllHosts())
                return;
            String host = null;

            for (String part : certs[0].getSubjectX500Principal().getName()
                    .split(",")) {
                String[] keyValue = part.split("=");

                if ("CN".equals(keyValue[0])) {
                    host = keyValue[1];
                    break;
                }
            }

            for (String trustedHost : getTemporaryTrustedHosts()) {
                if (host.equals(trustedHost))
                    return;
            }

            for (String trustedHost : getTrustedHosts()) {
                if (host.equals(trustedHost))
                    return;
            }

            delegateTrustManager.checkServerTrusted(certs, authType);
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return delegateTrustManager.getAcceptedIssuers();
        }
    }
}