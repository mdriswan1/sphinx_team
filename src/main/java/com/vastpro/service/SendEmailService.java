package com.vastpro.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class SendEmailService {

	public static Map<String, Object> sendEmailToUser(DispatchContext dctx, Map<String, Object> context) {

		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();

		try {
			String examId = (String) context.get("examId");
			Long password = (Long) context.get("passwordChangesAuto");

			GenericValue exam = EntityQuery.use(delegator).from("ExamMaster").where("examId", examId).queryOne();

			String examName = exam.getString("examName");

			/*
			 * GenericValue user = EntityQuery.use(delegator).from("UserDetailsForEmail").where("partyId", partyId).queryFirst();
			 * 
			 * if (user == null) { return ServiceUtil.returnError("User email not found"); }
			 * 
			 * String email = user.getString("infoString"); String userLoginId = user.getString("userLoginId");
			 */

			String email = "rkamalraj12345@gmail.com";
			String userLoginId = "admin_kamalraj";
			GenericValue userLoginGV = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "admin_kamalraj") // hardcoded
																															// value
							.queryOne();
			String body = "Dear Candidate,\n\n" + "Exam: " + examName + "\n\n" + "Username: " + userLoginId + "\n" + "Password: " + password
							+ "\n\n" + "Regards,\nAdmin";

			Map<String, Object> emailCtx = new HashMap<>();
			emailCtx.put("sendTo", email);
			emailCtx.put("sendFrom", "rkamalraj583@gmail.com");
			emailCtx.put("subject", "Exam Assigned");
			emailCtx.put("body", body);
			emailCtx.put("contentType", "text/plain");
			emailCtx.put("userLogin", userLoginGV);
			emailCtx.put("partyId", "SPX_UL_10140");
			// Map<String, Object> res = dispatcher.runSync("sendMail", emailCtx);
			dispatcher.runSync("sendMail", emailCtx);
			return ServiceUtil.returnSuccess();

		} catch (Exception e) {
			Debug.log("===debug== error------------->" + e.getMessage());
			System.out.println("===syso== error------------->" + e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
	}
}
