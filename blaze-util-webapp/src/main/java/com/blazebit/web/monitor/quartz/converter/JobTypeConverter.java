/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.web.monitor.quartz.converter;

import java.util.logging.Level;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 *
 * @author Christian Beikov
 */
@FacesConverter(value="jobTypeConverter")
public class JobTypeConverter implements Converter{

//    private static final Logger log = LoggerFactory.getLogger(JobTypeConverter.class);

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        try {
            return Class.forName(string);
        } catch (ClassNotFoundException ex) {
            throw new ConverterException(ex);
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return ((Class) o).getName();
    }
}
