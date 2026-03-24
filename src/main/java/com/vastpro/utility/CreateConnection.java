package com.vastpro.utility;

import javax.servlet.ServletContext;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;

public class CreateConnection {
	private static Delegator getDelegator(ServletContext servletContext) {
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        if (delegator == null) {
            delegator = DelegatorFactory.getDelegator("default");
        }
        return delegator;
    }

    private static LocalDispatcher getDispatcher(ServletContext servletContext) {
        LocalDispatcher dispatcher =
            (LocalDispatcher) servletContext.getAttribute("dispatcher");

        if (dispatcher == null) {
            dispatcher = ServiceContainer.getLocalDispatcher(
                "sphinx",   // must match web.xml
                getDelegator(servletContext)
            );
        }
        return dispatcher;
    }

}
