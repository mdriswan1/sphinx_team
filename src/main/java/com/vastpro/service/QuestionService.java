package com.vastpro.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class QuestionService {
	public static Map<String,Object>createQuestionService(DispatchContext dctx, Map<String, Object> questions){
		LocalDispatcher dispatcher=dctx.getDispatcher();
		try {
			Delegator delegator = dctx.getDelegator();
			
			String topicId=(String)questions.get("topicId");
			String questionDetail=(String)questions.get("questionDetail");
			String questionTypeId=(String)questions.get("questionTypeId");
			String answer = (String)questions.get("answer");
			
			
			
			if(topicId==null || questionDetail==null || answer==null  ) {
				return ServiceUtil.returnError("topic Id and quetionDetail and answer are required");
			}
			
			//check topic exists
			System.out.println("topic id is :"+topicId);
			GenericValue topic =EntityQuery.use(delegator).from("TopicMaster").where("topicId",topicId).queryOne();
			
			if(topic==null) {
				return ServiceUtil.returnError("Topic not Found");
			}
			
			 if (questionTypeId!= null) {
		            GenericValue questionType = EntityQuery.use(delegator)
		                .from("Enumeration")
		                .where("enumId", questionTypeId, "enumTypeId", "QUESTION_TYPE")
		                .queryOne();

		            if (questionType == null) {
		                return ServiceUtil.returnError("Invalid questionTypeId: " + questionTypeId);
		            }
		        }
			
		String questionId="SPX_QM_"+delegator.getNextSeqId("questionMaster");
			questions.put("questionId",questionId);
			dispatcher.runSync("createQuestion", questions);
            
			Map<String, Object> result = ServiceUtil.returnSuccess("Question created Successfully");
            result.put("responseMessage", "Question created Successfully");
            result.put("questionId", questionId);
			return result;
		}catch(GenericEntityException | GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Failed to create Question");
		}
	}
	
	//Update Questions
	public static Map<String, Object> updateQuestion(DispatchContext dctx, Map<String, Object> context) {

	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher=dctx.getDispatcher();

	    try {
	    	String questionId = (String) context.get("questionId"); 

	        if (questionId.length()<3) {
	            return ServiceUtil.returnError("For update QuestonId required");
	        }

	        GenericValue question = EntityQuery.use(delegator).from("questionMaster")
	        				.where("questionId", questionId).queryOne();

	        if (question == null) {
	            return ServiceUtil.returnError("Question not present in database: " + questionId);
	        }

	        String questionDetail = (String)context.get("questionDetail");
	        String optionA = (String)context.get("optionA");
	        String optionB = (String)context.get("optionB");
	        String optionC = (String)context.get("optionC");
	        String optionD = (String)context.get("optionD");
	        String answer = (String)context.get("answer");
	        Long numAnswers = (Long)context.get("numAnswers");
	        String questionTypeId = (String)context.get("questionTypeId");
	        Long difficultyLevel = (Long)context.get("difficultyLevel");
	        BigDecimal answerValue = (BigDecimal)context.get("answerValue");
	        String topicId = (String)context.get("topicId");
	        BigDecimal negativeMarkValue = (BigDecimal)context.get("negativeMarkValue");

	        Map<String,Object> updateQuestion=new HashMap<>();
           
	        
	        updateQuestion.put("questionId", questionId);
	        updateQuestion.put("questionTypeId", questionTypeId);
	        updateQuestion.put("questionDetail", questionDetail);
	        updateQuestion.put("optionA", optionA);
	        updateQuestion.put("optionB", optionB);
	        updateQuestion.put("optionC", optionC);
	        updateQuestion.put("optionD", optionD);
	        updateQuestion.put("answer", answer);
	        updateQuestion.put("numAnswers", numAnswers);
	        updateQuestion.put("difficultyLevel", difficultyLevel);
	        updateQuestion.put("answerValue", answerValue);
	        updateQuestion.put("topicId", topicId);
	        updateQuestion.put("negativeMarkValue", negativeMarkValue);
	        

	        if (topicId != null) {
	            GenericValue topic = EntityQuery.use(delegator)
	            		.from("TopicMaster")
	            		.where("topicId", topicId)
	            		.queryOne();

	            if (topic == null) {
	                return ServiceUtil.returnError("Topic not found for in database: " + topicId);
	            }
	            
	        }

	        if (questionTypeId!= null) {
	            GenericValue questionType = EntityQuery.use(delegator)
	                .from("Enumeration")
	                .where("enumId", questionTypeId, "enumTypeId", "QUESTION_TYPE")
	                .queryOne();

	            if (questionType == null) {
	                return ServiceUtil.returnError("Invalid questionTypeId: " + questionTypeId);
	            }
	            question.set("questionTypeId", questionTypeId);
	        }

	      
	   Map<String,Object>result=  dispatcher.runSync("updateQuestion", updateQuestion);
	        
	        
	   if(ServiceUtil.isError(result)) {
			return ServiceUtil.returnError((String)result.get("errorMessage"));
		}
	   return ServiceUtil.returnSuccess("Question updated successfully");
	        
	        
	    } catch (GenericEntityException | GenericServiceException e) {
	        return ServiceUtil.returnError("Error updating question: " + e.getMessage());
	    }
	}
	
	
	//DeleteQuestionservice
	public static Map<String,Object> deleteQuestion(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher=dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		try {
			 String questionId =  (String) context.get("questionId");
			 
			 GenericValue question = EntityQuery.use(delegator)
					            .from("questionMaster")
					            .where("questionId", questionId)
					            .queryOne();
			 
			 if (question == null) {
		            return ServiceUtil.returnError("Question not found for questionId: ");	      
		        }
			 
			Map<String,Object>result= dispatcher.runSync("deleteQuestion", context);
			
			if(ServiceUtil.isError(result)) {
				return ServiceUtil.returnError((String)result.get("errorMessage"));
			}
			
			return ServiceUtil.returnSuccess("Question deleted successfully");
		  
			 
		}catch(GenericEntityException | GenericServiceException e) {
			return ServiceUtil.returnError("Error deleting question: " + e.getMessage());
		}
	}
	
	public static Map<String,Object> getQuestionsById(DispatchContext dctx,Map<String,Object> context){
		Delegator delegator=dctx.getDelegator();
		
		try {
			
			String questionId=(String)context.get("questionId");
			
			
			
			GenericValue question=EntityQuery.use(delegator).from("questionMaster").where("questionId",questionId).queryOne();
			
			if(question == null) {
				return ServiceUtil.returnError("question not Found");
			}
			
			
			Map<String,Object> oneQuestion=new HashMap<>();
			
			oneQuestion.put("question",question);
			
			Map<String,Object> result=ServiceUtil.returnSuccess();
			result.put("question", oneQuestion);
			
			
			
			return result;
		}catch(GenericEntityException e) {
			return ServiceUtil.returnError("Error fetching questions By topic: "+e.getMessage());
		}
	}

	
	public static Map<String,Object> getQuesByTopic(DispatchContext dctx,Map<String,Object> context){
		Delegator delegator=dctx.getDelegator();
		
		try {
			
			String topicId=(String)context.get("topicId");
			
			
			GenericValue topic=EntityQuery.use(delegator).from("TopicMaster").where("topicId",topicId).queryOne();
			
			if(topic == null) {
				return ServiceUtil.returnError("Topic not Found");
			}
			
			
//			long totalCount=EntityQuery.use(delegator).from("questionMaster").where("topicId",topicId).queryCount();
						
			List<GenericValue> questions=EntityQuery.use(delegator)
							.from("questionMaster")
							.where("topicId",topicId)
							.orderBy("questionId")
							.queryList();
			
			
			
			List<Map<String,Object>> questionList=new ArrayList<>();
			
			for(GenericValue ques:questions) {
				Map<String,Object> qMap=new HashMap<>();
				
				qMap.put("questionId", ques.getString("questionId"));
				qMap.put("questionDetail", ques.getString("questionDetail"));
				qMap.put("optionA", ques.getString("optionA"));
				qMap.put("optionB", ques.getString("optionB"));
				qMap.put("optionC", ques.getString("optionC"));
				qMap.put("optionD", ques.getString("optionD"));
				qMap.put("numAnswers", ques.getLong("numAnswers"));
				qMap.put("questionTypeId", ques.getString("questionTypeId"));
				qMap.put("difficultyLevel", ques.getString("difficultyLevel"));
				qMap.put("topicId", ques.getString("topicId"));
				qMap.put("negativeMarkValue", ques.getBigDecimal("negativeMarkValue"));
				
				
				questionList.add(qMap);
			}
			
			Map<String,Object> result=ServiceUtil.returnSuccess();
			
			result.put("topicId", topic.getString("topicId"));
			result.put("topicName", topic.getString("topicName"));
			result.put("questionList", questionList);
			
			
			return result;
		}catch(GenericEntityException e) {
			return ServiceUtil.returnError("Error fetching questions By topic: "+e.getMessage());
		}
	}
}

