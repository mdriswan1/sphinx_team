package com.vastpro.restapi.resources;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

/**
 * This class is used to handle topic api
 */
@Path("/topic")
public class TopicResource {
	/**
	 * Method is used to create topic
	 */
	@POST
	@Path("/createtopic")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createTopic(@Context HttpServletRequest request) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", "Dispatcher not found")).build();
		}
		try {
			Map<String, Object> input = new HashMap<String, Object>();
			input.put("topicName", (String) request.getAttribute("topicName"));

			Map<String, Object> result = dispatcher.runSync("createtopic", input);
			if (ServiceUtil.isError(result)) {
				return Response.status(404).entity(Map.of("error", result.get("errorMessage"))).build();
			} else {
				return Response.ok(result).build();
			}

		} catch (GenericServiceException e) {

			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(Map.of("error", "Unexpected error occured, try again after sometime!")).build();
		}

	}

	/**
	 * Method is used to get topics
	 */
	@GET
	@Path("/gettopics")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopics(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", "Dispatcher not found")).build();
		}
		try {
			Map<String, Object> result = dispatcher.runSync("getAllTopic", Map.of());
			return Response.ok(result).build();

		} catch (GenericServiceException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(Map.of("error", "Unexpected error occured, try again after sometime!")).build();

		}
	}

	/**
	 * Method is used to delete topic
	 */
	@DELETE
	@Path("/deletetopic")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTopic(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		if (dispatcher == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
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
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(Map.of("error", "Unexpected error occured, try again after sometime!")).build();
		}
	}

	/**
	 * Method is used to update topic
	 */
	@PUT
	@Path("/updatetopic")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateTopic(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		try {
			Map<String, Object> input = new HashMap<String, Object>();
			input.put("topicId", request.getAttribute("topicId"));
			input.put("topicName", request.getAttribute("topicName"));
			Map<String, Object> result = dispatcher.runSync("updateTopicOwn", input);
			return Response.ok(result).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(Map.of("error", "Unexpected error occured, try again after sometime!")).build();
		}
	}

}
