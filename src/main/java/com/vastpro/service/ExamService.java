package com.vastpro.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.party.party.PartyServices;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;



public class ExamService {
	public static Map<String, Object> getAllExam(DispatchContext context, Map<String,Object> input) {
		Delegator delegator=context.getDelegator();
		try {
			List<GenericValue> value=delegator.findAll("ExamMaster", false);
			Map<String,Object> result=ServiceUtil.returnSuccess();
			result.put("data", value);
			return result;
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("it is error");
		}
		
	}
	
	
	
	public Map<String,Object> examCreate(DispatchContext context,Map<String,Object> input){
		Delegator delegator=context.getDelegator();
		LocalDispatcher dispatcher=context.getDispatcher();

		try {
		if(input.get("examName")==null) {
			return ServiceUtil.returnError("examname is empty");
		}else {
			GenericValue examAlreadyExits = EntityQuery.use(delegator).from("ExamMaster")
					.where("examName", input.get("examName")).queryFirst();
			if(examAlreadyExits!=null) {
				return ServiceUtil.returnError("the exam already exist");
			}
		}
		Map<String,Object> newVal=new HashMap<>();
		String examId = delegator.getNextSeqId("ExamMaster");
		examId = "SPX_EM_" + examId;
		newVal.put("examId", examId);
		
		newVal.put("examName",input.get("examName"));
		newVal.put("description",input.get("description"));
		newVal.put("noOfQuestions",Long.valueOf((String) input.get("noOfQuestions")));
		newVal.put("duration", Long.valueOf((String) input.get("duration")));
		newVal.put("passPercentage", Long.valueOf((String) input.get("passPercentage")));
		
		
		Map<String,Object> result=dispatcher.runSync("examcreates", newVal);
		System.out.println(result);
		
		result.put("status", "created successfully");
		result.put("examId",examId);
		
		return result;
		} catch (GenericServiceException | GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("exception throught");
			
		} 
	}
	public Map<String,Object> examUpdate(DispatchContext context,Map<String,Object> input){
		LocalDispatcher dispatcher=context.getDispatcher();
		try {
			if(input.get("examId")==null) {
				return ServiceUtil.returnError("examid is null");
			}else {
				Map<String,Object> newVal=new HashMap<>();
				newVal.put("examId", input.get("examId"));
				
				newVal.put("examName",input.get("examName"));
				newVal.put("description",input.get("description"));
				newVal.put("noOfQuestions",Long.valueOf((String) input.get("noOfQuestions")));
				newVal.put("duration", Long.valueOf((String) input.get("duration")));
				newVal.put("passPercentage", Long.valueOf((String) input.get("passPercentage")));
				
				if(dispatcher==null) {
					return ServiceUtil.returnError("dispatcher is null");
				}else {
					dispatcher.runSync("examUpdates",newVal);
					return ServiceUtil.returnSuccess("it is update success");
					
				}
				
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("it is generic servic exception "+e.getMessage());
		}
	}
	public Map<String,Object> examDelete(DispatchContext context,Map<String,Object> input){
		try {
			LocalDispatcher dispatcher=context.getDispatcher();
			String examId=(String) input.get("examId");
			System.out.println("EXAMID: "+examId);
			if(examId==null) {
				return ServiceUtil.returnError("exam id is null");
				
			}else {
				
				
				dispatcher.runSync("examDeletes",input);
			}
		}catch(GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error from service"+e.getMessage());
		}
		return ServiceUtil.returnSuccess("exam deleted");
	}
	
	public Map<String, Object> insertExamTopicDetails(DispatchContext context,Map<String, Object> input) throws GenericEntityException{
		LocalDispatcher dispatcher= context.getDispatcher();
		
		try {
			Map<String, Object> result=dispatcher.runSync("autoInsertExamT", input);
			
			if(ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("ExamTopic not inserted");
			}else {
				System.out.println("inside done inservice method");
				return ServiceUtil.returnSuccess("ExamTopic inserted Successfully");
			}
		}catch (GenericServiceException e) {
			// TODO: handle exception
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
	}
	
	public Map<String, Object> getAllExamTopics(DispatchContext context,Map<String, Object> input){
		Delegator delegator=context.getDelegator();
		Map<String,Object> result=ServiceUtil.returnSuccess("Topic getted successfully");
		try {
			
			List<GenericValue> topics=EntityQuery.use(delegator).from("ExamTopicDetail").queryList();
			if(topics.size()==0) {
				return ServiceUtil.returnSuccess("no topic found");
				
				
			}
			result.put("topic", topics);
			return result;
		
			
		}catch (GenericEntityException e) {
			// TODO: handle exception
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
	}
	
	public Map<String, Object> getExamTopicById(DispatchContext context,Map<String, Object> input){
		Delegator delegator=context.getDelegator();
		String examId=(String) input.get("examId");
		System.out.println("available id "+examId);
		Map<String, Object> result=ServiceUtil.returnSuccess("Topic getted successfully");
		try {
			
			List<GenericValue> topics=EntityQuery.use(delegator).from("ExamTopicDetail").where("examId", examId).queryList();
			if(topics.size()==0) {
				return ServiceUtil.returnSuccess("no topic found");
				
				
			}
			result.put("topic", topics);
			return result;
		}catch (GenericEntityException e) {
			// TODO: handle exception
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
	}
	
	public Map<String,Object> deleteExamByDetails(DispatchContext context,Map<String, Object> input){
		
		LocalDispatcher dispatcher=context.getDispatcher();
		Map<String, Object> result=ServiceUtil.returnSuccess("Topic deleted successfully");
		try {
			String topicId=(String) input.get("topicId");
    		String examId=(String) input.get("examId");
			Map<String, Object> contexts = new HashMap<>();
			contexts.put("examId", examId);
			System.out.println("exam id is service :"+examId);
			contexts.put("topicId", topicId);
			
			System.out.println("topic id is service :"+topicId);
    	
    		
			//"SPX_TM_10201"
			result=dispatcher.runSync("autoDeleteTopicbyDetails", contexts);
			if(ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("Topic Not Found");
			}else {
				return ServiceUtil.returnSuccess("Topic Deleted Successfully");
			}
		}catch (GenericServiceException e ) {
			// TODO: handle exception
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
	}
}
