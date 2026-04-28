package com.vastpro.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class UserService {

	public Map<String, Object> getAllUser(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		List<GenericValue> filteredUsers = new ArrayList<>();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			List<GenericValue> allUser = delegator.findAll("UserLogin", false);

			for (GenericValue user : allUser) {
				String partyId = user.getString("partyId");
				List<GenericValue> partyRoles = EntityQuery.use(delegator).from("PartyRole").where("partyId", partyId).queryList();
				boolean isUser = false;
				for (GenericValue partyRole : partyRoles) {
					String roleTypeId = partyRole.getString("roleTypeId");

					if (roleTypeId.equals("SPX_EXAMINEE")) {
						isUser = true;
						break;
					}
				}
				if (isUser) {
					String examId = (String) input.get("examId");
					GenericValue record = EntityQuery.use(delegator).from("PartyExamRelationship")
									.where("partyId", partyId, "examId", examId).queryOne();
					GenericValue record2 = EntityQuery.use(delegator).from("AssignExamTempoary").where("partyId", partyId, "examId", examId)
									.queryOne();
					if (record == null && record2 == null) {
						filteredUsers.add(user);
					}

				}
			}

			result.put("allUser", filteredUsers);
			return result;
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("it is exception" + e.getMessage());
		}

	}

	public Map<String, Object> createExamRelationship(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		} else {
			List<Map<String, Object>> allData = (List<Map<String, Object>>) input.get("allData");

			for (Map<String, Object> obj : allData) {
				try {
					// kamal
					// String password = new GeneratePssword().generatePassword();
					Long password = 12345678L;
					obj.put("passwordChangesAuto", password);
					// kamal
					Map<String, Object> result = dispatcher.runSync("examrelationshipcreates", obj);
					Debug.log("++++++++++++++success=>====>email result two===>\n\n" + result);

					// Map<String, Object> res = dispatcher.runSync("sendEmailToUser", obj);
					// Debug.log("--------------------success=>====>email result threee===>\n\n" + res);
					// if (ServiceUtil.isError(res)) {
					// return ServiceUtil.returnError((String) result.get("errorMessage"));
					// }

					Map<String, Object> result1 = dispatcher.runSync("deleteAssignTempoary", obj);

					if (ServiceUtil.isError(result)) {
						return ServiceUtil.returnError((String) result.get("errorMessage"));
					}

				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return ServiceUtil.returnError("it is exception" + obj + e.getMessage());
				}
			}
			return ServiceUtil.returnSuccess("succesfuly created");
		}

	}

	public Map<String, Object> assignTempoary(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		} else {
			try {

				List<String> list = (List<String>) input.get("partyId");
				String partyId = list.get(0);
				input.put("partyId", partyId);

				Map<String, Object> result = dispatcher.runSync("autoassignTempoary", input);
				if (ServiceUtil.isError(result)) {
					return ServiceUtil.returnError((String) result.get("errorMessage"));
				}
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ServiceUtil.returnError(e.getMessage());
			}

		}
		return ServiceUtil.returnSuccess("succesfuly created");
	}

	public Map<String, Object> getAssignedUser(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		}
		Delegator delegator = dispatcher.getDelegator();

		try {
			List<GenericValue> value = EntityQuery.use(delegator).from("AssignExamTempoary").where("examId", input.get("examId"))
							.queryList();
			if (value.isEmpty()) {
				return ServiceUtil.returnSuccess("no data found");
			}
			Map<String, Object> result = ServiceUtil.returnSuccess("succefully geted");
			result.put("allUser", value);
			return result;

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("exception" + e.getMessage());
		}
	}

	public Map<String, Object> asssignTempoaryUpdate(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		} else {
			try {
				if (input.get("examId") == "" || input.get("examId") == null) {
					return ServiceUtil.returnError("examId is null");

				}
				if (input.get("partyId") == "" || input.get("partyId") == null) {
					return ServiceUtil.returnError("party is null");

				}
				Map<String, Object> result = dispatcher.runSync("autoasssignTempoaryUpdate", input);

				if (ServiceUtil.isError(result)) {
					return ServiceUtil.returnError((String) result.get("errorMessage"));
				}

			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ServiceUtil.returnError(e.getMessage());
			}

		}
		return ServiceUtil.returnSuccess("succesfuly created");
	}

	public Map<String, Object> deleteAssign(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		}
		try {
			Map<String, Object> result = dispatcher.runSync("deleteAssignTempoary", input);
			if (result.get("responseMessage").equals("success")) {
				return ServiceUtil.returnSuccess("deleted success");
			}
			return ServiceUtil.returnError("not deleted");
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
	}
	// user

	public Map<String, Object> getAssignUserExam(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		}
		Delegator delegator = dispatcher.getDelegator();

		try {

			GenericValue value = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", input.get("userLoginId")).queryFirst();
			if (!value.isEmpty()) {
				Map<String, Object> result = ServiceUtil.returnSuccess();
				String partyId = value.getString("partyId");
				List<GenericValue> allExam = EntityQuery.use(delegator).from("UserLoginView").where("partyId", partyId).queryList();
				result.put("userExam", allExam);
				return result;
			}
			return ServiceUtil.returnError("User loginid not found");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("exception" + e.getMessage());

		}
	}

	public Map<String, Object> submitedAnswer(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		Delegator delegator = context.getDelegator();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		}
		String errMsg = "";
		if (input.get("questionId") == null || input.get("questionId").equals("")) {
			errMsg += "question id is null";
		}
		if (input.get("examId") == null || input.get("examId").equals("")) {
			errMsg += "examId id is null";
		}
		if (input.get("userLoginId") == null || input.get("userLoginId").equals("")) {
			errMsg += "userLoginId id is null";
		}
		if (input.get("isFlagged") == null || input.get("isFlagged").equals("")) {
			errMsg += "isFlagged id is null";
		}
		if (input.get("sNo") == null || input.get("sNo").equals("")) {
			errMsg += "sNo id is null";
		}
		if (input.get("submittedAnswer") == null || input.get("submittedAnswer").equals("")) {
			errMsg += "submittedAnswer id is null";
		}
		if (!errMsg.equals("")) {
			return ServiceUtil.returnError(errMsg);
		}
		try {
			GenericValue value = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", input.get("userLoginId")).queryFirst();
			input.put("isFlagged", Long.parseLong((String) input.get("isFlagged")));
			input.put("sNo", Long.parseLong((String) input.get("sNo")));
			input.put("questionId", (String) input.get("questionId"));

			input.put("partyId", value.getString("partyId"));
			GenericValue allreadyPresent = EntityQuery.use(delegator).from("AnswerMaster").where("questionId", input.get("questionId"),
							"partyId", value.getString("partyId"), "examId", input.get("examId")).queryFirst();
			input.remove("userLoginId");
			Map<String, Object> result = ServiceUtil.returnSuccess();
			if (allreadyPresent != null) {
				result = dispatcher.runSync("autoAnswerUpdate", input);
			} else {
				result = dispatcher.runSync("autosubmittedAnswer", input);
			}
			if (result.get("responseMessage").equals("success")) {

				return result;
			}
			return ServiceUtil.returnError("it not updated");

		}

		catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("exception" + e.getMessage());

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("exception" + e.getMessage());
		}
	}

	public Map<String, Object> detailedPartyPerformance(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		}
		String errMsg = "";
		if (input.get("partyId") == null || input.get("partyId").equals("")) {
			errMsg += "partyId id is required";
		}
		if (input.get("examId") == null || input.get("examId").equals("")) {
			errMsg += "examId id is required";
		}
		if (input.get("topicId") == null || input.get("topicId").equals("")) {
			errMsg += "topicId id is required";
		}
		if (input.get("topicPassPercentage") == null || input.get("topicPassPercentage").equals("")) {
			errMsg += "topicPassPercentage id is required";
		}
		if (input.get("userTopicPercentage") == null || input.get("userTopicPercentage").equals("")) {
			errMsg += "userTopicPercentage id is required";
		}
		if (input.get("correctQuestionsInThisTopic") == null || input.get("correctQuestionsInThisTopic").equals("")) {
			errMsg += "correctQuestionsInthisTopic id is required";
		}
		if (input.get("totalQuestionsInThisTopic") == null || input.get("totalQuestionsInThisTopic").equals("")) {
			errMsg += "totalQuestionsInThisTopic id is required";
		}
		if (input.get("userPassedInThisTopic") == null || input.get("userPassedInThisTopic").equals("")) {
			errMsg += "userPassedThisTopic id is required";
		}
		if (input.get("performanceId") == null || input.get("performanceId").equals("")) {
			errMsg += "performanceId id is required";
		}
		if (input.get("detailedPerformanceId") == null || input.get("detailedPerformanceId").equals("")) {
			errMsg += "detailedPerformanceId id is required";
		}

		if (!errMsg.equals("")) {
			return ServiceUtil.returnError(errMsg);
		}
		try {

			// input.put("userTopicPercentage", Long.parseLong((String) input.get("userTopicPercentage")));
			// input.put("topicPassPercentage", Long.parseLong((String) input.get("topicPassPercentage")));
			// input.put("performanceId", Long.parseLong((String) input.get("performanceId")));
			// input.put("userPassedInThisTopic", Long.parseLong((String) input.get("userPassedInThisTopic")));
			// input.put("totalQuestionsInThisTopic", Long.parseLong((String) input.get("totalQuestionsInThisTopic")));
			// input.put("correctQuestionsInThisTopic", Long.parseLong((String) input.get("correctQuestionsInThisTopic")));

			input.put("userTopicPercentage", Long.parseLong(input.get("userTopicPercentage").toString()));
			input.put("detailedPerformanceId", Long.parseLong(input.get("detailedPerformanceId").toString()));
			input.put("topicPassPercentage", Long.parseLong(input.get("topicPassPercentage").toString()));
			input.put("performanceId", Long.parseLong(input.get("performanceId").toString()));
			input.put("userPassedInThisTopic", Long.parseLong(input.get("userPassedInThisTopic").toString()));
			input.put("totalQuestionsInThisTopic", Long.parseLong(input.get("totalQuestionsInThisTopic").toString()));
			input.put("correctQuestionsInThisTopic", Long.parseLong(input.get("correctQuestionsInThisTopic").toString()));

			Map<String, Object> result = dispatcher.runSync("autoDetailedPartyPerformance", input);
			if (result.get("responseMessage").equals("success")) {

				return result;
			}
			return ServiceUtil.returnError("it not updated");

		}

		catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("exception" + e.getMessage());

		}
	}

	public Map<String, Object> finalSubmit(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		if (delegator == null) {
			return ServiceUtil.returnError("in service delegator is null");
		} else {
			try {
				GenericValue value = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", input.get("userLoginId"))
								.queryFirst();
				List<GenericValue> values = EntityQuery.use(delegator).from("AnswerMaster").where("exmId", input.get("examId"))
								.where("partyId", value.getString("partyId")).queryList();
				// for (GenericValue answer : values) {
				// if (answer.getLong("isFlagged") == 0) {
				// return ServiceUtil.returnError("must submit all answer");
				// }
				// }

				long totalCount = EntityQuery.use(delegator).from("QuestionBankMasterB").where("examId", input.get("examId")).queryCount();
				int total = values.size();
				if (total < totalCount - 1) {
					return ServiceUtil.returnError("must submit all answer");
				}
				return ServiceUtil.returnSuccess("submited");

			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ServiceUtil.returnError("exception" + e.getMessage());

			}
		}

	}

	public Map<String, Object> examResult(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		if (delegator == null) {
			return ServiceUtil.returnError("in service delegator is null");
		} else {
			try {
				GenericValue value = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", input.get("userLoginId"))
								.queryFirst();
				List<GenericValue> values = EntityQuery.use(delegator).from("PartyPerformance").where("exmId", input.get("examId"))
								.where("partyId", value.getString("partyId")).queryList();
				if (values.isEmpty()) {
					return ServiceUtil.returnError("no result found");
				}
				Map<String, Object> result = ServiceUtil.returnSuccess("success");
				result.put("result", values);
				return result;
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError("exception" + e.getMessage());
			}
		}

	}

	// validate
	public Map<String, Object> validateExam(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		String examId = (String) input.get("examId");
		String partyId = (String) input.get("partyId");

		try {
			GenericValue exam = EntityQuery.use(delegator).from("ExamMaster").where("examId", examId).queryOne();

			if (exam == null) {
				return ServiceUtil.returnError("exam not found");

			}

			double examPassPercentage = exam.getDouble("passPercentage");
			int totalQuestion = exam.getInteger("noOfQuestions");

			List<GenericValue> list = EntityQuery.use(delegator).from("QuestionAnswerView").queryList();

			long correctCount = list.stream().filter(gv -> gv.getString("answer").equals(gv.getString("submittedAnswer"))).count();
			int attempted = list.size();

			long wrongCount = attempted - correctCount;

			double percentage = 0;
			if (totalQuestion > 0) {
				percentage = (correctCount * 100.0) / totalQuestion;
			}

			boolean isPassed = percentage >= examPassPercentage;

			GenericValue partyvalue = EntityQuery.use(delegator).from("PartyExamRelationship").where("partyId", partyId, "examId", examId)
							.queryOne();
			Long noOfAttempts = partyvalue.getLong("noOfAttempts");

			Map<String, Object> saveInput = new HashMap<>();

			saveInput.put("performanceId", delegator.getNextSeqId("PartyPerformance"));
			saveInput.put("partyId", partyId);
			saveInput.put("examId", examId);

			saveInput.put("score", BigDecimal.valueOf(percentage));
			saveInput.put("noOfQuestions", Long.valueOf(totalQuestion));
			saveInput.put("totalCorrect", Long.valueOf(correctCount));
			saveInput.put("totalWrong", Long.valueOf(wrongCount));
			saveInput.put("userPassed", isPassed ? 1L : 0L);
			saveInput.put("attemptNo", Long.valueOf(noOfAttempts));
			saveInput.put("date", UtilDateTime.nowTimestamp());

			GenericValue partyPerf = delegator.makeValue("PartyPerformance", saveInput);
			delegator.create(partyPerf);

			// Success response
			Map<String, Object> result = ServiceUtil.returnSuccess("Exam submitted successfully");

			result.put("score", percentage);
			result.put("totalCorrect", correctCount);
			result.put("totalWrong", wrongCount);
			result.put("attemptNo", noOfAttempts);
			result.put("passed", isPassed);

			return result;

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("exam not submitted");
		}

	}

}
