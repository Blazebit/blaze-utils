package com.blazebit.message.apt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface MessageBundleConfig {

    /**
     * Returns the base path to properties files for this message bundle.
     * If nothing is specified, the base path will be the qualified class name of the message bundle
     * with dot characters replaced by slash characters.
     *
     * @return the base base to properties files
     */
    String base() default "";

    /**
     * Returns the locales for which properties files are required to be present for this message bundle.
     *
     * @return the locales for which properties files are required to be present
     */
    String[] locales();

    /**
     * Returns the location of the template on the class path which should be used for the enum generation.
     *
     * @return the location of the template
     */
    String templateLocation() default "/META-INF/templates/messageBundleEnum.ftl";

}