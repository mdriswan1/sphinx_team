package com.vastpro.service;

import java.util.ArrayList;
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

public class UserService {

	public Map<String, Object> getAllUser(DispatchContext context, Map<String, Object> input) {
		Delegator delegator = context.getDelegator();
		List<GenericValue> filteredUsers = new ArrayList<>();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			List<GenericValue> allUser = delegator.findAll("UserLogin", false);

			for (GenericValue user : allUser) {
				String partyId = user.getString("partyId");
				List<GenericValue> partyRoles = EntityQuery.use(delegator).from("PartyRole").where("partyId", partyId).queryList();
				boolean isUser = false;
				for (GenericValue partyRole : partyRoles) {
					String roleTypeId = partyRole.getString("roleTypeId");

					if (roleTypeId.equals("SPX_USER")) {
						isUser = true;
						break;
					}
				}
				if (isUser) {
					String examId = (String) input.get("examId");
					GenericValue record = EntityQuery.use(delegator).from("PartyExamRelationship")
									.where("partyId", partyId, "examId", examId).queryOne();
					GenericValue record2 = EntityQuery.use(delegator).from("AssignExamTempoary").where("partyId", partyId, "examId", examId)
									.queryOne();
					if (record == null && record2 == null) {
						filteredUsers.add(user);
					}

				}
			}

			result.put("allUser", filteredUsers);
			return result;
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("it is exception" + e.getMessage());
		}

	}

	public Map<String, Object> createExamRelationship(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		} else {
			List<Map<String, Object>> allData = (List<Map<String, Object>>) input.get("allData");

			for (Map<String, Object> obj : allData) {
				try {
					Map<String, Object> result = dispatcher.runSync("examrelationshipcreates", obj);

					Map<String, Object> result1 = dispatcher.runSync("deleteAssignTempoary", obj);

					if (ServiceUtil.isError(result)) {
						return ServiceUtil.returnError((String) result.get("errorMessage"));
					}

				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return ServiceUtil.returnError("it is exception" + obj + e.getMessage());
				}
			}
			return ServiceUtil.returnSuccess("succesfuly created");
		}

	}

	public Map<String, Object> assignTempoary(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		} else {
			try {

				List<String> list = (List<String>) input.get("partyId");
				String partyId = list.get(0);
				input.put("partyId", partyId);

				Map<String, Object> result = dispatcher.runSync("autoassignTempoary", input);
				if (ServiceUtil.isError(result)) {
					return ServiceUtil.returnError((String) result.get("errorMessage"));
				}
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ServiceUtil.returnError(e.getMessage());
			}

		}
		return ServiceUtil.returnSuccess("succesfuly created");
	}

	public Map<String, Object> getAssignedUser(DispatchContext context, Map<String, Object> input) {
		LocalDispatcher dispatcher = context.getDispatcher();
		if (dispatcher == null) {
			return ServiceUtil.returnError("in service dispatcher is null");
		}
		Delegator delegator = dispatcher.getDelegator();

		try {
			List<GenericValue> value = EntityQuery.use(delegator).from("AssignExamTempoary").queryList();
			if (value.isEmpty()) {
				return ServiceUtil.returnSuccess("no data found");
			}
			Map<String, Object> result = ServiceUtil.returnSuccess("succefully geted");
			result.put("allUser", value);
			return result;

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("exception" + e.getMessage());
		}
	}

}
