package com.vastpro.service;

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

public class CreateTopic {
	
	public Map<String, Object> createTopic(DispatchContext context,Map<String, Object> input) {
		Delegator delegator=context.getDelegator();
		String topicId="SPX_TM_"+delegator.getNextSeqId("TopicMaster");
		GenericValue topicMaster=delegator.makeValue("TopicMaster");
		topicMaster.set("topicName", input.get("topicName"));
		topicMaster.set("topicId", topicId);
		try {
			delegator.create(topicMaster);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess("created topic");
		
	}
	
	public Map<String, Object> getAllTopics(DispatchContext context, Map<String, Object> input) {
		Delegator delegator=context.getDelegator();
		Map<String,Object> result=ServiceUtil.returnSuccess("Topic getted successfully");
		try {
//			List<GenericValue> topics=delegator.findAll("TopicMaster", false);
			List<GenericValue> topicList=EntityQuery.use(delegator).from("TopicMaster").queryList();
			
			if(topicList.size()==0) {
				return ServiceUtil.returnSuccess("no topic found");
			}
			result.put("topic", topicList);
			
			
			return result;
		}catch (  GenericEntityException e) {
			// TODO: handle exception
			e.printStackTrace();
	        return ServiceUtil.returnFailure("error while reterving");
		}
	}
	
	
	
	public Map<String, Object> deleteById(DispatchContext context,Map<String, Object> input){
		Delegator delegator=context.getDelegator();
		LocalDispatcher dispatcher=context.getDispatcher();
		String topicId=(String) input.get("topicId");
		  
		try {
			
			 GenericValue topicMaster = EntityQuery.use(delegator)
                     .from("TopicMaster")
                     .where("topicId", topicId)
                     .queryOne();
			if(topicMaster==null) {
				return ServiceUtil.returnError("Topic Not Found");
			}
			Map<String, Object> deleteMap=new HashMap<String, Object>();
			deleteMap.put("topicId", topicId);
			
				Map<String, Object> result=dispatcher.runSync("deleteTopic", deleteMap);
			
			
//			int removed=delegator.removeByAnd("TopicMaster",Map.of("topicId",topicId));
			
			
			if(ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("Topic Not Found");
			}else {
				return ServiceUtil.returnSuccess("Topic Deleted Successfully");
			}
		}catch (GenericEntityException | GenericServiceException e) {
			// TODO: handle exception
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
	}
	
	
	
	public Map<String , Object> updateTopic(DispatchContext context,Map<String, Object> input){
		Delegator delegator=context.getDelegator();
		LocalDispatcher dispatcher=context.getDispatcher();
		String topicId=(String) input.get("topicId");
		String topicName=(String) input.get("topicName");
		
		try {
			if(topicName==null || topicId==null) {
				return ServiceUtil.returnError("field cannot be empty");
			}
			GenericValue updateTopicId=EntityQuery.use(delegator).from("TopicMaster").where("topicId",topicId).queryOne();
			
			if(updateTopicId==null) {
				return ServiceUtil.returnError("Topic with Id"+ topicId+" not found");
			}
			Map<String, Object> updateMap=new HashMap<String, Object>();
			updateMap.put("topicId", topicId);
			updateMap.put("topicName", topicName);
			
			Map<String, Object> result=dispatcher.runSync("updateTopic", updateMap);
			if(ServiceUtil.isError(result)) {
				return ServiceUtil.returnError("error,occur during uodate topic");
			}
			return ServiceUtil.returnSuccess("topic update successfully");
			
		}catch (GenericEntityException | GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error,occur during update topic");
			// TODO: handle exception
		}
	}

}
