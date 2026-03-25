package com.vastpro.restapi.resources;
import javax.ws.rs.core.Response.Status;


import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;
import org.apache.ofbiz.service.ServiceUtil;
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

	@Context
	private HttpServletRequest request;
	
	@Context
	private ServletContext servletContext;
	
	
	 private Delegator getDelegator() {
	        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
	        if (delegator == null) {
	            delegator = DelegatorFactory.getDelegator("default");
	        }
	        return delegator;
	    }

	 private LocalDispatcher getDispatcher() {
	        LocalDispatcher dispatcher =
	            (LocalDispatcher) servletContext.getAttribute("dispatcher");

	        if (dispatcher == null) {
	            dispatcher = ServiceContainer.getLocalDispatcher(
	                "sphinx",   // must match web.xml
	                getDelegator()
	            );
	        }
	        return dispatcher;
	    }

   
    @POST
    @Path("/testpost")
    public Response testPost() {
        return Response.ok("WORKING").build();
    }
    
    @GET
    @Path("/testget")
    public Response testget() {
        return Response.ok("WORKING").build();
    }
    
    @DELETE
    @Path("/testdelete")
    public Response testDelete() {
        return Response.ok("WORKING").build();
    }
    
    @PUT
    @Path("/testput")
    public Response testPut() {
        return Response.ok("WORKING").build();
    }
    
    @PATCH
    @Path("/testpatch")
    public Response testPatch() {
        return Response.ok("WORKING").build();
    }
    
    @POST
    @Path("/signin")
    public Response login(Map<String, Object> input) {
        try {
            String username = (String) input.get("username");
            String password = (String) input.get("password");

            if (username == null || password == null) {
                return Response.status(400).entity(
                    Map.of("status", "error", "message", "Missing username or password")
                ).build();
            }

            LocalDispatcher dispatcher = getDispatcher();

            Map<String, Object> context = Map.of(
                "login.username", username,
                "login.password", password
            );

            Map<String, Object> result = dispatcher.runSync("signIn", context);
            if ("success".equals(result.get("responseMessage"))) {
                return Response.ok(
                    Map.of(
                        "status", "success",
                        "message", "Signin successful"
                    )
                ).build();
            } else {
                return Response.status(401).entity(
                    Map.of(
                        "status", "error",
                        "message", result.get("errorMessage")
                    )
                ).build();
            }

        } catch (Exception e) {
            return Response.status(500).entity(
                Map.of(
                    "status", "error",
                    "message", e.getMessage()
                )
            ).build();
        }
    }
    
 
    @POST
    @Path("/signup")
    public Response signup(Map<String, Object> context) {
    	
    	LocalDispatcher dispatcher = getDispatcher();
            try {
            	context.get("delegator");
            	System.out.println("signup in");
            	   Map<String, Object> result = dispatcher.runSync("regService", context);
            	   System.out.println("signup out");
             if ("Employee created successfully".equals(result.get("successMessage"))) {
            	 System.out.println("signup if");
                return Response.ok(
                    Map.of(
                        "status", "success",
                        "message", "user created successfully"
                    )
                ).build();

            }else {
            	
                return Response.status(401).entity(
                        Map.of(
                            "status", "error",
                            "message", result.get("errorMessage")
                        )
                    ).build();
                }
             } catch (Exception e) {
                return Response.status(500)
                        .entity(Map.of(
                            "status", "error",
                            "message", e.getMessage()
                        ))
                        .build();
            }
        
    	
    }

}
