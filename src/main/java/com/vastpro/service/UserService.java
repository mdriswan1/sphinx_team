package com.vastpro.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class UserService {
	
	public Map<String,Object> getAllUser(DispatchContext context,Map<String,Object> input){
		Delegator delegator=context.getDelegator();
		try {
			List<GenericValue> allUser=delegator.findAll("UserLogin", false);
			Map<String,Object> result=ServiceUtil.returnSuccess();
			result.put("allUser", allUser);
			return result;
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("it is exception"+e.getMessage());
		}
		
		
	}
	public Map<String,Object> createExamRelationship(DispatchContext context,Map<String,Object> input){
		LocalDispatcher dispatcher=context.getDispatcher();
		if(dispatcher==null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		}else {
			List<Map<String, Object>> allData=(List<Map<String, Object>>)input.get("allData");
			for(Map<String,Object> obj:allData) {
				try {
					Map<String,Object> result=dispatcher.runSync("examrelationshipcreates",obj);
					
					
					if(ServiceUtil.isError(result)) {
						return ServiceUtil.returnError((String)result.get("errorMessage"));
					}
					
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return ServiceUtil.returnError("it is exception"+obj+e.getMessage());
				}
			}
			return ServiceUtil.returnSuccess("succesfuly created");
		}
		
	}
	

}
