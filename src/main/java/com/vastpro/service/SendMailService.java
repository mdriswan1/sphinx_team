package com.vastpro.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.crypto.HashCrypt;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import com.vastpro.utility.GeneratePssword;

public class SendMailService {
	public static Map<String, Object> sendExamAssignmentEmail(String examId, String partyId, HttpServletRequest request,
					HttpServletResponse response) {

		try {
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			Delegator delegator = (Delegator) request.getAttribute("delegator");

			GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "admin").queryOne();

			// get user email
			GenericValue assignedUser = EntityQuery.use(delegator).from("UserLogin").where("partyId", partyId).queryFirst();

			// get exam name
			GenericValue exam = EntityQuery.use(delegator).from("ExamMaster").where("examId", examId).queryOne();

			if (assignedUser == null) {
				return ServiceUtil.returnError("User not found");
			}
			if (exam == null) {
				return ServiceUtil.returnError("Exam not found");
			}

			String email = assignedUser.getString("userLoginId");
			String examName = exam.getString("examName");

			// generate password

			String rawPassword = new GeneratePssword().generatePassword();
			String hashedPassword = HashCrypt.cryptUTF8("SHA", null, rawPassword);

			// save password to db
			Map<String, Object> updateData = new HashMap<>();
			updateData.put("examId", examId);
			updateData.put("partyId", partyId);
			updateData.put("passwordChangesAuto", hashedPassword);
			updateData.put("userLogin", userLogin);

			Map<String, Object> updateResult = dispatcher.runSync("updatePartyExamPassword", updateData);

			if (ServiceUtil.isError(updateResult)) {
				return ServiceUtil.returnError("Failed to save password: " + ServiceUtil.getErrorMessage(updateResult));
			}

			// send rawpassword
			Map<String, Object> emailCtx = new HashMap<>();
			emailCtx.put("sendTo", email);
			emailCtx.put("subject", "You have been assigned to Exam: " + examName);
			emailCtx.put("body", "Hello,\n\n" + "You have been assigned to: " + examName + "\n\n" + "Username: " + email + "\n"
							+ "Exam Password: " + rawPassword + "\n\n" + "Use these to start your exam.\n\n" + "Regards,\nAdmin");
			emailCtx.put("contentType", "text/plain");

			Map<String, Object> mailResult = dispatcher.runSync("sendMail", emailCtx);
			if (ServiceUtil.isError(mailResult)) {
				return ServiceUtil.returnError("Email failed: " + ServiceUtil.getErrorMessage(mailResult));
			}

			return ServiceUtil.returnSuccess("Email sent successfully");

		} catch (Exception e) {
			return ServiceUtil.returnError("Error: " + e.getMessage());
		}
	}

}
