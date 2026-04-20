package com.vastpro.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityFunction;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class CreateTopic {
	public Map<String, Object> createTopic(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		String topicName = (String) input.get("topicName");
		LocalDispatcher dispatcher = context.getDispatcher();

		// 1. Validate input
		if (topicName == null || topicName.trim().isEmpty()) {
			return ServiceUtil.returnError("Topic name cannot be empty");
		}

		topicName = topicName.trim();

		try {
			// 2. Case-insensitive check using UPPER (FIXED)
			EntityCondition condition = EntityCondition.makeCondition(EntityFunction.upperField("topicName"), EntityOperator.EQUALS,
							topicName.toUpperCase());

			GenericValue existingTopic = EntityQuery.use(delegator).from("TopicMaster").where(condition).queryFirst();

			if (existingTopic != null) {
				return ServiceUtil.returnError("Topic already exists. Try another name.");
			}

			// 3. Generate ID
			String topicId = "SPX_TM_" + delegator.getNextSeqId("TopicMaster");

			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put("topicId", topicId);
			inputs.put("topicName", topicName);

			Map<String, Object> result;

			result = dispatcher.runSync("insertTopicAuto", inputs);

			if (ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("Topic Not Created");
			} else {
				return ServiceUtil.returnSuccess("Topic Created Successfully");
			}

		} catch (GenericEntityException | GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Error while creating topic: " + e.getMessage());
		}
	}

	public Map<String, Object> getAllTopics(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess("Topic getted successfully");
		try {
			// List<GenericValue> topics=delegator.findAll("TopicMaster", false);
			List<GenericValue> topicList = EntityQuery.use(delegator).from("TopicMaster").orderBy("-lastUpdatedStamp").queryList();

			if (topicList.size() == 0) {
				return ServiceUtil.returnSuccess("no topic found");
			}
			result.put("topic", topicList);
			return result;
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnFailure("error while reterving");
		}
	}

	public Map<String, Object> deleteById(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		LocalDispatcher dispatcher = context.getDispatcher();
		String topicId = (String) input.get("topicId");

		try {

			GenericValue topicMaster = EntityQuery.use(delegator).from("TopicMaster").where("topicId", topicId).queryOne();

			List<GenericValue> questionMaster = EntityQuery.use(delegator).from("QuestionMaster").where("topicId", topicId).queryList();

			if (questionMaster != null && !questionMaster.isEmpty()) {
				delegator.removeAll(questionMaster);
				System.out.println("Deleted " + questionMaster.size() + " existing questions for examId: " + topicId);
			}
			List<GenericValue> questionMasterB = EntityQuery.use(delegator).from("QuestionBankMasterB").where("topicId", topicId)
							.queryList();

			if (questionMasterB != null && !questionMasterB.isEmpty()) {
				delegator.removeAll(questionMasterB);
				System.out.println("Deleted " + questionMasterB.size() + " existing questions for examId: " + topicId);
			}
			List<GenericValue> examTopicDetails = EntityQuery.use(delegator).from("ExamTopicDetails").where("topicId", topicId).queryList();

			if (examTopicDetails != null && !examTopicDetails.isEmpty()) {
				delegator.removeAll(examTopicDetails);
				System.out.println("Deleted " + examTopicDetails.size() + " existing questions for examId: " + topicId);
			}
			if (topicMaster == null) {
				return ServiceUtil.returnError("Topic Not Found");
			}
			Map<String, Object> deleteMap = new HashMap<String, Object>();
			deleteMap.put("topicId", topicId);
			Map<String, Object> result = dispatcher.runSync("deleteTopic", deleteMap);
			if (ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("Topic Not Found");
			} else {
				return ServiceUtil.returnSuccess("Topic Deleted Successfully");
			}
		} catch (GenericEntityException | GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
	}

	public Map<String, Object> updateTopic(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		LocalDispatcher dispatcher = context.getDispatcher();
		String topicId = (String) input.get("topicId");
		String topicName = (String) input.get("topicName");

		try {
			if (topicName == null || topicId == null) {
				return ServiceUtil.returnError("field cannot be empty");
			}
			GenericValue updateTopicId = EntityQuery.use(delegator).from("TopicMaster").where("topicId", topicId).queryOne();

			if (updateTopicId == null) {
				return ServiceUtil.returnError("Topic with Id" + topicId + " not found");
			}
			Map<String, Object> updateMap = new HashMap<String, Object>();
			updateMap.put("topicId", topicId);
			updateMap.put("topicName", topicName);

			Map<String, Object> result = dispatcher.runSync("updateTopic", updateMap);
			if (ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("error,occur during update topic");
			}
			return ServiceUtil.returnSuccess("topic update successfully");

		} catch (GenericEntityException | GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error,occur during update topic");
		}
	}

}
