package com.vastpro.restapi.resources;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.service.LocalDispatcher;


/**
 * QuestionResource
 * 
 * This class handles question related Requests and Responses
 */
@Path("/question")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuestionResource {


		@Context
		static private ServletContext servletContext;
		
		@Context
		private HttpServletRequest request;		
		
	    @POST
	    @Path("/createQuestion")
	    public Response getUser(@Context  HttpServletRequest request, Map<String, Object> context) {
	    	try {
	    		//transfer the data to create question service service
	    		//exam name, topic, question details, option A, option B, option C, option D, answer
	    		LocalDispatcher dispatcher =(LocalDispatcher) request.getAttribute("dispatcher");
	    		Map<String, Object> result = dispatcher.runSync("questionMasterCreate", context);
	    		Debug.log("built in service return value: "+result);
	    		Response.ok(
	    				Map.of(
	    						"success","done"
	    						)
	    				).build();
	    	}catch(Exception e) {
	    		Debug.log("Question Master: "+e.getMessage());
	    		}
	    	return null;
	    	}
}
