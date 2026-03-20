package com.vastpro.restapi.resources;
import javax.ws.rs.core.Response.Status;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;
import org.apache.ofbiz.service.ServiceUtil;
@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

	@Context
	private HttpServletRequest request;
	
	@Context
	private ServletContext servletContext;
	
	/*
    private Delegator getDelegator() {
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        if (delegator == null) {
            // Fallback — get directly from factory
            delegator = DelegatorFactory.getDelegator("default");
        }
        return delegator;
    }
    
    private LocalDispatcher getDispatcher() {
        LocalDispatcher dispatcher = 
            (LocalDispatcher) servletContext.getAttribute("dispatcher");
        if (dispatcher == null) {
            // Fallback — get directly from ServiceContainer
            dispatcher = ServiceContainer.getLocalDispatcher(
                "sphinx",   // must match localDispatcherName in web.xml
                getDelegator()
            );
        }
        return dispatcher;
    }
*/
	
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
    @Path("/user")
    public Response getUser(Map<String, Object> input) {
    	try {
            Delegator delegator   = getDelegator();
            LocalDispatcher dispatcher = getDispatcher();

            if (dispatcher == null) {
            	//Status.INTERNAL_SERVER_ERROR
                return Response.status(500)
                    .entity(Map.of("error", "Dispatcher is still null"))
                    .build();
            }

            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin")
                    .where("userLoginId", "admin")
                    .queryOne();

            input.put("userLogin", userLogin);

            Map<String, Object> result =
                dispatcher.runSync("getData", input);

            return Response.ok(result.get("success")).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    
    }
    
    @POST
    @Path("/data")
    public Response getUserDetail(Map<String, Object> input) {
        try {
            return Response.ok(
                Map.of(
                    "status", "success",
                    "message", "User API working fine"
                )
            ).build();

        } catch (Exception e) {
            return Response.status(500)
                    .entity(Map.of(
                        "status", "error",
                        "message", e.getMessage()
                    ))
                    .build();
        }
    }
    
    @POST
    @Path("/test")
    public Response test() {
        return Response.ok("WORKING").build();
    }
    
    @POST
    @Path("/login")
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

            Map<String, Object> result =userLogin(context);
//                dispatcher.runSync("userLogin", context);
            if ("success".equals(result.get("responseMessage"))) {
//                return Response.ok(
//                    Map.of(
//                        "status", "success",
//                        "message", "Login successful"
//                    )
//                ).build();
            	Map<String, Object> result1=ServiceUtil.returnError("error");
            	return Response.ok(result1).build();
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
    
    public Map<String, Object> userLogin(Map<String, Object> context){
    	if(context.get("login.username").equals("kamal") && context.get("login.password").equals("kamal54321") ) {
    		return ServiceUtil.returnSuccess("success");
    	}else {
    		return ServiceUtil.returnError("error");
    	}
    }
}
