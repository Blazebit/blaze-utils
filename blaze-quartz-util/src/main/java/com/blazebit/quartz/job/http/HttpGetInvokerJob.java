/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz.job.http;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class HttpGetInvokerJob extends AbstractHttpInvokerJob {

    @Override
    protected URLConnection createConnection(String url, String parameterString) throws IOException{
        return new URL(new StringBuilder(url).append("?").append(parameterString).toString()).openConnection();
    }
}
