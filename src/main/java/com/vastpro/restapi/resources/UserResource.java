package com.vastpro.restapi.resources;
import javax.ws.rs.core.Response.Status;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;
import org.apache.ofbiz.service.ServiceUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
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
	            (LocalDispatcher) request.getAttribute("dispatcher");
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
    @Path("/signIn")
    public Response login(@Context HttpServletRequest request,@Context HttpServletResponse response) {
        try {
         

           

            LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

            Map<String, Object> input = new HashMap<String, Object>();
               input.put("userLoginId", request.getAttribute("userLoginId"));
               input.put("currentPassword",  request.getAttribute("password"));
            

            Map<String, Object> result = dispatcher.runSync("signIn",input);
            if ("success".equals(result.get("responseMessage"))) {
                return Response.ok(
                    Map.of(
                        "status", "success",
                        "message", result.get("successMessage")
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
                    "message","Invalid Credinatilas"
                )
            ).build();
        }
    }
    
 
    @POST
    @Path("/signUp")
    public Response signup(@Context HttpServletRequest request,@Context HttpServletResponse response){
    	Map<String,Object> user=new HashMap<String, Object>();
    	user.put("userName",request.getAttribute("userName"));
    	user.put("firstName",request.getAttribute("firstName"));
    	user.put("lastName",request.getAttribute("lastName"));
    	user.put("phNo",request.getAttribute("phNo"));
    	user.put("email",request.getAttribute("email"));
    	user.put("password",request.getAttribute("password"));
    	LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
    	
    	if(dispatcher==null) {
    		return Response.status(500).entity(Map.of("error","dispatcher is null")).build();
    	}else {
    		try {
    			
				Map<String,Object> result=dispatcher.runSync("regService", user);
				if(result.get("responseMessage").equals("success")) {
					return Response.ok(Map.of("success",result.get("successMessage"))).build();
				}else {
					return Response.status(Status.NOT_ACCEPTABLE).entity(Map.of("error","mmm")).build();
				}
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","internal server error ty again after some time")).build();
			}
    	}	
    	
    }
    @GET
    @Path("/getAllUser")
    public Response getAllUser(@Context HttpServletRequest request,@Context HttpServletResponse response) {
    	LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
    	if(dispatcher==null) {
    		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","dispatcher is null")).build();
    	}else {
    		try {
				Map<String,Object> result=dispatcher.runSync("getAllUser",Map.of());
				if(result.get("responseMessage").equals("success")) {
					List<GenericValue> users=(List<GenericValue>) result.get("allUser");
					List<String> names = users.stream()
					        .map(user -> user.getString("userLoginId")) // or "firstName" based on your field
					        .collect(Collectors.toList());
					return Response.status(Status.OK).entity(Map.of("allUser",names,"stats",result.get("responseMessage"))).build();
				}
				return Response.status(Status.NO_CONTENT).entity(Map.of("error","no data")).build();
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","exception"+e.getMessage())).build();
			}
    	}
    }
    @POST
    @Path("/saveexamrelationship")
    public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
    	if(dispatcher==null) {
    		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","dispatcher is null")).build();
    	}
    	try {
			List<Map<String,Object>> list=(List<Map<String, Object>>) request.getAttribute("allData");
			
			
			Map<String,Object> result=dispatcher.runSync("examrelationshipcreate",Map.of("allData",list));
			if(result.get("responseMessage").equals("success")) {
				return Response.ok(Map.of("success",result.get("successMessage"))).build();
			}else {
				return Response.status(Status.NOT_ACCEPTABLE).entity(Map.of("error","mmm")).build();
			}
//    		String jsonString = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//    		ObjectMapper mapper = new ObjectMapper();
//    		Map<String,Object> inputData = mapper.readValue(jsonString, Map.class);
//    		
//    		
//    		List<Map<String, Object>> alldata = (List<Map<String, Object>>) inputData.get("alldata");
//
//    		for (Map<String, Object> userData : alldata) {
//    		    System.out.println("ExamId: " + userData.get("examId"));
//    		    System.out.println("PartyId: " + userData.get("partyId"));
//    		    System.out.println("AllowedAttempts: " + userData.get("allowedAttempts"));
//    		    System.out.println("NoOfAttempts: " + userData.get("noOfAttempts"));
//    		}

    		

    		
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","internal server error try again after some time")).build();
		} 
	}	

}
    


