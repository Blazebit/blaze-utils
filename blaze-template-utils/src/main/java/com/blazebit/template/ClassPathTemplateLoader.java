package com.blazebit.template;

import freemarker.cache.TemplateLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class ClassPathTemplateLoader implements TemplateLoader {

    private final ClassLoader classLoader;

    public ClassPathTemplateLoader() {
        this(ClassPathTemplateLoader.class.getClassLoader());
    }

    public ClassPathTemplateLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Object findTemplateSource(String string) throws IOException {
        return classLoader.getResource(string);
    }

    @Override
    public long getLastModified(Object o) {
        URL url = (URL) o;

        if ("file".equals(url.getProtocol())) {
            try {
                return new File(url.toURI()).lastModified();
            } catch (URISyntaxException ex) {
                return -1;
            }
        } else if ("jar".equals(url.getProtocol())) {
            try {
                URLConnection connection = url.openConnection();
                return connection.getLastModified();
            } catch (IOException ex) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public Reader getReader(Object o, String string) throws IOException {
        return new InputStreamReader(((URL) o).openStream());
    }

    @Override
    public void closeTemplateSource(Object o) throws IOException {
    }
}
