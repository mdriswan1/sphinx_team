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

	
	public static Map<String,Object> getQuestionsByTopic(DispatchContext dctx,Map<String,Object> context){
		Delegator delegator=dctx.getDelegator();
		
		try {
			
			String topicId=(String)context.get("topicId");
			Integer pageNo=(Integer)context.get("pageNo");
			Integer pageSize=(Integer)context.get("pageSize");
			
			
			
			if(topicId==null || topicId.trim().isEmpty()) {
				return ServiceUtil.returnError("topicId is Required");
			}
			
			if(pageNo==null || pageNo<1) {
				pageNo=1;
			}
			if(pageSize==null || pageSize<1) {
				pageSize=10;
			}
			
			GenericValue topic=EntityQuery.use(delegator).from("TopicMaster").where("topicId",topicId).queryOne();
			
			if(topic == null) {
				return ServiceUtil.returnError("Topic not Found");
			}
			
			
			long totalCount=EntityQuery.use(delegator).from("questionMaster").where("topicId",topicId).queryCount();
			
			int totalPages=(int) Math.ceil((double)totalCount/pageSize);
			int offset=(pageNo-1)*pageSize;
			
			
			List<GenericValue> questions=EntityQuery.use(delegator)
							.from("questionMaster")
							.where("topicId",topicId)
							.orderBy("questionId")
							.cursorScrollInsensitive()
							.maxRows(pageSize)
							.queryList();
			
			if(offset>questions.size()) {
				questions=new ArrayList<>();
			}else {
				questions=questions.subList(offset,
								Math.min(offset+pageSize,questions.size()));
			}
			
			List<Map<String,Object>> questionList=new ArrayList<>();
			
			for(GenericValue q:questions) {
				Map<String,Object> qMap=new HashMap<>();
				
				qMap.put("questionId", q.getLong("questionId"));
				qMap.put("questionDetail", q.getString("questionDetail"));
				qMap.put("optionA", q.getString("optionA"));
				qMap.put("optionB", q.getString("optionB"));
				qMap.put("optionC", q.getString("optionC"));
				qMap.put("optionD", q.getString("optionD"));
				qMap.put("numAnswers", q.getLong("numAnswers"));
				qMap.put("questionTypeId", q.getString("questionTypeId"));
				qMap.put("difficultyLevel", q.getString("difficultyLevel"));
				qMap.put("topicId", q.getString("topicId"));
				qMap.put("negativeMarkValue", q.getBigDecimal("negativeMarkValue"));
				
				
				questionList.add(qMap);
			}
			
			Map<String,Object> result=ServiceUtil.returnSuccess();
			
			result.put("topicId", topic.getString("topicId"));
			result.put("topicName", topic.getString("topicName"));
			result.put("totalCount", totalCount);
			result.put("questionList", questionList);
			result.put("pageNo",pageNo);
			result.put("pageSize",pageSize);
			result.put("totalPages", totalPages);
			result.put("hasNext", pageNo<totalPages);
			result.put("hasPrevious",pageNo>1);
			
			
			return result;
		}catch(GenericEntityException e) {
			return ServiceUtil.returnError("Error fetching questions By topic: "+e.getMessage());
		}
	}
}


//===========

/*
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


 * CreateQuestion
 * 
 * This class responsible for doing CRUD operation with questions

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
*/