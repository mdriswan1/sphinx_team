package com.vastpro.service;

import java.util.List;
import java.util.Map;


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
	public Map<String,Object> examCreate(DispatchContext context,Map<String,Object> input){
		Delegator delecator=context.getDelegator();
		try {
			
			
		GenericValue value=delecator.makeValue("ExamMaster");
		String examid ="SPX_"+delecator.getNextSeqId("examId");
		value.set("examName",input.get("examName"));
		value.set("description",input.get("description"));
		value.set("duration", input.get("duration"));
		value.set("noOfQuestions",input.get("questions"));
		value.set("passPercentage",input.get("percentage"));
		value.set("examId",examid);;
		
			delecator.create(value);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess("successful created exam");
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
}
