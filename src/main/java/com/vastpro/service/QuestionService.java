package com.vastpro.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

/**
 * CreateQuestion
 * 
 * This class responsible for doing CRUD operation with questions
 */
public class QuestionService {

	public static Map<String, Object> createQuestionMaster(DispatchContext dctx, Map<String, Object> context) {

		Map<String, Object> input = new HashMap<>();
		Delegator delegator = dctx.getDelegator();
		try {
//			GenericValue topic = delegator.findOne("TopicMaster	", false, Map.of("topicId", context.get("topicId"))); 
//			if(topic == null) {
//				return ServiceUtil.returnError("topic not defined");
//			}
			Long questionId = Long.valueOf(delegator.getNextSeqId("QuestionMaster"));
			String topicId = (String) context.get("topicId");
			LocalDispatcher dispatcher = dctx.getDispatcher();
			String questionDetail = (String) context.get("questionDetail");
			String optionA = (String) context.get("optionA");
			String optionB = (String) context.get("optionB");
			String optionC = (String) context.get("optionC");
			String optionD = (String) context.get("optionD");
			String optionE = (String) context.get("optionE");
			String answer = (String) context.get("answer");
			long numAnswers = (Integer.parseInt((String)context.get("numAnswers")));
			String questionType = (String)context.get("questionType");
			long difficultyLevel =  (Integer.parseInt((String)context.get("difficultyLevel")));
			long answerValue =  (Integer.parseInt((String)context.get("answerValue")));
			long negMarkValue =  (Integer.parseInt((String)context.get("negativeMarkValue")));
			
			input.put("questionId", questionId);
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
			Map<String, Object> res = dispatcher.runSync("createQuestionMaster", input);

			Map<String, Object> result = new HashMap<>();
			if (ServiceUtil.isError(res)) {
				Debug.log("built in service return value: " + input);
				result.put("Status", "ERROR");
				result.put("message",ServiceUtil.getErrorMessage(res));
				return result;

			}

			result.put("status", "Success");
			result.put("message", "Updated Successfully");
			return result;
		} catch (Exception e) {
			Debug.log("Question Master: " + e.getMessage());
			
			return Map.of("message", e.getMessage());
		}
		}
	
	public static Map<String, Object> updateQuestionMaster(DispatchContext dctx, Map<String, ? extends Object> context) {
		try {
			
		}catch(Exception e) {
			
		}
		return null;
	}
     public static Map<String, Object> createQuesBankB(DispatchContext dctx, Map<String, ? extends Object> context) {

    	 Delegator delegator = dctx.getDelegator();

    	 try {
    	     GenericValue questionMaster = delegator.findOne("questionMaster", 
    	         UtilMisc.toMap("questionId", context.get("questionId")), false);

    	     if (questionMaster == null) {
    	         return ServiceUtil.returnError("Question not found");
    	     }

    	     GenericValue questionBankMasterB = delegator.makeValue("QuestionBankMasterB");

    	     int totalQuestion = 0;
    	     int noQues = (int)context.get("noQues");//50
    	     int percentage = (int)context.get("percentage");//30%
    	     
    	     totalQuestion = noQues * percentage/100;//15
    	     
    	     questionBankMasterB.set("questionId", questionMaster.get("questionId"));
    	     questionBankMasterB.set("topicId", questionMaster.get("topicId"));
    	     questionBankMasterB.set("questionDetail", questionMaster.get("questionDetail"));
    	     questionBankMasterB.set("optionA", questionMaster.get("optionA"));
    	     questionBankMasterB.set("optionB", questionMaster.get("optionB"));
    	     questionBankMasterB.set("optionC", questionMaster.get("optionC"));
    	     questionBankMasterB.set("optionD", questionMaster.get("optionD"));
    	     questionBankMasterB.set("optionE", questionMaster.get("optionE"));
    	     questionBankMasterB.set("answer", questionMaster.get("answer"));
    	     questionBankMasterB.set("numAnswers", questionMaster.get("numAnswers"));
    	     questionBankMasterB.set("questionType", questionMaster.get("questionType"));
    	     questionBankMasterB.set("difficultyLevel", questionMaster.get("difficultyLevel"));
    	     questionBankMasterB.set("answerValue", questionMaster.get("answerValue"));
    	     questionBankMasterB.set("negativeMarkValue", questionMaster.get("negativeMarkValue"));

    	     delegator.create(questionBankMasterB);

    	     return ServiceUtil.returnSuccess("Copied successfully");

    	 } catch (GenericEntityException e) {
    	     return ServiceUtil.returnError("Error: " + e.getMessage());
    	 }
}
     
     public static Map<String, Object> createQuesBankMaster(DispatchContext dctx, Map<String, ? extends Object> context) {

  		
    	 Delegator delegator = dctx.getDelegator();

    	 try {
    	     int noQues = (int) context.get("noQues");
    	     int percentage = (int) context.get("percentage");
    	     String topicId = (String) context.get("topicId");

    	     int totalQuestion = noQues * percentage / 100;

    	     List<GenericValue> questionList = delegator.findAll(
    	         "QuestionMaster",
    	         false
    	     );
    	     
    	     EntityQuery.use(delegator).from("QuestionMaster").where(UtilMisc.toMap("q","1", "c","2"));

    	     if (questionList == null || questionList.isEmpty()) {
    	         return ServiceUtil.returnError("No questions found for topic");
    	     }

    	     
    	     Collections.shuffle(questionList);

    	     int limit = Math.min(totalQuestion, questionList.size());

    	     for (int i = 0; i < limit; i++) {
    	         GenericValue questionMaster = questionList.get(i);

    	         GenericValue questionBankMasterB = delegator.makeValue("QuestionBankMasterB");

    	         questionBankMasterB.setAllFields(questionMaster, false, null, null);

    	         delegator.create(questionBankMasterB);
    	     }

    	     return ServiceUtil.returnSuccess("Inserted " + limit + " questions");

    	 } catch (Exception e) {
    	     return ServiceUtil.returnError("Error: " + e.getMessage());
    	 }}
}