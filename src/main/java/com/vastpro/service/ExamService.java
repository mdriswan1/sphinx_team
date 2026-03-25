package com.vastpro.service;

import java.util.Map;


import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class ExamService {
	public Map<String,Object> examCreate(DispatchContext context,Map<String,Object> input){
		Delegator delecator=context.getDelegator();
		try {
		GenericValue value=delecator.makeValue("ExamMaster");
		String examid ="SPX_"+delecator.getNextSeqId("examId");
		value.set("examName",input.get("examName"));
		value.set("description",input.get("description"));
		value.set("duration", input.get("duration"));
		value.set("noOfQuestions",input.get("questions"));
		value.set("passPercentage",input.get("percentage"));
		value.set("examId",examid);;
		
			delecator.create(value);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess("successful created exam");
	}

}
