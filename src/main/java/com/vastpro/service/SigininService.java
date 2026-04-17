package com.vastpro.service;

import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import com.vastpro.utility.HashPassword;

public class SigininService {

	public static Map<String, Object> checkSigininService(DispatchContext context, Map<String, Object> input) {

		Delegator delegator = context.getDelegator();
		String userName = (String) input.get("userLoginId");
		String password = (String) input.get("currentPassword");
		if (userName == null || password == null) {
			return ServiceUtil.returnError("username or password is null");
		} else {
			try {
				GenericValue value = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userName).queryOne();

				if (HashPassword.checkPassword(password, value.getString("currentPassword"))) {
					List<GenericValue> roles = EntityQuery.use(delegator).from("PartyRole").where("partyId", value.getString("partyId"))
									.queryList();
					String role = "";

					for (GenericValue value1 : roles) {
						String roleTypeId = value1.getString("roleTypeId");

						if (roleTypeId.equals("SPX_ADMIN")) {
							role = "admin";
							break;
						} else if (roleTypeId.equals("SPX_USER")) {
							role = "user";
							break;
						}
					}
					if (role.length() > 1) {
						Map<String, Object> result = ServiceUtil.returnSuccess("login success");
						result.put("role", role);
						return result;
					}

				}
				return ServiceUtil.returnError("login unsuccess");
			} catch (GenericEntityException e) {

				return ServiceUtil.returnError("it is through exception");
			}

		}

	}

}