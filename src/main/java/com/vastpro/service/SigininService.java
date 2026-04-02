package com.vastpro.service;


import java.util.Map;


import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import com.vastpro.checkpassword.HashPassword;
public class SigininService {

    public static Map<String, Object> checkSigininService(DispatchContext context, Map<String,Object> input) {
    	//LocalDispatcher dispatcher=context.getDispatcher();
    	Delegator delegator=context.getDelegator();
    	String userName= (String) input.get("userLoginId");
    	String password=(String) input.get("currentPassword");
    	if(userName==null || password==null) {
    		return ServiceUtil.returnError("username or password is null");
    	}else {
    		try {
//				dispatcher.runSync("signIns", input);
    			GenericValue value= delegator.findOne("UserLogin", Map.of("userLoginId",userName), false);
    			if(HashPassword.checkPassword(password,value.getString("currentPassword"))) {
    				return ServiceUtil.returnSuccess("login success");
    			}
				return ServiceUtil.returnError("login unsuccess");
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ServiceUtil.returnError("it is through exception");
			} 
    		
    	}
    			
    	
 
            
    }
    

}