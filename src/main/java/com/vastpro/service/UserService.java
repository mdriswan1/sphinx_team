package com.vastpro.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ntp.TimeStamp;
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

					if (roleTypeId.equals("SPX_USER")) {
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

					Map<String, Object> res = dispatcher.runSync("sendEmailToUser", obj);
					if (ServiceUtil.isError(res)) {
						return ServiceUtil.returnError((String) result.get("errorMessage"));
					}
					// Map<String, Object> result1 = dispatcher.runSync("deleteAssignTempoary", obj);

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
		if (input.get("partyId") == null || input.get("partyId").equals("")) {
			errMsg += "question id is null";
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
			input.put("isFlagged", Long.parseLong((String) input.get("isFlagged")));
			input.put("sNo", Long.parseLong((String) input.get("sNo")));
			input.put("questionId", Long.parseLong((String) input.get("questionId")));
			Map<String, Object> result = dispatcher.runSync("autosubmittedAnswer", input);
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

	public Map<String, Object> partyPerformance(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		}
		String errMsg = "";
		if (input.get("partyId") == null || input.get("partyId").equals("")) {
			errMsg += "partyId  is null";
		}
		if (input.get("examId") == null || input.get("examId").equals("")) {
			errMsg += "examId id is null";
		}
		if (input.get("score") == null || input.get("score").equals("")) {
			errMsg += "score id is null";
		}
		if (input.get("date") == null || input.get("date").equals("")) {
			errMsg += "date id is null";
		}
		if (input.get("noOfQuestions") == null || input.get("noOfQuestions").equals("")) {
			errMsg += "noOfQuestions id is null";
		}
		if (input.get("totalCorrect") == null || input.get("totalCorrect").equals("")) {
			errMsg += "totalCorrect id is null";
		}
		if (input.get("totalWrong") == null || input.get("totalWrong").equals("")) {
			errMsg += "totalWrong id is null";
		}
		if (input.get("userPassed") == null || input.get("userPassed").equals("")) {
			errMsg += "userPassed id is null";
		}
		if (input.get("performanceId") == null || input.get("performanceId").equals("")) {
			errMsg += "performanceId id is null";
		}
		if (input.get("attemptNo") == null || input.get("attemptNo").equals("")) {
			errMsg += "attemptNo id is null";
		}
		if (!errMsg.equals("")) {
			return ServiceUtil.returnError(errMsg);
		}
		try {
			input.put("score", ((BigDecimal) input.get("score")));
			input.put("noOfQuestions", Long.parseLong((String) input.get("sNo")));
			input.put("totalCorrect", Long.parseLong((String) input.get("sNo")));
			input.put("totalWrong", Long.parseLong((String) input.get("sNo")));
			input.put("userPassed", Long.parseLong((String) input.get("sNo")));
			input.put("performanceId", Long.parseLong((String) input.get("sNo")));
			input.put("attemptNo", Long.parseLong((String) input.get("sNo")));
			input.put("date", ((TimeStamp) input.get("date")));
			Map<String, Object> result = dispatcher.runSync("autoPartyPerformance", input);
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

	public Map<String, Object> detailedPartyPerformance(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		}
		String errMsg = "";
		if (input.get("partyId") == null || input.get("partyId").equals("")) {
			errMsg += "partyId id is null";
		}
		if (input.get("examId") == null || input.get("examId").equals("")) {
			errMsg += "examId id is null";
		}
		if (input.get("topicId") == null || input.get("topicId").equals("")) {
			errMsg += "topicId id is null";
		}
		if (input.get("topicPassPercentage") == null || input.get("topicPassPercentage").equals("")) {
			errMsg += "topicPassPercentage id is null";
		}
		if (input.get("userTopicPercentage") == null || input.get("userTopicPercentage").equals("")) {
			errMsg += "userTopicPercentage id is null";
		}
		if (input.get("correctQuestionsInthisTopic") == null || input.get("correctQuestionsInthisTopic").equals("")) {
			errMsg += "correctQuestionsInthisTopic id is null";
		}
		if (input.get("totalQuestionsInThisTopic") == null || input.get("totalQuestionsInThisTopic").equals("")) {
			errMsg += "totalQuestionsInThisTopic id is null";
		}
		if (input.get("userPassedThisTopic") == null || input.get("userPassedThisTopic").equals("")) {
			errMsg += "userPassedThisTopic id is null";
		}
		if (input.get("performanceId") == null || input.get("performanceId").equals("")) {
			errMsg += "performanceId id is null";
		}

		if (!errMsg.equals("")) {
			return ServiceUtil.returnError(errMsg);
		}
		try {
			input.put("userTopicPercentage", ((BigDecimal) input.get("userTopicPercentage")));
			input.put("topicPassPercentage", ((BigDecimal) input.get("topicPassPercentage")));
			input.put("performanceId", Long.parseLong((String) input.get("performanceId")));
			input.put("userPassedThisTopic", Long.parseLong((String) input.get("userPassedThisTopic")));
			input.put("totalQuestionsInThisTopic", Long.parseLong((String) input.get("totalQuestionsInThisTopic")));
			input.put("correctQuestionsInthisTopic", Long.parseLong((String) input.get("correctQuestionsInthisTopic")));

			Map<String, Object> result = dispatcher.runSync("autoPartyPerformance", input);
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

}
