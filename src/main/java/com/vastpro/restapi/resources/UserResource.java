package com.vastpro.restapi.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

import org.apache.http.HttpStatus;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.webapp.control.LoginWorker;

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
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			Map<String, Object> input = new HashMap<String, Object>();
			input.put("userLoginId", request.getAttribute("userLoginId"));
			input.put("currentPassword", request.getAttribute("password"));

			request.setAttribute("USERNAME", request.getAttribute("userLoginId"));
			request.setAttribute("PASSWORD", request.getAttribute("password"));

			if ("success".equalsIgnoreCase(LoginWorker.login(request, response))) {

				HttpSession session = request.getSession(false);
				if (UtilValidate.isNotEmpty(session)) {
					GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
					if (UtilValidate.isNotEmpty(userLogin)) {
						GenericValue userRole = EntityQuery.use(delegator).from("PartyRole")
										.where("partyId", userLogin.getString("partyId")).queryFirst();
						session.setAttribute("userRole", userRole.getString("roleTypeId"));
						session.setAttribute("partyId", userRole.getString("partyId"));
						session.setAttribute("userLoginId", userLogin.getString("userLoginId"));
						input.put("role", userRole.getString("roleTypeId"));
						input.put("partyId", userRole.getString("partyId"));
						/*
						 * System.out.println("++++++++++++++++++++++userLogin: " + userLogin);
						 * System.out.println("++++++++++++++++++++++userRole: " + userRole);
						 * System.out.println("++++++++++++++++++++++role: " + session.getAttribute("userRole"));
						 * System.out.println("++++++++++++++++++++++role: " + session.getAttribute("partyId"));
						 */
					}
				}
				return Response.status(HttpStatus.SC_OK).entity(UtilMisc.toMap("success", "Signed In Successfully!", "result", input))
								.build();
			} else {
				return Response.status(HttpStatus.SC_BAD_REQUEST)
								.entity(ServiceUtil.returnError((String) request.getAttribute("_ERROR_MESSAGE_"))).build();
			}

			// Map<String, Object> result = dispatcher.runSync("signIn", input);
			// if ("success".equals(result.get("responseMessage"))) {
			// return Response.ok(Map.of("status", "success", "message", result.get("successMessage"), "role", result.get("role")))
			// .build();
			// } else {
			// return Response.status(401).entity(Map.of("status", "error", "message", "Invalid Credinatilas")).build();
			// }

		} catch (Exception e) {
			return Response.status(500).entity(Map.of("status", "error", "message", "Invalid credentials")).build();
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
		HttpSession session = request.getSession(false);
		GenericValue userLogin = null;
		if (UtilValidate.isNotEmpty(session)) {
			userLogin = (GenericValue) session.getAttribute("userLogin");
		}
		Map<String, Object> user = new HashMap<String, Object>();
		user.put("userName", request.getAttribute("userName"));
		user.put("firstName", request.getAttribute("firstName"));
		user.put("lastName", request.getAttribute("lastName"));
		user.put("phNo", request.getAttribute("phNo"));
		user.put("email", request.getAttribute("email"));
		user.put("password", request.getAttribute("password"));
		user.put("role", request.getAttribute("role"));
		user.put("userLogin", userLogin);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		if (dispatcher == null) {
			return Response.status(500).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			try {

				Map<String, Object> result = dispatcher.runSync("regService", user);
				if (result.get("responseMessage").equals("success")) {
					return Response.ok(UtilMisc.toMap("success", result.get("successMessage"))).build();
				} else {
					return Response.status(Status.NOT_ACCEPTABLE).entity(UtilMisc.toMap("error", "not created")).build();
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
			HttpSession session = request.getSession(false);
			if (UtilValidate.isEmpty(session)) {
				return Response.status(Status.UNAUTHORIZED).entity(UtilMisc.toMap("error", "pls login first")).build();
			}
			try {
				Map<String, Object> input = new HashMap<>();
				input.put("examId", request.getAttribute("examId"));
				String serviceType = (String) request.getAttribute("servicetype");
				Map<String, Object> result;
				if (serviceType.equals("assigned")) {
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
		HttpSession session = request.getSession(false);
		if (UtilValidate.isEmpty(session)) {
			return Response.status(Status.UNAUTHORIZED).entity(UtilMisc.toMap("error", "pls login first")).build();
		}
		try {

			List<Map<String, Object>> list = (List<Map<String, Object>>) request.getAttribute("allData");

			Map<String, Object> result = dispatcher.runSync("examrelationshipcreate", UtilMisc.toMap("allData", list));
			Debug.log("===success=>====>email result===>\n\n" + result);
			if (result.get("responseMessage").equals("success")) {
				return Response.ok(UtilMisc.toMap("success", result.get("successMessage"))).build();
			} else {
				return Response.status(Status.NOT_ACCEPTABLE).entity(UtilMisc.toMap("error", "alreadycreated")).build();
			}
		} catch (GenericServiceException e) {
			Debug.log("===error=>====>email===>\n\n" + e);
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
		// if (dispatcher == null) {
		// return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		// } else {
		// HttpSession session = request.getSession(false);
		// if (UtilValidate.isEmpty(session)) {
		// return Response.status(Status.UNAUTHORIZED).entity(UtilMisc.toMap("error", "pls login first")).build();
		// }

		HttpSession session = request.getSession(false);
		GenericValue userLogin = null;
		if (UtilValidate.isNotEmpty(session)) {
			userLogin = (GenericValue) session.getAttribute("userLogin");
		}

		Debug.log("-----------------------------------------" + session.getAttribute("partyId"));

		Map<String, Object> input = new HashMap<>();

		input.put("firstName", request.getAttribute("firstName"));
		input.put("lastName", request.getAttribute("lastName"));
		input.put("userName", request.getAttribute("userName"));
		input.put("email", request.getAttribute("email"));
		input.put("role", request.getAttribute("role"));
		// input.put("userLogin", userLogin);
		input.put("userLoginId", request.getAttribute("userLoginId"));

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

	@POST
	@Path("/partyExamCreate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response AsignTempoary(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			HttpSession session = request.getSession(false);
			if (UtilValidate.isEmpty(session)) {
				return Response.status(Status.UNAUTHORIZED).entity(UtilMisc.toMap("error", "pls login first")).build();
			}
			Map<String, Object> input = new HashMap<>();

			input.put("examId", request.getAttribute("examId"));
			input.put("partyId", request.getAttribute("partyId"));
			input.put("noOfAttempts", request.getAttribute("noOfAttempts"));
			input.put("allowedAttempts", request.getAttribute("allowedAttempts"));
			input.put("timeoutDays", request.getAttribute("timeoutDays"));
			input.put("userLoginId", request.getAttribute("userLoginId"));

			Map<String, Object> result;
			try {
				result = dispatcher.runSync("partyExamCreate", input);
				if (result.get("responseMessage").equals("success")) {
					return Response.status(Status.OK).entity(UtilMisc.toMap("success", result.get("responseMessage"))).build();
				} else {
					return Response.status(Status.NOT_MODIFIED).entity(UtilMisc.toMap("success", result.get("responseMessage"))).build();
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
			HttpSession session = request.getSession(false);
			if (UtilValidate.isEmpty(session)) {
				return Response.status(Status.UNAUTHORIZED).entity(UtilMisc.toMap("error", "pls login first")).build();
			}
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
			HttpSession session = request.getSession(false);
			if (UtilValidate.isEmpty(session)) {
				return Response.status(Status.UNAUTHORIZED).entity(UtilMisc.toMap("error", "pls login first")).build();
			}
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

	// assignTempoaryUpdate
	@Path("/asssignTempoaryUpdate")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response assignTemparyUpdate(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			HttpSession session = request.getSession(false);
			if (UtilValidate.isEmpty(session)) {
				return Response.status(Status.UNAUTHORIZED).entity(UtilMisc.toMap("error", "pls login first")).build();
			}
			Map<String, Object> input = new HashMap<>();
			input.put("examId", request.getAttribute("examId"));
			input.put("partyId", request.getAttribute("partyId"));
			input.put("noOfAttempts", request.getAttribute("noOfAttempts"));
			input.put("allowedAttempts", request.getAttribute("allowedAttempts"));
			input.put("timeoutDays", request.getAttribute("timeoutDays"));
			try {
				Map<String, Object> result = dispatcher.runSync("asssignTempoaryUpdate", input);
				if (result.get("responseMessage").equals("success")) {
					return Response.status(Status.OK).entity(UtilMisc.toMap("success", result.get("successMessage"))).build();
				}
				return Response.status(Status.NOT_MODIFIED).entity(UtilMisc.toMap("success", result.get("successMessage"))).build();
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();

			}

		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/deleteAssign")
	public Response deleteAssign(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			Map<String, Object> input = new HashMap<>();

			input.put("partyId", request.getAttribute("partyId"));
			input.put("examId", request.getAttribute("examId"));
			Map<String, Object> result;
			try {
				result = dispatcher.runSync("deleteAssign", input);
				if (result.get("responseMessage").equals("success")) {
					return Response.status(Status.OK).entity(UtilMisc.toMap("success", result.get("successMessage"))).build();
				}

				return Response.status(Status.NOT_FOUND).entity(UtilMisc.toMap("error", result.get("errorMessage"))).build();
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
			HttpSession session = request.getSession(false);
			if (UtilValidate.isEmpty(session)) {
				return Response.status(Status.UNAUTHORIZED).entity(UtilMisc.toMap("error", "pls login first")).build();
			}
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

	@POST
	@Path("/submited-answer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitedAnswer(@Context HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			HttpSession session = request.getSession(false);
			if (UtilValidate.isEmpty(session)) {
				return Response.status(Status.UNAUTHORIZED).entity(UtilMisc.toMap("error", "pls login first")).build();
			}
			Map<String, Object> input = new HashMap<>();

			input.put("questionId", request.getAttribute("questionId"));
			input.put("examId", request.getAttribute("examId"));
			// input.put("partyId", session.getAttribute("partyId"));
			input.put("userLoginId", request.getAttribute("userLoginId"));
			input.put("submittedAnswer", request.getAttribute("submittedAnswer"));
			// input.put("sNo", request.getAttribute("sNo"));
			// input.put("isFlagged", request.getAttribute("isFlagged"));
			input.put("sNo", request.getAttribute("sNo"));
			input.put("isFlagged", request.getAttribute("isFlagged"));
			try {
				Map<String, Object> result = dispatcher.runSync("submitedAnswer", input);
				if (result.get("responseMessage").equals("success")) {
					return Response.status(Status.OK).entity(UtilMisc.toMap("success", result.get("successMessage"))).build();
				}
				return Response.status(Status.NOT_MODIFIED).entity(UtilMisc.toMap("error", result.get("errorMessage"))).build();
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
			}

		}
	}

	// DetailedPartyPerformance
	@POST
	@Path("/detailed-party-performance")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response DetailedPartyPerformance(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			HttpSession session = request.getSession(false);
			if (UtilValidate.isEmpty(session)) {
				return Response.status(Status.UNAUTHORIZED).entity(UtilMisc.toMap("error", "pls login first")).build();
			}
			Map<String, Object> input = new HashMap<>();
			input.put("partyId", session.getAttribute("partyId"));
			input.put("examId", request.getAttribute("examId"));
			input.put("topicId", request.getAttribute("topicId"));
			input.put("topicPassPercentage", request.getAttribute("topicPassPercentage"));
			input.put("userTopicPercentage", request.getAttribute("userTopicPercentage"));
			input.put("correctQuestionsInThisTopic", request.getAttribute("correctQuestionsInThisTopic"));
			input.put("totalQuestionsInThisTopic", request.getAttribute("totalQuestionsInThisTopic"));
			input.put("userPassedInThisTopic", request.getAttribute("userPassedInThisTopic"));
			input.put("performanceId", request.getAttribute("performanceId"));
			input.put("detailedPerformanceId", request.getAttribute("detailedPerformanceId"));

			try {
				Map<String, Object> result = dispatcher.runSync("detailedPartyPerformance", input);
				if (result.get("responseMessage").equals("success")) {
					return Response.status(Status.OK).entity(UtilMisc.toMap("success", result.get("successMessage"))).build();
				}
				return Response.status(Status.NOT_MODIFIED).entity(UtilMisc.toMap("error", result.get("errorMessage"))).build();
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
			}

		}
	}

	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response validateUser(@Context HttpServletRequest request, @Context HttpServletResponse response) {

		try {
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			if (dispatcher == null) {
				dispatcher = ServiceContainer.getLocalDispatcher("sphinx", (Delegator) request.getAttribute("delegator"));
			}
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			if (UtilValidate.isEmpty(request.getAttribute("userName"))) {

			}

			request.setAttribute("USERNAME", request.getAttribute("userName"));
			request.setAttribute("PASSWORD", request.getAttribute("password"));
			String msg = LoginWorker.login(request, response);
			System.out.println(msg);
			if ("success".equalsIgnoreCase(LoginWorker.login(request, response))) {
				Map<String, Object> result = ServiceUtil.returnSuccess("Logged In Successfully!");
				HttpSession session = request.getSession(false);
				if (UtilValidate.isNotEmpty(session)) {
					GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
					if (UtilValidate.isNotEmpty(userLogin)) {
						GenericValue userRole = EntityQuery.use(delegator).from("PartyRole")
										.where("partyId", userLogin.getString("partyId")).queryFirst();
						session.setAttribute("userRole", userRole);
						result.put("role", userRole.getString("roleTypeId"));
						result.put("partyId", userRole.getString("partyId"));
					}
				}

				return Response.status(200).entity(result).build();
			}
			Map<String, Object> result = ServiceUtil.returnError((String) request.getAttribute("_ERROR_MESSAGE_"));

			return Response.ok(result).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity(Map.of("error", e.getMessage())).build();
		}
	}

	@Path("/submit-final")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response finalSubmit(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			Map<String, Object> input = new HashMap<>();
			input.put("userLoginId", request.getAttribute("userLoginId"));
			input.put("examId", request.getAttribute("examId"));
			try {
				Map<String, Object> result = dispatcher.runSync("finalSubmit", input);
				if (result.get("responseMessage").equals("success")) {
					return Response.status(Status.OK)
									.entity(UtilMisc.toMap("success", result.get("successMessage"), "skipped", result.get("skipped")))
									.build();
				}
				return Response.status(Status.BAD_GATEWAY).entity(UtilMisc.toMap("error", result.get("errorMessage"))).build();

			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
			}

		}
	}

	@POST
	@Path("/exam-result")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response examResult(@Context HttpServletRequest request) {
		try {
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			if (dispatcher == null) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
			}
			String examId = (String) request.getAttribute("examId");
			String userLoginId = (String) request.getAttribute("userLoginId");
			Map<String, Object> result = dispatcher.runSync("examResult", UtilMisc.toMap("examId", examId, "userLoginId", userLoginId));
			if (result.get("responseMessage").equals("success")) {
				return Response.status(Status.OK).entity(UtilMisc.toMap("result", result.get("result"))).build();
			} else {
				return Response.status(Status.NO_CONTENT).entity(UtilMisc.toMap("error", "not found data")).build();
			}

		} catch (GenericServiceException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
		}
	}

	// validate
	@POST
	@Path("validate-exam")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response validateAnswer(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}

		Map<String, Object> input = new HashMap<String, Object>();

		String partyId = (String) request.getParameter("partyId");
		input.put("partyId", partyId);
		String examId = (String) request.getParameter("examId");
		input.put("examId", examId);

		try {
			Map<String, Object> result = dispatcher.runSync("validateExam", input);
			if (ServiceUtil.isError(result)) {
				return Response.status(Response.Status.NOT_FOUND).entity(UtilMisc.toMap("error", result.get("errorMessage"))).build();
			} else {

				return Response.ok(result).build();
			}
		} catch (GenericServiceException e) {

			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();

		}

	}

	/**
	 * This class is responsible for sending email
	 */
	@POST
	@Path("/send-email")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendEmail(@Context HttpServletRequest request) {
		try {
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			dispatcher.runSync("sendEmailService", UtilMisc.toMap("examId", request.getAttribute("examId")));
			return Response.status(67).build();
		} catch (GenericServiceException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
		}
	}

	@POST
	@Path("/getUserReport")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserReport(@Context HttpServletRequest request) {
		try {
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

			if (dispatcher == null) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found"))
								.build();
			}

			Map<String, Object> input = new HashMap<>();
			input.put("userLoginId", request.getAttribute("userLoginId"));

			Map<String, Object> result = dispatcher.runSync("getUserReport", input);

			if (ServiceUtil.isSuccess(result)) {
				return Response.status(Response.Status.OK).entity(UtilMisc.toMap("data", result.get("data"))).build();
			} else {
				return Response.status(Response.Status.NO_CONTENT).entity(UtilMisc.toMap("error", result.get("errorMessage"))).build();
			}

		} catch (GenericServiceException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occurred, try again later")).build();
		}
	}
}
