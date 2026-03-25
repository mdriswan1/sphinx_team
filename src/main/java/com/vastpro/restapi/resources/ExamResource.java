package com.vastpro.restapi.resources;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;

import com.vastpro.utility.CreateConnection;

@Path("/exam")
public class ExamResource {
	private CreateConnection createConnection=new CreateConnection();
	
	@Context
	private ServletContext servletContext;
	
	@Context
	private HttpServletRequest request;
	
	  public Response getUserDetail(Map<String, Object> input) {
		  
		  LocalDispatcher dispatcher = createConnection.getDispatcher(servletContext);
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
				return Response.status(500).entity(Map.of("error","not created")).build();
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			return Response.status(500).entity(Map.of("error",e.getMessage())).build();
			
		}
		  
	  }
}
