/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz.job.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class HttpPostInvokerJob extends AbstractHttpInvokerJob {

    @Override
    protected URLConnection createConnection(String url, String parameterString) throws IOException{
        OutputStreamWriter wr = null;
        
        try{
            URLConnection con = new URL(url).openConnection();
            con.setDoOutput(true);
            wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(parameterString);
            wr.flush();
            return con;
        }finally{
            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException ex) {
                    // Ignore
                }
            }
        }
    }
}
