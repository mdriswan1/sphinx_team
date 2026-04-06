package com.vastpro.restapi.resources;

import java.util.HashMap;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;
import org.apache.ofbiz.service.ServiceUtil;


 
@Path("/question")
public class QuestionResource {

	@POST
	@Path("/createquestion")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createQuestion(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		LocalDispatcher dispatcher=(LocalDispatcher)request.getAttribute("dispatcher");
		Map<String,Object> resp= new HashMap<String, Object>();
		if(dispatcher==null) {
			return Response.status(500).entity(Map.of("error","Dispatcher not found")).build();
		}
		try {	
			Map<String,Object> data= new HashMap<String, Object>();
			data.put("questionDetail", request.getAttribute("questionDetail"));
			data.put("optionA", request.getAttribute("optionA"));
			data.put("optionB", request.getAttribute("optionB"));
			data.put("optionC", request.getAttribute("optionC"));
			data.put("optionD", request.getAttribute("optionD"));
			data.put("answer", request.getAttribute("answer"));
			data.put("numAnswers", request.getAttribute("numAnswers"));
			data.put("questionTypeId", request.getAttribute("questionTypeId"));
			data.put("difficultyLevel", request.getAttribute("difficultyLevel"));
			data.put("answerValue", request.getAttribute("answerValue"));
			data.put("topicId", request.getAttribute("topicId"));
			data.put("negativeMarkValue", request.getAttribute("negativeMarkValue"));

			Map<String, Object> result = dispatcher.runSync("createQuestionService", data);
			if (ServiceUtil.isError(result)) {
	        	resp.put("status",  "error");
	        	resp.put("message", ServiceUtil.getErrorMessage(result));
	            return Response.status(500).entity(resp ).build();
	        }
			return Response.ok(result).build();
		}catch(Exception e) {
			return Response.status(500).entity(Map.of("error", e.getMessage())).build();
		}
	}

	
	@POST
	@Path("/getquesbyid")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getQuestion(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		LocalDispatcher dispatcher=(LocalDispatcher)request.getAttribute("dispatcher");
		Map<String,Object> resp= new HashMap<String, Object>();
		if(dispatcher==null) {
			return Response.status(500).entity(Map.of("error","Dispatcher not found")).build();
		}
		try {	
			Map<String,Object> data= new HashMap<String, Object>();
			data.put("questionId", request.getAttribute("questionId"));
		    if(request.getAttribute("questionId")==null) {
				return Response.status(400).entity(Map.of("error","questionId null")).build();
		    }

			Map<String, Object> result = dispatcher.runSync("getQuesById", data);
			if (ServiceUtil.isError(result)) {
	        	resp.put("status",  "error");
	        	resp.put("message", ServiceUtil.getErrorMessage(result));
	            return Response.status(500).entity(resp ).build();
	        }
			return Response.ok(result).build();
		}catch(Exception e) {
			return Response.status(500).entity(Map.of("error", e.getMessage())).build();
		}
	}

	
	
	@PUT
	@Path("/updatequestion")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateQuestion(@Context HttpServletRequest request,@Context HttpServletResponse response) {
		LocalDispatcher dispatcher=(LocalDispatcher)request.getAttribute("dispatcher");
		Map<String,Object> resp= new HashMap<String, Object>();
		if(dispatcher==null) {
			return Response.status(500).entity(Map.of("error","Dispatcher not found")).build();
		}
	    try {
	        String questionId = (String) request.getAttribute("questionId");
	        if(request.getAttribute("questionId")==null) {
				return Response.status(400).entity(Map.of("error","questionId null")).build();
		    }

	        
	        Map<String,Object>data=new HashMap<String, Object>();
	        data.put("questionId", questionId);
	        data.put("questionDetail", request.getAttribute("questionDetail"));
			data.put("optionA", request.getAttribute("optionA"));
			data.put("optionB", request.getAttribute("optionB"));
			data.put("optionC", request.getAttribute("optionC"));
			data.put("optionD", request.getAttribute("optionD"));
			data.put("answer", request.getAttribute("answer"));
			data.put("numAnswers", request.getAttribute("numAnswers"));
			data.put("questionTypeId", request.getAttribute("questionTypeId"));
			data.put("difficultyLevel", request.getAttribute("difficultyLevel"));
			data.put("answerValue", request.getAttribute("answerValue"));
			data.put("topicId", request.getAttribute("topicId"));
			data.put("negativeMarkValue", request.getAttribute("negativeMarkValue"));
 
			
		
	        Map<String, Object> result  = dispatcher.runSync("updateQuestionMaster", data);
	        if (ServiceUtil.isError(result)) {
	        	resp.put("status",  "error");
	        	resp.put("message", ServiceUtil.getErrorMessage(result));
	            return Response.status(500).entity(resp ).build();
	        }
	        resp.put("status","SUCCESS");
	        resp.put("message","Question updated successfully");
	        return Response.ok(result).build();
 
	    } catch (Exception e) {
	    	return Response.status(500).entity(Map.of("error", e.getMessage())).build();
	    }
	}
	

	@DELETE
	@Path("/deletequestion")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteQuestion(@Context HttpServletRequest request,@Context HttpServletResponse response) {
		Map<String,Object> resp= new HashMap<String, Object>();
	
	  		LocalDispatcher dispatcher=(LocalDispatcher)request.getAttribute("dispatcher");
	  		if(dispatcher==null) {
	  			return Response.status(500).entity(Map.of("error","Dispatcher not found")).build();
	  		}
	    try {
	        String questionId=(String) request.getAttribute("questionId");
	        if(request.getAttribute("questionId")==null) {
				return Response.status(400).entity(Map.of("error","questionId null")).build();
		    }

	        
	        Map<String,Object>data=new HashMap<String, Object>();
	        data.put("questionId", questionId);
 
	        Map<String, Object> result = dispatcher.runSync("deleteQuesMaster",data);
 
	        if (ServiceUtil.isError(result)) {
	           
	            return Response.status(500).entity(result).build();
	        }
 
	        resp.put("status","SUCCESS");
	        resp.put("message", "question deleted Successfully");
	        return Response.ok(result).build();
 
	    } catch (Exception e) {
	        resp.put("status",  "ERROR");
	        resp.put("message", e.getMessage());
	        return Response.status(500).entity(resp).build();
	    }
	}
	
	@POST
	@Path("/getquesbytopic")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getquesbytopic(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if(dispatcher == null) {
			return Response.status(500).entity(Map.of("error","dispatcher is null")).build();
		}
		
		Map<String, Object> data = new HashMap<>();
		if(request.getAttribute("topicId")==null) {
			return Response.status(400).entity(Map.of("error", "topicId not found")).build();
		}
		
		data.put("topicId",request.getAttribute("topicId"));
		
		try {
			Map<String, Object> result = dispatcher.runSync("getquesbytopic", data);

			if(ServiceUtil.isError(result)) {
	            return Response.status(500).entity(result).build();
			}
			
			return Response.ok(result).build();
		} catch (GenericServiceException e) {

            return Response.status(500).entity(Map.of("ERROR",e.getMessage())).build();
		}
	}
}