package com.vastpro.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import com.vastpro.checkpassword.GeneratePssword;
import com.vastpro.checkpassword.HashPassword;

public class SiginupService {

    public static Timestamp getDateTime() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    public static Map<String, Object> checkSiginupService(DispatchContext context, Map<String, ? extends Object> input) {

        Delegator delegator = context.getDelegator();
        LocalDispatcher dispatcher =context.getDispatcher();
        try {
        	String role=(String)input.get("role");
        	Map<String, Object> partyTable= new HashMap<>();
            String partyId = "SPX_UL_" + delegator.getNextSeqId("Party");
            partyTable.put("partyId", partyId);
            partyTable.put("partyTypeId", "PERSON");
            partyTable.put("statusId", "PARTY_ENABLED");
            Map<String, Object> result1 = dispatcher.runSync("createParty", partyTable);
            
            Map<String,Object> personTable=new HashMap<>();
            personTable.put("partyId", partyId);
            personTable.put("firstName", input.get("firstName"));
            personTable.put("lastName", input.get("lastName"));
            Map<String,Object> result2=dispatcher.runSync("createPerson", personTable);

            Map<String,Object> userLogin=new HashMap<>();
            userLogin.put("userLoginId", input.get("userName"));
            if(role.equals("SPX_ADMIN")) {
            	
            	String password=HashPassword.hashPassword((String)input.get("password"));
                userLogin.put("currentPassword",password);
            }else if(role.equals("SPX_USER")) {
            	String password=new GeneratePssword().generatePassword(7);
            	System.out.println("++++++++++++"+password+"+++++++++++++++");
            	password=HashPassword.hashPassword(password);
            	System.out.println("+++++++++++++++++++++++++++++++++++++ "+password+" -------------------------------------------------------------------------------");
            	userLogin.put("currentPassword",password);
            }
            
            userLogin.put("partyId", partyId);
            userLogin.put("enabled", "N");
            Map<String,Object> result3=dispatcher.runSync("createUserLogin", userLogin);
           

            Map<String,Object> partyRole=new HashMap<>();
          
            partyRole.put("partyId", partyId);
            partyRole.put("roleTypeId",role);
            Map<String,Object> result4=dispatcher.runSync("createPartyRole", partyRole);
            String contactMechId = "SPX_CONTACT_" + delegator.getNextSeqId("ContactMech");

            Map<String,Object> contactMech=new HashMap<>();
            contactMech.put("contactMechId", contactMechId);
            contactMech.put("contactMechTypeId", "EMAIL_ADDRESS");
            contactMech.put("infoString", input.get("email"));
            Map<String,Object> result5=dispatcher.runSync("createContactMech", contactMech);

            Map<String,Object> partyContactMech=new HashMap<>();
            partyContactMech.put("contactMechId", contactMechId);
            partyContactMech.put("partyId", partyId);
            partyContactMech.put("fromDate", getDateTime());
            Map<String,Object> result6=dispatcher.runSync("createPartyContactMech", partyContactMech);

            String teleContactMechIds = "SPX_CONTACT_" + delegator.getNextSeqId("ContactMech");
            Map<String,Object> contactMechTele=new HashMap<>();
            contactMechTele.put("contactMechId", teleContactMechIds);
            contactMechTele.put("contactMechTypeId", "TELECOM_NUMBER");
            Map<String,Object> result7=dispatcher.runSync("createContactMechTele", contactMechTele);

           
            Map<String,Object> telecomNumber=new HashMap<>();
            telecomNumber.put("contactMechId", teleContactMechIds);
            telecomNumber.put("contactNumber", input.get("phNo"));
            Map<String,Object> result8=dispatcher.runSync("createTelecomNumber", telecomNumber);
            
            Map<String,Object> partyContactMechTele=new HashMap<>();
            partyContactMechTele.put("contactMechId", teleContactMechIds);
            partyContactMechTele.put("partyId", partyId);
            partyContactMechTele.put("fromDate", getDateTime());
            Map<String,Object> result9=dispatcher.runSync("createPartyContactMechTele", partyContactMechTele);
            
            return ServiceUtil.returnSuccess("Employee created successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError("Error while creating employee: " + e.getMessage());
        }
    }
}