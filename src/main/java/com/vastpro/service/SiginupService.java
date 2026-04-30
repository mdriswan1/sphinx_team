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

import com.vastpro.utility.GeneratePssword;

public class SiginupService {

	public static Timestamp getDateTime() {
		return Timestamp.valueOf(LocalDateTime.now());
	}

	public static Map<String, Object> checkSiginupService(DispatchContext context, Map<String, ? extends Object> input) {

		Delegator delegator = context.getDelegator();
		LocalDispatcher dispatcher = context.getDispatcher();
		try {

			String role = (String) input.get("role");
			Map<String, Object> partyTable = new HashMap<>();
			String partyId = "SPX_UL_" + delegator.getNextSeqId("Party");
			partyTable.put("partyId", partyId);
			partyTable.put("partyTypeId", "PERSON");
			partyTable.put("statusId", "PARTY_ENABLED");
			Map<String, Object> result1 = dispatcher.runSync("createParty", partyTable);

			Map<String, Object> personTable = new HashMap<>();
			personTable.put("partyId", partyId);
			personTable.put("firstName", input.get("firstName"));
			personTable.put("lastName", input.get("lastName"));
			Map<String, Object> result2 = dispatcher.runSync("createPerson", personTable);

			Map<String, Object> userLogin = new HashMap<>();
			userLogin.put("userLoginId", input.get("userName"));
			if (role.equals("SPX_ADMIN")) {
				String pass = (String) input.get("password");
				// String password = HashPassword.hashPassword((String) input.get("password"));
				userLogin.put("currentPassword", pass);
			} else if (role.equals("SPX_USER")) {
				String password = new GeneratePssword().generatePassword();
				System.out.println("++++++++" + password + "+++++++++");
				// password = HashPassword.hashPassword(password);
				userLogin.put("currentPassword", password);
				userLogin.put("currentPasswordVerify", password);
			}

			userLogin.put("partyId", partyId);
			userLogin.put("enabled", "Y");
			userLogin.put("requirePasswordChange", "N");
			userLogin.put("userLogin", input.get("userLogin"));
			Map<String, Object> result3 = dispatcher.runSync("createUserLogin", userLogin);

			GenericValue secGroup = delegator.makeValue("UserLoginSecurityGroup");
			secGroup.set("userLoginId", input.get("userName"));
			secGroup.set("groupId", "SPHINX_ADMIN_GROUP");
			secGroup.set("fromDate", Timestamp.valueOf(LocalDateTime.now()));
			delegator.create(secGroup);
			//
			if (!("SPX_USER".equals(role))) {
				GenericValue set = delegator.makeValue("UserLoginSecurityGroup");
				secGroup.set("userLoginId", input.get("userName"));
				secGroup.set("groupId", "PARTYADMIN");
				secGroup.set("fromDate", Timestamp.valueOf(LocalDateTime.now()));
				delegator.create(secGroup);
				// Map<String,Object> setPartyToCreateUserResult=dctx.getDispatcher().runSync("addUserLoginToSecurityGroup",
				// UtilMisc.toMap("userLoginId", username, "groupId", "PARTYADMIN", "fromDate",
				// Timestamp.valueOf(LocalDateTime.now()),
				// "userLogin",
				// params.get("userLogin")
				// ));
				// if(ServiceUtil.isError(setPartyToCreateUserResult)) {
				// Debug.logError("Error while create the user using the addUserLoginToSecurityGroup out box service when set the
				// PARTYADMIN"+(String)userLoginResult.get("errorMessage"),UserSignUpService.class.getName());
				// return handleTransaction();
				// }
			}

			Map<String, Object> partyRole = new HashMap<>();

			partyRole.put("partyId", partyId);
			partyRole.put("roleTypeId", role);
			Map<String, Object> result4 = dispatcher.runSync("createPartyRole", partyRole);
			String contactMechId = "SPX_CONTACT_" + delegator.getNextSeqId("ContactMech");

			Map<String, Object> contactMech = new HashMap<>();
			contactMech.put("contactMechId", contactMechId);
			contactMech.put("contactMechTypeId", "EMAIL_ADDRESS");
			contactMech.put("infoString", input.get("email"));
			Map<String, Object> result5 = dispatcher.runSync("createContactMech", contactMech);

			Map<String, Object> partyContactMech = new HashMap<>();
			partyContactMech.put("contactMechId", contactMechId);
			partyContactMech.put("partyId", partyId);
			partyContactMech.put("fromDate", getDateTime());
			Map<String, Object> result6 = dispatcher.runSync("createPartyContactMech", partyContactMech);

			String teleContactMechIds = "SPX_CONTACT_" + delegator.getNextSeqId("ContactMech");
			Map<String, Object> contactMechTele = new HashMap<>();
			contactMechTele.put("contactMechId", teleContactMechIds);
			contactMechTele.put("contactMechTypeId", "TELECOM_NUMBER");
			Map<String, Object> result7 = dispatcher.runSync("createContactMechTele", contactMechTele);

			Map<String, Object> telecomNumber = new HashMap<>();
			telecomNumber.put("contactMechId", teleContactMechIds);
			telecomNumber.put("contactNumber", input.get("phNo"));
			Map<String, Object> result8 = dispatcher.runSync("createTelecomNumber", telecomNumber);

			Map<String, Object> partyContactMechTele = new HashMap<>();
			partyContactMechTele.put("contactMechId", teleContactMechIds);
			partyContactMechTele.put("partyId", partyId);
			partyContactMechTele.put("fromDate", getDateTime());
			Map<String, Object> result9 = dispatcher.runSync("createPartyContactMechTele", partyContactMechTele);

			return ServiceUtil.returnSuccess("Employee created successfully");

		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Error while creating employee: " + e.getMessage());
		}
	}
}