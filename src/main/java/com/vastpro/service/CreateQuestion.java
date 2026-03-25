package com.vastpro.service;

import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class CreateQuestion {

	public static Map<String, Object> checkSiginupService(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		/*
		 * 
    <attribute name="topicId" type="String" mode="IN" optional="false"/>
    <attribute name="questionDetail" type="String" mode="IN" optional="false"/>
    <attribute name="optiona" type="String" mode="IN" optional="false"/>
    <attribute name="optionb" type="String" mode="IN" optional="false"/>
    <attribute name="optionc" type="String" mode="IN" optional="false"/>
    <attribute name="optiond" type="String" mode="IN" optional="false"/>
    <attribute name="optione" type="String" mode="IN" optional="false"/>
    <attribute name="answer" type="String" mode="IN" optional="false"/>

		 * */
		
		Delegator delegator = dctx.getDelegator();
		
	 GenericValue questionBankMaster = delegator.makeValue("QuestionBankMaster");
	 questionBankMaster.set("topicId", context.get("topicId"));
	 questionBankMaster.set("questionDetail", context.get("questionDetail"));
	 questionBankMaster.set("optiona", context.get("optiona"));
	 questionBankMaster.set("optionb", context.get("optionb"));
	 questionBankMaster.set("optionc", context.get("optionc"));
	 questionBankMaster.set("optiond", context.get("optiond"));
	 questionBankMaster.set("optione", context.get("optione"));
	 questionBankMaster.set("answer", context.get("answer"));
     try {
		delegator.create(questionBankMaster);
		return ServiceUtil.returnSuccess("Question created successfully");
	} catch (GenericEntityException e) {
		return ServiceUtil.returnSuccess("Qustion cannot created");
		
	}
}
}