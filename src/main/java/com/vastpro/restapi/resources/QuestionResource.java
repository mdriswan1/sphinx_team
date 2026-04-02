package com.vastpro.restapi.resources;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

/**
 * QuestionResource
 * 
 * This class handles question related Requests and Responses
 */
@Path("/question")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuestionResource {

	@POST
	@Path("/createquestion")
	public Response createQuestion(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		try {
			// transfer the data to create question service service
			// exam name, topic, question details, option A, option B, option C, option D,
			// answer

			Map<String, Object> input = new HashMap<>();
			String topicId = (String) request.getAttribute("topicId");
			String questionDetail = (String) request.getAttribute("questionDetail");
			String optionA = (String) request.getAttribute("optionA");
			String optionB = (String) request.getAttribute("optionB");
			String optionC = (String) request.getAttribute("optionC");
			String optionD = (String) request.getAttribute("optionD");
			String optionE = (String) request.getAttribute("optionE");
			String answer = (String) request.getAttribute("answer");
			long numAnswers = (Integer) request.getAttribute("numAnswers");
			String questionType = (String) request.getAttribute("questionType");
			long difficultyLevel = (Integer) request.getAttribute("difficultyLevel");
			long answerValue = (Integer) request.getAttribute("answerValue");
			long negMarkValue = (Integer) request.getAttribute("negativeMarkValue");
			input.put("topicId", topicId);
			input.put("questionDetail", questionDetail);
			input.put("optionA", optionA);
			input.put("optionB", optionB);
			input.put("optionC", optionC);
			input.put("optionD", optionD);
			input.put("optionE", optionE);
			input.put("answer", answer);
			input.put("numAnswers", numAnswers);
			input.put("questionType", questionType);
			input.put("difficultyLevel", difficultyLevel);
			input.put("answerValue", answerValue);
			input.put("negativeMarkValue", negMarkValue);

			
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			Map<String, Object> res = dispatcher.runSync("createQuestion", input);

			Map<String, Object> result = new HashMap<>();
			if (ServiceUtil.isError(res)) {
				Debug.log("built in service return value: " + input);
				result.put("Status", "ERROR");
				result.put("message",ServiceUtil.getErrorMessage(res));
				return Response.status(500).entity(result).build();

			}

			result.put("status", "Success");
			result.put("message", "Updated Successfully");
			return Response.ok(result).build();
		} catch (Exception e) {
			Debug.log("Question Master: " + e.getMessage());
			
			return Response.ok(Map.of("message", e.getMessage())).build();
		}
	}

	@POST
	@Path("/updatequestion")
	public Response updateQuestion(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		try {
			// transfer the data to create question service service
			// exam name, topic, question details, option A, option B, option C, option D,
			// answer

			Map<String, Object> input = new HashMap<>();

			String topicId = (String) request.getAttribute("topicId");
			String questionDetail = (String) request.getAttribute("questionDetail");
			String optionA = (String) request.getAttribute("optionA");
			String optionB = (String) request.getAttribute("optionB");
			String optionC = (String) request.getAttribute("optionC");
			String optionD = (String) request.getAttribute("optionD");
			String optionE = (String) request.getAttribute("optionE");
			String answer = (String) request.getAttribute("answer");
			long numAnswers = (Integer) request.getAttribute("numAnswers");
			long questionType = (Integer) request.getAttribute("questionType");
			long difficultyLevel = (Integer) request.getAttribute("difficultyLevel");
			long answerValue = (Integer) request.getAttribute("answerValue");
			long negMarkValue = (Integer) request.getAttribute("negativeMarkValue");

//		input.put("questionId", (long)request.getAttribute("questionId")); // PK is mandatory

			input.put("topicId", topicId);
			input.put("questionDetail", questionDetail);
			input.put("optionA", optionA);
			input.put("optionB", optionB);
			input.put("optionC", optionC);
			input.put("optionD", optionD);
			input.put("optionE", optionE);
			input.put("answer", answer);
			input.put("numAnswers", numAnswers);
			input.put("questionType", questionType);
			input.put("difficultyLevel", difficultyLevel);
			input.put("answerValue", answerValue);
			input.put("negativeMarkValue", negMarkValue);
			
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			Map<String, Object> res = dispatcher.runSync("updateQuestion", input);

			Map<String, Object> result = new HashMap<>();
			if (ServiceUtil.isError(res)) {
				Debug.log("built in service return value: " + input);
				result.put("Status", "ERROR");
				result.put("message",ServiceUtil.getErrorMessage(res));
				return Response.status(500).entity(result).build();

			}

			result.put("status", "Success");
			result.put("message", "Updated Successfully");
			return Response.ok(result).build();
		} catch (Exception e) {
			Debug.log("Question Master: " + e.getMessage());
			
			return Response.ok(Map.of("message", e.getMessage())).build();
		}
	}
}