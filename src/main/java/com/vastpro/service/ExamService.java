package com.vastpro.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class ExamService {
	public static Map<String, Object> getAllExam(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		try {
			GenericValue partyId = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", input.get("userLoginId")).queryFirst();
			List<GenericValue> allExam = EntityQuery.use(delegator).from("AdminPartyExamRel").where("partyId", partyId.get("partyId"))
							.queryList();
			if (allExam == null || allExam.isEmpty()) {
				return ServiceUtil.returnError("exam not there");
			}
			List<GenericValue> value = new ArrayList<>();
			for (GenericValue exam : allExam) {
				GenericValue examMaster = EntityQuery.use(delegator).from("ExamMaster").where("examId", exam.getString("examId"))
								.queryFirst();
				value.add(examMaster);
			}
			Map<String, Object> result = ServiceUtil.returnSuccess();
			result.put("data", value);
			return result;
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("it is error");
		}

	}

	public Map<String, Object> examCreate(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		LocalDispatcher dispatcher = context.getDispatcher();

		try {
			if (input.get("examName") == null) {
				return ServiceUtil.returnError("examname is empty");
			} else {
				GenericValue examAlreadyExits = EntityQuery.use(delegator).from("ExamMaster").where("examName", input.get("examName"))
								.queryFirst();
				if (examAlreadyExits != null) {
					return ServiceUtil.returnError("the exam already exist");
				}
			}
			Map<String, Object> newVal = new HashMap<>();
			String examId = delegator.getNextSeqId("ExamMaster");
			examId = "SPX_EM_" + examId;
			newVal.put("examId", examId);
			String userLoginId = (String) input.get("userLoginId");
			GenericValue value = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userLoginId).queryFirst();
			newVal.put("partyId", value.getString("partyId"));

			dispatcher.runSync("autoCreateExamAdminParty", newVal);

			newVal.put("examName", input.get("examName"));
			GenericValue examMaster = EntityQuery.use(delegator).from("ExamMaster").where("examName", input.get("examName")).queryFirst();
			if (examMaster != null) {
				return ServiceUtil.returnError("Exam name already present");
			}
			newVal.put("description", input.get("description"));
			newVal.put("noOfQuestions", Long.valueOf((String) input.get("noOfQuestions")));
			newVal.put("duration", Long.valueOf((String) input.get("duration")));
			newVal.put("passPercentage", Long.valueOf((String) input.get("passPercentage")));

			Map<String, Object> result2 = dispatcher.runSync("examcreates", newVal);

			System.out.println(result2);

			result2.put("status", "created successfully");
			result2.put("examId", examId);

			return result2;
		} catch (GenericServiceException | GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("exception throught");

		}
	}

	public Map<String, Object> examUpdate(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		try {
			if (input.get("examId") == null) {
				return ServiceUtil.returnError("examid is null");
			} else {
				Map<String, Object> newVal = new HashMap<>();
				newVal.put("examId", input.get("examId"));

				newVal.put("examName", input.get("examName"));
				newVal.put("description", input.get("description"));
				newVal.put("noOfQuestions", Long.valueOf((String) input.get("noOfQuestions")));
				newVal.put("duration", Long.valueOf((String) input.get("duration")));
				newVal.put("passPercentage", Long.valueOf((String) input.get("passPercentage")));

				if (dispatcher == null) {
					return ServiceUtil.returnError("dispatcher is null");
				} else {
					dispatcher.runSync("examUpdates", newVal);
					return ServiceUtil.returnSuccess("it is update success");

				}

			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("it is generic servic exception " + e.getMessage());
		}
	}

	public Map<String, Object> insertExamTopicDetails(DispatchContext context, Map<String, Object> input) throws GenericEntityException {
		LocalDispatcher dispatcher = context.getDispatcher();
		Delegator delegator = context.getDelegator();

		try {
			String examId = (String) input.get("examId");
			String topicId = (String) input.get("topicId");
			double newPercentage = Double.parseDouble(input.get("topicPassPercentage").toString());
			GenericValue value = EntityQuery.use(delegator).from("ExamTopicDetails").where("examId", examId, "topicId", topicId).queryOne();
			if (value != null) {
				return ServiceUtil.returnError("Topic already exists for this exam");
			}

			List<GenericValue> details = EntityQuery.use(delegator).from("ExamTopicDetails").where("examId", examId).queryList();

			double total = 0;

			for (GenericValue det : details) {
				if (det.get("topicPassPercentage") != null) {
					total += Double.parseDouble(det.get("topicPassPercentage").toString());
				}
			}

			if (total + newPercentage > 100) {
				return ServiceUtil.returnError("Total topic percentage cannot exceed 100. Current total: " + total);
			}
			Map<String, Object> result = dispatcher.runSync("autoInsertExamT", input);

			if (ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("ExamTopic not inserted");
			} else {
				System.out.println("inside done inservice method");
				return ServiceUtil.returnSuccess("ExamTopic inserted Successfully");
			}
		} catch (GenericServiceException e) {

			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
	}

	public Map<String, Object> getAllExamTopics(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess("Topic getted successfully");
		try {

			List<GenericValue> topics = EntityQuery.use(delegator).from("ExamTopicDetail").orderBy("-lastUpdatedStamp").queryList();

			if (topics.size() == 0) {
				return ServiceUtil.returnSuccess("no topic found");

			}
			result.put("topic", topics);
			return result;

		} catch (GenericEntityException e) {

			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
	}

	public Map<String, Object> getExamTopicById(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		String examId = (String) input.get("examId");
		System.out.println("available id " + examId);
		Map<String, Object> result = ServiceUtil.returnSuccess("Topic getted successfully");
		try {

			List<GenericValue> topics = EntityQuery.use(delegator).from("ExamTopicDetail").where("examId", examId).queryList();
			if (topics.size() == 0) {
				return ServiceUtil.returnSuccess("no topic found");

			}
			result.put("topic", topics);
			return result;
		} catch (GenericEntityException e) {

			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
	}

	public Map<String, Object> deleteExamByDetails(DispatchContext context, Map<String, Object> input) {

		LocalDispatcher dispatcher = context.getDispatcher();
		Delegator delegator = context.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess("Topic deleted successfully");
		try {

			String examId = (String) input.get("examId");
			if (examId == null) {
				return ServiceUtil.returnError("examId is empty");
			}
			GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", input.get("userLoginId"))
							.queryFirst();
			Map<String, Object> contexts = new HashMap<>();
			contexts.put("examId", examId);

			List<GenericValue> questionMasterB = EntityQuery.use(delegator).from("QuestionBankMasterB").where("examId", examId).queryList();

			if (questionMasterB != null && !questionMasterB.isEmpty()) {
				delegator.removeAll(questionMasterB);

				System.out.println("Deleted " + questionMasterB.size() + " existing questions for examId: " + examId);

			}

			List<GenericValue> topicDetails = EntityQuery.use(delegator).from("ExamTopicDetails").where("examId", examId).queryList();
			if (topicDetails != null && !topicDetails.isEmpty()) {
				delegator.removeAll(topicDetails);

				System.out.println("Deleted " + topicDetails.size() + " existing questions for examId: " + examId);

			}
			List<GenericValue> partyExamRelationshipDetails = EntityQuery.use(delegator).from("PartyExamRelationship")
							.where("examId", examId).queryList();
			if (partyExamRelationshipDetails != null && !partyExamRelationshipDetails.isEmpty()) {
				delegator.removeAll(partyExamRelationshipDetails);
			}
			result = dispatcher.runSync("examDeletes", contexts);

			if (ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("exam Not Found");
			} else {
				if (userLogin != null) {
					dispatcher.runSync("autoDeleteExamAdminPartyRel",
									UtilMisc.toMap("examId", examId, "partyId", userLogin.getString("partyId")));
				}

				return ServiceUtil.returnSuccess("exam Deleted Successfully");
			}
		} catch (GenericServiceException | GenericEntityException e) {

			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());

		}
	}

	public Map<String, Object> getPartyExam(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		String examId = (String) input.get("examId");
		try {
			List<GenericValue> values = EntityQuery.use(delegator).from("ExamPartyexamMaiew").where("examId", examId).queryList();
			Map<String, Object> result = ServiceUtil.returnSuccess("success");
			if (values.isEmpty()) {
				return ServiceUtil.returnSuccess("data not found");
			}
			result.put("allData", values);
			return result;
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("exception" + e.getMessage());
		}
	}

	public Map<String, Object> deleteExamRelationship(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		try {
			Map<String, Object> result = dispatcher.runSync("autoDeleteExamRelationship", input);

			return result;
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("exception" + e.getMessage());
		}
	}

	public Map<String, Object> deleteExamTopic(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();

		try {
			Map<String, Object> result = dispatcher.runSync("deleteExamTopicAuto", input);
			if (ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("not deleted");
			} else {
				return ServiceUtil.returnSuccess("topic Deleted Successfully");
			}
		} catch (GenericServiceException e) {

			e.printStackTrace();
			return ServiceUtil.returnError("error" + e.getMessage());
		}
	}

	public Map<String, Object> examUpdateTopic(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();

		try {
			double newPercentage = Double.parseDouble(input.get("topicPassPercentage").toString());
			if (newPercentage > 100) {
				return ServiceUtil.returnError("topic must should less than  100");
			}
			input.put("topicPassPercentage", newPercentage);
			Map<String, Object> result = dispatcher.runSync("examTopicUpdateAuto", input);
			if (ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("topic not updated");
			} else {
				return ServiceUtil.returnSuccess("topic updated Successfully");
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("error" + e.getMessage());
		}
	}

	public Map<String, Object> checkOtpService(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();

		String examId = (String) input.get("examId");
		String partyId = (String) input.get("partyId");
		Long otpIn = Long.parseLong((String) input.get("otpIn"));
		try {

			GenericValue value = EntityQuery.use(delegator).from("PartyExamRelationship").where("partyId", partyId, "examId", examId)
							.queryOne();

			if (value == null) {
				return ServiceUtil.returnError("No record found");
			}

			Long dbOtp = value.getLong("passwordChangesAuto");
			if (dbOtp != null && dbOtp.equals(otpIn)) {

				Long allowedAttempts = value.getLong("allowedAttempts");
				Long noOfAttempts = value.getLong("noOfAttempts");

				allowedAttempts = (allowedAttempts == null) ? 0L : allowedAttempts;
				noOfAttempts = (noOfAttempts == null) ? 0L : noOfAttempts;

				value.set("allowedAttempts", allowedAttempts - 1);
				value.set("noOfAttempts", noOfAttempts + 1);

				value.store();

				return ServiceUtil.returnSuccess("Successfully verified");
			}
			return ServiceUtil.returnError("enter the valid password");

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("error" + e.getMessage());
		}

	}
}
