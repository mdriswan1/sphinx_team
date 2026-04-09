package com.vastpro.service;


import java.util.List;
import java.util.Map;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
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
    			GenericValue value= EntityQuery.use(delegator).from("UserLogin").where("userLoginId",userName).queryOne();
    			
    			if(HashPassword.checkPassword(password,value.getString("currentPassword"))) {
    				//select * from ofbiz.party_role where party_id='SPX_10190';
    				//roleTypeId
    				//GenericValue role= (GenericValue) delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId",value.getString("partyId")), null, false);
    				System.out.println("++++++++++++++"+value.getString("partyId")+"+++++++++++");
    				//GenericValue role=EntityQuery.use(delegator).from("PartyRole").where("partyId",value.getString("partyId")).queryFirst();
    				List<GenericValue> roles = EntityQuery.use(delegator).from("PartyRole").where("partyId", value.getString("partyId")).queryList();
    				String role="";
    				
    				for(GenericValue value1:roles) {
    					 String roleTypeId = value1.getString("roleTypeId");
    					System.out.println("+++++++++++++++++"+value1.getString("roleTypeId")+"+++++++++++++++++++");
    					if(roleTypeId.equals("SPX_ADMIN")) {
    						role="admin";
    						break;
    					}else if(roleTypeId.equals("SPX_USER")) {
    						role="user";
    						break;
    					}
    				}
    				if(role.length()>1) {
    					Map<String,Object> result= ServiceUtil.returnSuccess("login success");
    					result.put("role",role);
    					return result;
    				}
    				
    				
    			}
				return ServiceUtil.returnError("login unsuccess");
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				
				return ServiceUtil.returnError("it is through exception");
			} 
    		
    	}
    			
    	
 
            
    }
    

}