package com.vastpro.restapi.resources;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import com.vastpro.utility.CreateConnection;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;

import com.vastpro.utility.CreateConnection;

@Path("/exam")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExamResource {
	 @POST
	 @Path("/createexam")
	
	    public Response createExam(@Context  HttpServletRequest request, @Context HttpServletResponse response) {
		 LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		 
		 
		 Map<String,Object> input=new HashMap<>();
		 
		 input.put("examName", request.getAttribute("examName"));
		 input.put("description", request.getAttribute("description"));
		 input.put("noOfQuestions", request.getAttribute("noOfQuestions"));
		 input.put("duration", request.getAttribute("duration"));
		 input.put("passPercentage",request.getAttribute("passPercentage"));
	    
			  
			  if(dispatcher==null) { 
				  Response.status(500).entity(Map.of("error","dispatcher is null")).build();
			  }
			  try {
				Map<String,Object> result=dispatcher.runSync("examcreate",input);
				if(result.get("responseMessage").equals("success")) {
					return Response.ok(Map.of("status","success",
							  "message",result.get("successMessage"),"examId",result.get("examId")
					  )).build();
				}else {
					return Response.status(200).entity(Map.of("error",result.get("responseMessage"))).build();
				}
					
				
			} catch (GenericServiceException e) {			
				e.printStackTrace();
				return Response.status(500).entity(Map.of("error",e.getMessage())).build();
				
			}
			  
		  }
	    @GET
	    @Path("/getexam")
	    public Response getExamName(@Context  HttpServletRequest request) {
	    	LocalDispatcher dispatcher= (LocalDispatcher) request.getAttribute("dispatcher");
	    	if(dispatcher==null) {
	    		Response.status(500).entity(Map.of("error","dispatcher is null")).build();
	    	}else {
	    		try {
					Map<String,Object> result=dispatcher.runSync("getExam",Map.of());
					return Response.ok(Map.of("data",result)).build();
				} catch (GenericServiceException e) {
					e.printStackTrace();
					return Response.status(500).entity(Map.of("error","through exception")).build();
				}
	    	}
			return null;
	    }
	
	private Delegator getDelegator(ServletContext servletContext) {
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        if (delegator == null) {
            delegator = DelegatorFactory.getDelegator("default");
        }
        return delegator;
    }

    public LocalDispatcher getDispatcher(ServletContext servletContext) {
        LocalDispatcher dispatcher =
            (LocalDispatcher) servletContext.getAttribute("dispatcher");

        if (dispatcher == null) {
            dispatcher = ServiceContainer.getLocalDispatcher(
                "sphinx",   // must match web.xml
                getDelegator(servletContext)
            );
        }
        return dispatcher;
    }
    
	@Context
	private ServletContext servletContext;
	
	@Context
	private HttpServletRequest request;

    @POST
    @Path("/createQuestion")
    public Response getUser(Map<String, Object> context) {
    	try {
    		//transfer the data to create question service service
    		//exam name, topic, question details, option A, option B, option C, option D, answer
    		LocalDispatcher dispatcher = getDispatcher(servletContext);
    		Map<String, Object> result = dispatcher.runSync("createQuestion", context);
    	}catch(Exception e) {
    		
    		}
    	return null;
    	}
    
    public Response getUserDetail(Map<String, Object> input) {
		  
		  LocalDispatcher dispatcher = getDispatcher(servletContext);
		  if(dispatcher==null) {
			  Response.status(500).entity(Map.of("error","dispatcher is null")).build();
		  }
		  try {
			Map<String,Object> result=dispatcher.runSync("examcreate", input);
			if("created successfully".equals(result.get("message"))) {
				return Response.ok(Map.of("status","success",
										  "message","createdsuccesfully"
								  )).build();
			}
			else {
				return Response.status(200).entity(Map.of("error","not created")).build();
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			return Response.status(500).entity(Map.of("error",e.getMessage())).build();
			
		}
		  
	  }
    @Path("/examUpdate")
    @PUT
    public Response examUpdate(@Context HttpServletRequest request,@Context HttpServletResponse response) {
    	LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
    	if(dispatcher==null) {
    		return Response.status(500).entity(Map.of("error","dispatchrer is null")).build();
    	}
    	 
		 Map<String,Object> input=new HashMap<>();
		 
		 input.put("examId",request.getAttribute("examId"));
		 input.put("examName", request.getAttribute("examName"));
		 input.put("description", request.getAttribute("description"));
		 input.put("noOfQuestions", request.getAttribute("noOfQuestions"));
		 input.put("duration", request.getAttribute("duration"));
		 input.put("passPercentage",request.getAttribute("passPercentage"));
    	
    	try {
			Map<String,Object> result=dispatcher.runSync("examUpdate",input);
			if(result.get("responseMessage").equals("success")) {
				return Response.ok(Map.of("success","updated successfully")).build();
			}else {
				return Response.status(200).entity(Map.of("error","not updated")).build();
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.status(500).entity(Map.of("error","exception occured")).build();
		}
    }
    @Path("/examDelete")
    @DELETE
    public Response examDelete(@Context HttpServletRequest request,@Context HttpServletResponse response) {
    	LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
    	if(dispatcher==null) {
    		return Response.status(500).entity(Map.of("error","it is dsispacter is null")).build();
    	}else {
    		String examId=(String)request.getAttribute("examId");
    		if(examId==null) {
    			return Response.ok(Map.of("error","examid is null")).build();
    		}else {
    			Map<String,Object> examDelete=new HashMap<String, Object>();
    			examDelete.put("examId",examId);
    			try {
					Map<String,Object> result=dispatcher.runSync("examDelete", examDelete);
					if(result.get("responseMessage").equals("success")) {
						return Response.ok(Map.of("success","exam deleted")).build();
					}else {
						return Response.ok(Map.of("success","exam not deleted")).build();
					}
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return Response.status(500).entity(Map.of("error","eception througnm")).build();
					
				}
    		}
    	}
    }
}
