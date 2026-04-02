package com.vastpro.restapi;

import org.glassfish.jersey.server.ResourceConfig;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import org.glassfish.jersey.jackson.JacksonFeature;

/**
 * RestApplication
 *
 * This class configures Jersey for RESTful web services.
 * It scans the package "com.vastpro.sphinx.rest.resource".
 *
 */

public class RestApplication extends ResourceConfig{
    public RestApplication() {
        packages("com.vastpro.restapi.resources");
        register(JacksonFeature.class);
//        register(MultiPartFeature.class);
    }
}
