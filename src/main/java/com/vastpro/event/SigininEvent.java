package com.vastpro.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
//test
//test2
public class SigininEvent {

	public static final String MODULE = SigininEvent.class.getName();
		public static String checkSigininEvent(HttpServletRequest request, HttpServletResponse response) {

	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

	        
	        String password = request.getParameter("password");
	        String email = request.getParameter("email");

	        if (UtilValidate.isEmpty(password) || UtilValidate.isEmpty(email)) {
	            String errMsg = "First password and email are required fields on the form and can't be empty.";
	            request.setAttribute("_ERROR_MESSAGE_", errMsg);
	            return "error";
	        }
	      

	        try {
	            Debug.logInfo("=======Creating OfbizDemo record in event using service createOfbizDemoByGroovyService=========", MODULE);
	            dispatcher.runSync("checkuser", UtilMisc.toMap( "email", email, "password", password));
	        } catch (GenericServiceException e) {
	            String errMsg = "Unable to create new records in OfbizDemo entity: " + e.toString();
	            request.setAttribute("_ERROR_MESSAGE_", errMsg);
	            return "error";
	        }
	        request.setAttribute("_EVENT_MESSAGE_", "OFBiz Demo created succesfully.");
	        return "success";
	}
		            
}