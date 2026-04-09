package com.vastpro.restapi.resources;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;
import org.apache.ofbiz.service.ServiceUtil;

@Path("/topic")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TopicResource {
	
	private Delegator getDelegator(ServletContext context) {
		Delegator delegator=(Delegator) context.getAttribute("delegator");
		if(delegator==null) {
			delegator=DelegatorFactory.getDelegator("default");
		}
		return delegator;
	}
	
	public LocalDispatcher getDispatcher(ServletContext context) {
		LocalDispatcher dispatcher=(LocalDispatcher) context.getAttribute("dispatcher");
		if(dispatcher==null) {
			dispatcher=ServiceContainer.getLocalDispatcher("sphinx", getDelegator(context));
		}
		return dispatcher;
	}
	
	
	
	
	@POST
	@Path("/createtopic")
	public Response createTopic(@Context HttpServletRequest request) {
		
		LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
		if(dispatcher==null) {
			Response.status(500).entity(Map.of("error","dispatcher is  null")).build();
		}
		try {
			Map<String, Object> input=new HashMap<String, Object>();
			input.put("topicName",(String) request.getAttribute("topicName"));
			
			System.out.println("what is the eror "+input);
			Map<String, Object> result=dispatcher.runSync("createtopic", input);
			return Response.ok(result).build();
			
		}catch (GenericServiceException e) {
			
			e.printStackTrace();
			return Response.status(500).entity(Map.of("error",e.getMessage())).build();
		}
		
	}
	
	@GET
	@Path("/gettopics")
	public Response getTopics(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
		if(dispatcher==null) {
			Response.status(500).entity(Map.of("error","dispatcher is  null")).build();
		}
		try {
			Map<String, Object> result=dispatcher.runSync("getAllTopic",Map.of());				
			return Response.ok(result).build();
			
		}catch (GenericServiceException e) {
			e.printStackTrace();
			return Response.status(500).entity(Map.of("error",e.getMessage())).build();
			
		}
	}
	
	
	@DELETE
	@Path("/deletetopic")
	public Response deleteTopic(@Context HttpServletRequest request, @Context ServletContext context) {
	    LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

	    if (dispatcher == null) {
	        dispatcher = ServiceContainer.getLocalDispatcher("sphinx", getDelegator(context));
	    }

	    try {
	        Map<String, Object> input = new HashMap<>();
	        String topicId = request.getParameter("topicId");
	        if (topicId == null || topicId.isEmpty()) {
	            return Response.status(400).entity(Map.of("error", "topicId is required")).build();
	        }
	        input.put("topicId", topicId);

	        Map<String, Object> result = dispatcher.runSync("deleteTopicById", input);

	        if (ServiceUtil.isError(result)) {
	            return Response.status(404).entity(Map.of("error", result.get("errorMessage"))).build();
	        } else {
	            return Response.ok(Map.of("status", "success", "message", "Topic deleted successfully")).build();
	        }
	    } catch (GenericServiceException e) {
	        e.printStackTrace();
	        return Response.status(500).entity(Map.of("error", e.getMessage())).build();
	    }
	}
	
	
	@PUT
	@Path("/updatetopic")
	public Response updateTopic(@Context HttpServletRequest request,@Context ServletContext context) {
		  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		    if (dispatcher == null) {
		        dispatcher = ServiceContainer.getLocalDispatcher("sphinx", getDelegator(context));
		    }
		
		try {
			Map<String, Object> input=new HashMap<String, Object>();
			input.put("topicId", request.getAttribute("topicId"));
			input.put("topicName", request.getAttribute("topicName"));
			Map<String, Object> result=dispatcher.runSync("updateTopicOwn", input);
			return Response.ok(result).build();
		}catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity(Map.of("error2", e.getMessage())).build();
		}
	}
	
}
