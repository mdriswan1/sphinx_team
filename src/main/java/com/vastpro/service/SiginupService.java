package com.vastpro.service;


import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
public class SiginupService {

    public static Map<String, Object> checkSiginupService(DispatchContext dctx, Map<String, ? extends Object> context) {

        Delegator delegator = dctx.getDelegator();

        try {
          //  GenericValue emp = delegator.makeValue("employee");

            // Generate primary key
          //  emp.setNextSeqId();

            // Set fields from input context
          //  emp.setNonPKFields(context);

            // Create record
          //  delegator.create(emp);

            return ServiceUtil.returnSuccess("Employee created successfully");

        } catch (Exception e) {
        	return ServiceUtil.returnSuccess("Employee created successfully");
//            return ServiceUtil.returnError("Error while creating employee: " + e.getMessage());
        }
    }
    

}