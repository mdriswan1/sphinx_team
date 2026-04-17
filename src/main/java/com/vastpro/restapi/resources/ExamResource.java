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
 * This class is used to handle exam api requests
 */
@Path("/exam")
public class ExamResource {

	/**
	 * Method is used to create exam
	 */
	@POST
	@Path("/createexam")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createExam(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}

		Map<String, Object> input = new HashMap<>();

		input.put("examName", request.getAttribute("examName"));
		input.put("description", request.getAttribute("description"));
		input.put("noOfQuestions", request.getAttribute("noOfQuestions"));
		input.put("duration", request.getAttribute("duration"));
		input.put("passPercentage", request.getAttribute("passPercentage"));

		try {
			Map<String, Object> result = dispatcher.runSync("examcreate", input);
			if (result.get("responseMessage").equals("success")) {
				return Response.ok(UtilMisc.toMap("status", "success", "message", result.get("successMessage"), "examId",
								result.get("examId"))).build();
			} else {
				return Response.ok(UtilMisc.toMap("error", result.get("responseMessage"))).build();
			}

		} catch (GenericServiceException e) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();

		}

	}

	/**
	 * Method is used to get exam details
	 */
	@GET
	@Path("/getexam")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExamName(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		try {
			Map<String, Object> result = dispatcher.runSync("getExam", UtilMisc.toMap());
			return Response.ok(UtilMisc.toMap("data", result)).build();
		} catch (GenericServiceException e) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
		}

	}

	/**
	 * Method is used to update exam
	 */
	@PUT
	@Path("/examUpdate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response examUpdate(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatchrer is null")).build();
		}

		Map<String, Object> input = new HashMap<>();

		input.put("examId", request.getAttribute("examId"));
		input.put("examName", request.getAttribute("examName"));
		input.put("description", request.getAttribute("description"));
		input.put("noOfQuestions", request.getAttribute("noOfQuestions"));
		input.put("duration", request.getAttribute("duration"));
		input.put("passPercentage", request.getAttribute("passPercentage"));

		try {
			Map<String, Object> result = dispatcher.runSync("examUpdate", input);
			if (result.get("responseMessage").equals("success")) {
				return Response.ok(UtilMisc.toMap("success", "updated successfully")).build();
			} else {
				return Response.ok(UtilMisc.toMap("error", "not updated")).build();
			}
		} catch (GenericServiceException e) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
		}
	}

	/**
	 * Method is used to delete exam
	 */
	@DELETE
	@Path("/examDelete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response examDelete(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		String examId = (String) request.getAttribute("examId");
		if (examId == null) {
			return Response.ok(UtilMisc.toMap("error", "examid is null")).build();
		} else {
			Map<String, Object> examDelete = new HashMap<String, Object>();
			examDelete.put("examId", examId);
			try {
				Map<String, Object> result = dispatcher.runSync("examDelete", examDelete);
				if (result.get("responseMessage").equals("success")) {
					return Response.ok(UtilMisc.toMap("success", "exam deleted")).build();
				} else {
					return Response.ok(UtilMisc.toMap("success", "exam not deleted")).build();
				}
			} catch (GenericServiceException e) {

				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();

			}
		}
	}

	/**
	 * Method is used to insert exam topic details
	 */

	@POST
	@Path("/examtopicdetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertExamTopicDetails(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		if (dispatcher == null) {
			Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		try {
			String topicPassStr = (String) request.getAttribute("topicPassPercentage");
			System.out.println(topicPassStr + "    topicpercentage");
			Double topicPassPercentage = Double.valueOf(topicPassStr);
			Map<String, Object> input = new HashMap<String, Object>();
			input.put("examId", request.getAttribute("examId"));
			input.put("topicId", request.getAttribute("topicId"));
			input.put("topicPassPercentage", topicPassPercentage);
			Map<String, Object> result = dispatcher.runSync("insertExamDetails", input);

			if (ServiceUtil.isError(result)) {
				return Response.status(Response.Status.NOT_FOUND).entity(UtilMisc.toMap("error", result.get("errorMessage"))).build();
			} else {
				System.out.println("inside done in resource");
				return Response.ok(UtilMisc.toMap("status", "success", "message", "insert successfully")).build();
			}

		} catch (GenericServiceException e) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();

		}
	}

	/**
	 * Method is used to get all exam topic details
	 */
	@GET
	@Path("/dummy")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExamTopicDetails(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		try {
			Map<String, Object> result = dispatcher.runSync("getAllExamTopics", UtilMisc.toMap());

			if (ServiceUtil.isError(result)) {
				return Response.status(Response.Status.NOT_FOUND).entity(UtilMisc.toMap("error", result.get("errorMessage"))).build();
			} else {
				System.out.println("inside done in resource");
				return Response.ok(result).build();
			}

		} catch (GenericServiceException e) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
		}
	}

	/**
	 * Method is used to get exam topics by id
	 */
	@GET
	@Path("/examtopicbyid")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExamTopicsById(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> input = new HashMap<String, Object>();
		if (dispatcher == null) {
			Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		try {
			input.put("examId", request.getParameter("examId"));
			Map<String, Object> result = dispatcher.runSync("getExamDetailsById", input);
			if (ServiceUtil.isError(result)) {
				return Response.status(Response.Status.NOT_FOUND).entity(UtilMisc.toMap("error", result.get("errorMessage"))).build();
			} else {
				System.out.println("inside done in resource");
				return Response.ok(result).build();
			}

		} catch (GenericServiceException e) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
		}
	}

	@DELETE
	@Path("/examtopicDelete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response examTopicDelete(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		try {
			Map<String, Object> input = new HashMap<String, Object>();

			String examId = (String) request.getAttribute("examId");
			input.put("examId", examId);
			String topicId = (String) request.getAttribute("topicId");
			input.put("topicId", topicId);
			Map<String, Object> result = dispatcher.runSync("deleteExamTopic", input);
			if (ServiceUtil.isError(result)) {
				return Response.status(Response.Status.NOT_FOUND).entity(UtilMisc.toMap("error", result.get("errorMessage"))).build();
			} else {

				return Response.ok(result).build();
			}

		} catch (GenericServiceException e) {
			e.printStackTrace();
			// TODO: handle exception
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();

		}
	}

	@PUT
	@Path("/examtopicUpdate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response examTopicUpdate(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> input = new HashMap<String, Object>();

		String topicPassPercentage = (String) request.getAttribute("topicPassPercentage");

		input.put("examId", request.getAttribute("examId"));
		input.put("topicId", request.getAttribute("topicId"));
		input.put("topicPassPercentage", topicPassPercentage);

		try {
			Map<String, Object> result = dispatcher.runSync("examUpdateTopic", input);

			if (ServiceUtil.isError(result)) {
				return Response.status(Response.Status.NOT_FOUND).entity(UtilMisc.toMap("error", result.get("errorMessage"))).build();
			} else {

				return Response.ok(result).build();
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();

		}

	}
	/**
	 * Method is used to delete exam topic details by id
	 */

}
