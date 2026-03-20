package com.vastpro.event;

import java.io.PrintWriter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.jose4j.json.internal.json_simple.JSONObject;

public class SignupEvent {

    public static String checkSignupEvent(HttpServletRequest request, HttpServletResponse response) {

        String name = request.getParameter("EmployeeName");
        String phone = request.getParameter("EmployeePhone");
        String dept = request.getParameter("DeptName");

        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        try {
            // Validation
            if (phone == null || phone.length() != 10) {
                return sendJson(response, false, "Phone Number must be 10 digits", null);
            }

            // Call service
            dispatcher.runSync("employeeService", UtilMisc.toMap(
                    "EmployeeName", name,
                    "EmployeePhone", phone,
                    "DeptName", dept
            ));

            String token = "JWT_TOKEN_SAMPLE";

            // admin data
            JSONObject admin = new JSONObject();
            admin.put("id", "ADM12345");
            admin.put("name", name);
            admin.put("role", "ADMIN");

            JSONObject data = new JSONObject();
            data.put("token", token);
            data.put("admin", admin);

            return sendJson(response, true, "Signup successful", data);

        } catch (GenericServiceException e) {
            return sendJson(response, false, "Error: " + e.getMessage(), null);
        }
    }

    // Common JSON response method
    private static String sendJson(HttpServletResponse response, boolean success, String message, JSONObject data) {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            JSONObject json = new JSONObject();
            json.put("success", success);
            json.put("message", message);
            json.put("data", data != null ? data : null);

            PrintWriter out = response.getWriter();
            out.print(json.toString());
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "none"; //  VERY IMPORTANT (prevents view rendering)
    }
}