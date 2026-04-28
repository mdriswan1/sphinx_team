package com.vastpro.restapi.resources;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
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

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

/**
 * This class is used to handle question api
 */

@Path("/question")
public class QuestionResource {
	/**
	 * Method is used to create question
	 */
	@POST
	@Path("/create-question")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createQuestion(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		Debug.log("________________This is from session____________: " + session.getAttribute("partyId"));
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> resp = new HashMap<String, Object>();
		if (dispatcher == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		try {
			Map<String, Object> data = new HashMap<String, Object>();
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
				resp.put("status", "error");
				resp.put("message", ServiceUtil.getErrorMessage(result));
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
			return Response.ok(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
		}
	}

	/**
	 * Method is used to get question by id
	 */
	@POST
	@Path("/get-ques-by-id")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getQuestion(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> resp = new HashMap<String, Object>();
		if (dispatcher == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("questionId", request.getAttribute("questionId"));
			if (request.getAttribute("questionId") == null) {
				return Response.status(Response.Status.BAD_REQUEST).entity(UtilMisc.toMap("error", "questionId null")).build();
			}

			Map<String, Object> result = dispatcher.runSync("getQuesById", data);
			if (ServiceUtil.isError(result)) {
				resp.put("status", "error");
				resp.put("message", ServiceUtil.getErrorMessage(result));
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
			return Response.ok(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
		}
	}

	/**
	 * Method is used to update question
	 */
	@PUT
	@Path("/update-question")
	@Consumes(MediaType.APPLICATION_JSON)

	@Produces(MediaType.APPLICATION_JSON)
	public Response updateQuestion(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> resp = new HashMap<String, Object>();
		if (dispatcher == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		try {
			String questionId = (String) request.getAttribute("questionId");
			if (request.getAttribute("questionId") == null) {
				return Response.status(Response.Status.BAD_REQUEST).entity(UtilMisc.toMap("error", "questionId null")).build();
			}

			Map<String, Object> data = new HashMap<String, Object>();
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

			Map<String, Object> result = dispatcher.runSync("updateQuestionMaster", data);
			if (ServiceUtil.isError(result)) {
				resp.put("status", "error");
				resp.put("message", ServiceUtil.getErrorMessage(result));
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(resp).build();
			}
			resp.put("status", "SUCCESS");
			resp.put("message", "Question updated successfully");
			return Response.ok(result).build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
		}
	}

	/**
	 * Method is used to delete question
	 */
	@DELETE
	@Path("/delete-question")
	@Consumes(MediaType.APPLICATION_JSON)

	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteQuestion(@Context HttpServletRequest request) {
		Map<String, Object> resp = new HashMap<String, Object>();

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		try {
			String questionId = (String) request.getAttribute("questionId");
			if (request.getAttribute("questionId") == null) {
				return Response.status(Response.Status.BAD_REQUEST).entity(UtilMisc.toMap("error", "questionId null")).build();
			}

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("questionId", questionId);

			Map<String, Object> result = dispatcher.runSync("deleteQuesMaster", data);

			if (ServiceUtil.isError(result)) {

				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}

			resp.put("status", "SUCCESS");
			resp.put("message", "question deleted Successfully");
			return Response.ok(result).build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
		}
	}

	/**
	 * Method is used to get question by topic
	 */
	@POST
	@Path("/get-ques-by-topic")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getquesbytopic(@Context HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}

		Map<String, Object> data = new HashMap<>();
		if (request.getAttribute("topicId") == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(UtilMisc.toMap("error", "topicId not found")).build();
		}

		data.put("topicId", request.getAttribute("topicId"));

		try {
			Map<String, Object> result = dispatcher.runSync("getquesbytopic", data);

			if (ServiceUtil.isError(result)) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}

			return Response.ok(result).build();
		} catch (GenericServiceException e) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
		}
	}

	/**
	 * Method is used to upload questions file
	 */
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadQuestions(@Context HttpServletRequest request) {

		Part filePart;
		ByteBuffer buffer;
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if (dispatcher == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}
		try {
			filePart = request.getPart("file");

			if (filePart == null) {
				return Response.status(Response.Status.BAD_REQUEST).entity(ServiceUtil.returnError("File not found")).build();
			}

			String fileName = filePart.getSubmittedFileName();

			if (!fileName.endsWith(".xlsx")) {
				return Response.status(Response.Status.BAD_REQUEST).entity(ServiceUtil.returnError("Excel file required")).build();
			}

			byte[] bytes = filePart.getInputStream().readAllBytes();

			buffer = ByteBuffer.wrap(bytes);

		} catch (IOException | ServletException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(ServiceUtil.returnError("Unexpected error occured, try again after sometime!")).build();
		}

		try {
			Map<String, Object> result = dispatcher.runSync("uploadBulkQuestion", UtilMisc.toMap("file", buffer));
			if (result.get("responseMessage") != null && result.get("responseMessage").equals("error")) {
				return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
			} else {
				result.put("successMessage", "Questions uploaded successfully!");
			}

			return Response.status(Response.Status.CREATED).entity(result).build();

		} catch (GenericServiceException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(ServiceUtil.returnError("Unexpected error occured, try again after sometime!")).build();
		}

	}

	/**
	 * Method is used to generate exam questions
	 */
	@Path("/generate-Exam-Questions")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response generateExamQuestions(@Context HttpServletRequest request) {
		Map<String, Object> params = new HashMap<>();
		params.put("examId", request.getAttribute("examId")); // GET ?examId=EXAM_10000

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		if (dispatcher == null) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		} else {
			try {
				Map<String, Object> result = dispatcher.runSync("generateExamQuestions", params);

				if (result.get("responseMessage").equals("success")) {
					return Response.ok(UtilMisc.toMap("status", "success", "examName", result.get("examName"), "message",
									result.get("successMessage"))).build();
				} else {
					return Response.status(Response.Status.NOT_ACCEPTABLE).entity(UtilMisc.toMap("error", result.get("errorMessage")))
									.build();
				}

			} catch (GenericServiceException e) {

				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
								.entity(UtilMisc.toMap("error", "Internal server error, try again later")).build();
			}
		}
	}

	/**
	 * Method is used to get exam questions
	 */
	@GET
	@Path("/get-Exam-Question")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getQuestions(@Context HttpServletRequest request, @Context ServletContext context) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> input = new HashMap<String, Object>();
		if (dispatcher == null) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(UtilMisc.toMap("error", "Dispatcher not found")).build();
		}

		Map<String, Object> result;
		try {
			String examId = (String) request.getParameter("examId");
			System.out.println("exam id : " + examId);
			int offSet = Integer.parseInt((String) request.getParameter("offSet"));
			input.put("examId", examId);
			input.put("offSet", offSet);

			result = dispatcher.runSync("getQuestions", input);
			if (ServiceUtil.isError(result)) {
				return Response.status(Response.Status.NOT_ACCEPTABLE).entity(UtilMisc.toMap("error", result.get("errorMessage"))).build();
			}

			return Response.ok(result).build();

		} catch (GenericServiceException e) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)

							.entity(UtilMisc.toMap("error", "Unexpected error occured, try again after sometime!")).build();
		}

	}
}