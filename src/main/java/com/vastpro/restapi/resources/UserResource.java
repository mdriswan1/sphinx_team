package com.vastpro.restapi.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.ws.rs.core.Response.Status;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;

/**
 * This class is used to handle user api
 */
@Path("/user")
public class UserResource {
	/**
	 * Method is used to test post api
	 */
	@POST
	@Path("/testpost")
	public Response testPost() {
		return Response.ok("WORKING").build();
	}

	/**
	 * Method is used to test get api
	 */
	@GET
	@Path("/testget")
	public Response testget() {
		return Response.ok("WORKING").build();
	}

	/**
	 * Method is used to test delete api
	 */
	@DELETE
	@Path("/testdelete")
	public Response testDelete() {
		return Response.ok("WORKING").build();
	}

	/**
	 * Method is used to test put api
	 */
	@PUT
	@Path("/testput")
	public Response testPut() {
		return Response.ok("WORKING").build();
	}

	/**
	 * Method is used to test patch api
	 */
	@PATCH
	@Path("/testpatch")
	public Response testPatch() {
		return Response.ok("WORKING").build();
	}

	/**
	 * Method is used to login user
	 */
	@POST
	@Path("/signIn")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		try {

			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

			Map<String, Object> input = new HashMap<String, Object>();
			input.put("userLoginId", request.getAttribute("userLoginId"));
			input.put("currentPassword", request.getAttribute("password"));

			Map<String, Object> result = dispatcher.runSync("signIn", input);
			if ("success".equals(result.get("responseMessage"))) {
				return Response.ok(Map.of("status", "success", "message", result.get("successMessage"), "role", result.get("role")))
								.build();
			} else {
				return Response.status(401).entity(Map.of("status", "error", "message", "Invalid Credinatilas")).build();
			}

		} catch (Exception e) {
			return Response.status(500).entity(Map.of("status", "error", "message", "Invalid Credinatilas")).build();
		}
	}

	/**
	 * Method is used to register user
	 */
	@POST
	@Path("/signUp")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response signup(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		Map<String, Object> user = new HashMap<String, Object>();
		user.put("userName", request.getAttribute("userName"));
		user.put("firstName", request.getAttribute("firstName"));
		user.put("lastName", request.getAttribute("lastName"));
		user.put("phNo", request.getAttribute("phNo"));
		user.put("email", request.getAttribute("email"));
		user.put("password", request.getAttribute("password"));
		user.put("role", request.getAttribute("role"));
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		if (dispatcher == null) {
			return Response.status(500).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			try {

				Map<String, Object> result = dispatcher.runSync("regService", user);
				if (result.get("responseMessage").equals("success")) {
					return Response.ok(UtilMisc.toMap("success", result.get("successMessage"))).build();
				} else {
					return Response.status(Status.NOT_ACCEPTABLE).entity(UtilMisc.toMap("error", "mmm")).build();
				}
			} catch (GenericServiceException e) {

				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "internal server error ty again after some time")).build();
			}
		}

	}

	/**
	 * Method is used to get all users
	 */
	@POST
	@Path("/getAllUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUser(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			try {
				Map<String, Object> input = new HashMap<>();
				input.put("examId", request.getAttribute("examId"));
				String serviceType = (String) request.getAttribute("servicetype");
				Map<String, Object> result;
				if (serviceType.equals("assigned")) {
					input.put("userLoginId", request.getAttribute("examId"));
					result = dispatcher.runSync("getAssignedUser", input);
				} else {
					result = dispatcher.runSync("getAllUser", input);

				}

				if (result.get("responseMessage").equals("success")) {
					List<GenericValue> users = (List<GenericValue>) result.get("allUser");
					result.put("allUser", users);
					return Response.status(Status.OK).entity(result).build();
				}
				return Response.status(Status.NO_CONTENT).entity(UtilMisc.toMap("error", "no data")).build();
			} catch (GenericServiceException e) {

				return Response.status(Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
			}
		}
	}

	/**
	 * Method is used to save exam relationship
	 */
	@POST
	@Path("/saveexamrelationship")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request, @Context HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		try {
			List<Map<String, Object>> list = (List<Map<String, Object>>) request.getAttribute("allData");

			Map<String, Object> result = dispatcher.runSync("examrelationshipcreate", UtilMisc.toMap("allData", list));
			if (result.get("responseMessage").equals("success")) {
				return Response.ok(UtilMisc.toMap("success", result.get("successMessage"))).build();
			} else {
				return Response.status(Status.NOT_ACCEPTABLE).entity(UtilMisc.toMap("error", "mmm")).build();
			}
		} catch (GenericServiceException e) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "internal server error try again after some time")).build();
		}
	}

	/**
	 * Method is used to create user
	 */
	@Path("/addUser")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(@Context HttpServletRequest request, @Context HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			Map<String, Object> input = new HashMap<>();

			input.put("firstName", request.getAttribute("firstName"));
			input.put("lastName", request.getAttribute("lastName"));
			input.put("userName", request.getAttribute("userName"));
			input.put("email", request.getAttribute("email"));
			input.put("role", request.getAttribute("role"));
			try {
				Map<String, Object> result = dispatcher.runSync("regService", input);
				if (result.get("responseMessage").equals("success")) {
					return Response.status(Status.OK).entity(UtilMisc.toMap("sucess", result.get("responseMessage"))).build();
				} else {
					return Response.status(Status.NOT_MODIFIED).entity(UtilMisc.toMap("success", result.get("responseMessage"))).build();
				}

			} catch (GenericServiceException e) {

				return Response.status(Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
			}
		}

	}

	@POST
	@Path("/assigntempoary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response AsignTempoary(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			Map<String, Object> input = new HashMap<>();

			input.put("examId", request.getAttribute("examId"));
			input.put("partyId", request.getAttribute("partyId"));
			input.put("noOfAttempts", request.getAttribute("noOfAttempts"));
			input.put("allowedAttempts", request.getAttribute("allowedAttempts"));
			input.put("timeoutDays", request.getAttribute("timeoutDays"));
			input.put("userLoginId", request.getAttribute("userLoginId"));

			Map<String, Object> result;
			try {
				result = dispatcher.runSync("assignTempoary", input);
				if (result.get("responseMessage").equals("success")) {
					return Response.status(Status.OK).entity(UtilMisc.toMap("success", result.get("successMessage"))).build();
				} else {
					return Response.status(Status.NOT_MODIFIED).entity(UtilMisc.toMap("success", result.get("errorMessage"))).build();
				}
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
			}

		}
	}

	@POST
	@Path("/getPartyExam")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPartyExam(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			Map<String, Object> input = new HashMap<>();
			input.put("examId", request.getAttribute("examId"));
			Map<String, Object> result;
			try {
				result = dispatcher.runSync("getPartyExam", input);
				if (result.get("responseMessage").equals("success")) {
					return Response.status(Status.OK).entity(result).build();
				}
				return Response.status(Status.OK).entity(result).build();
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
			}

		}
	}

	@POST
	@Path("/deleteExamRelationship")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteExamRelationship(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			Map<String, Object> input = new HashMap<>();
			input.put("examId", request.getAttribute("examId"));
			input.put("partyId", request.getAttribute("partyId"));
			Map<String, Object> result;
			try {
				result = dispatcher.runSync("deleteExamRelationship", input);
				if (result.get("responseMessage").equals("success")) {
					return Response.status(Status.OK).entity(UtilMisc.toMap("success", "deleted succefully")).build();
				}
				result.put("error", "not found");
				return Response.status(Status.NOT_FOUND).entity(result).build();
			} catch (GenericServiceException e) {
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();

			}
		}
	}

	// user
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getAssignUserExam")
	public Response getAssignUserExam(@Context HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			Map<String, Object> input = new HashMap<>();
			input.put("userLoginId", request.getAttribute("userLoginId"));
			try {
				Map<String, Object> result = dispatcher.runSync("getAssignUserExam", input);
				return Response.status(Status.OK).entity(UtilMisc.toMap("userExam", result.get("userExam"), "success", "get success"))
								.build();
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();

			}

		}

	}
}
