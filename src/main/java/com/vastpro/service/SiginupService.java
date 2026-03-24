package com.vastpro.service;

import java.util.Map;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class SiginupService {

    public static Timestamp getDateTime() {
        LocalDateTime now = LocalDateTime.now();
        return Timestamp.valueOf(LocalDateTime.now());
    }

    public static Map<String, Object> checkSiginupService(DispatchContext dctx, Map<String, ? extends Object> context) {

        Delegator delegator = dctx.getDelegator();

        try {
            String partyId = "SPX_" + delegator.getNextSeqId("Party");

            GenericValue party = delegator.makeValue("Party");
            party.set("partyId", partyId);
            party.set("partyTypeId", "PERSON");
            party.set("statusId", "PARTY_ENABLED");

            delegator.create(party);

            GenericValue person = delegator.makeValue("Person");
            person.set("partyId", partyId);
            person.set("firstName", context.get("firstName"));
            person.set("lastName", context.get("lastName"));

            delegator.create(person);

            GenericValue userLoginId = delegator.makeValue("UserLogin");

            userLoginId.set("userLoginId", context.get("username"));
            userLoginId.set("currentPassword", context.get("password"));
            userLoginId.set("partyId", partyId);
            userLoginId.set("enabled", "N");

            delegator.create(userLoginId);

            GenericValue partyRole = delegator.makeValue("PartyRole");
            partyRole.set("partyId", partyId);
            partyRole.set("roleTypeId", "SPX_EXAMINEE");

            delegator.create(partyRole);

            String contactMechId = "SPX_CONTACT_" + delegator.getNextSeqId("ContactMech");

            GenericValue contactMech = delegator.makeValue("ContactMech");
            contactMech.set("contactMechId", contactMechId);
            contactMech.set("contactMechTypeId", "EMAIL_ADDRESS");
            contactMech.set("infoString", context.get("email"));

            delegator.create(contactMech);

            GenericValue partyContactMech = delegator.makeValue("PartyContactMech");
            partyContactMech.set("contactMechId", contactMechId);
            partyContactMech.set("partyId", partyId);
            partyContactMech.set("fromDate", getDateTime());

            delegator.create(partyContactMech);

            String teleContactMechIds = "SPX_CONTACT_" + delegator.getNextSeqId("ContactMech");

            GenericValue contactMechTele = delegator.makeValue("ContactMech");
            contactMechTele.set("contactMechId", teleContactMechIds);
            contactMechTele.set("contactMechTypeId", "TELECOM_NUMBER");

            delegator.create(contactMechTele);

            String teleContactMechId = "SPX_CONTACT_" + delegator.getNextSeqId("ContactMech");

            GenericValue telecomNumber = delegator.makeValue("TelecomNumber");
            telecomNumber.set("contactMechId", teleContactMechIds);
            telecomNumber.set("contactNumber", context.get("phNo"));

            delegator.create(telecomNumber);

            GenericValue partyContactMechTele = delegator.makeValue("PartyContactMech");
            partyContactMechTele.set("contactMechId", teleContactMechIds);
            partyContactMechTele.set("partyId", partyId);
            partyContactMechTele.set("fromDate", getDateTime());

            delegator.create(partyContactMechTele);

            return ServiceUtil.returnSuccess("Employee created successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError("Error while creating employee: " + e.getMessage());
        }
    }
}