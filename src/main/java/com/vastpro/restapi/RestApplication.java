package com.vastpro.restapi;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * This class is used to configure rest api
 */
public class RestApplication extends ResourceConfig {
	/**
	 * constructor is used to register packages and features
	 */
	public RestApplication() {
		packages("com.vastpro.restapi.resources");
		register(JacksonFeature.class);
		register(MultiPartFeature.class);
	}
}
