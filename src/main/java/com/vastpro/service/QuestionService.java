package com.vastpro.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

/**
 * CreateQuestion
 * 
 * This class responsible for doing CRUD operation with questions
 */
public class QuestionService {

	public static Map<String, Object> createQuestionMaster(DispatchContext dctx, Map<String, ? extends Object> context) {

		
		Delegator delegator = dctx.getDelegator();
		
		GenericValue questionMaster = delegator.makeValue("questionMaster");

		questionMaster.set("questionId", context.get("questionId")); 
		questionMaster.set("topicId", context.get("topicId"));
		questionMaster.set("questionDetail", context.get("questionDetail"));
		questionMaster.set("optionA", context.get("optionA"));
		questionMaster.set("optionB", context.get("optionB"));
		questionMaster.set("optionC", context.get("optionC"));
		questionMaster.set("optionD", context.get("optionD"));
		questionMaster.set("optionE", context.get("optionE"));
		questionMaster.set("answer", context.get("answer"));
		questionMaster.set("numAnswers", context.get("numAnswers"));
		questionMaster.set("questionType", context.get("questionType"));
		questionMaster.set("difficultyLevel", context.get("difficultyLevel"));
		questionMaster.set("answerValue", context.get("answerValue"));
		questionMaster.set("negativeMarkValue", context.get("negativeMarkValue"));

     try {
		delegator.create(questionMaster);
		return ServiceUtil.returnSuccess("Question created successfully");
	} catch (GenericEntityException e) {
		return ServiceUtil.returnSuccess("Qustion cannot created");
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