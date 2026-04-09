package com.vastpro.restapi.resources;



import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;

@Path("/questionstwo")

public class TestQuestion2 {
	 @POST
	    @Path("/upload")
	    @Consumes(MediaType.MULTIPART_FORM_DATA)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response uploadQuestions(
	            @FormDataParam("file") InputStream fileInputStream,
	            @FormDataParam("file") FormDataContentDisposition fileMetaData,
	            @Context javax.servlet.http.HttpServletRequest request) {

	        if (fileInputStream == null) {
	            return Response.status(Response.Status.BAD_REQUEST)
	                    .entity(ServiceUtil.returnError("File not found"))
	                    .build();
	        }

	        String fileName = fileMetaData.getFileName();
	        if (!fileName.endsWith(".xlsx")) {
	            return Response.status(Response.Status.BAD_REQUEST)
	                    .entity(ServiceUtil.returnError("Excel file required"))
	                    .build();
	        }

	        try {
	            byte[] bytes = fileInputStream.readAllBytes();
	            ByteBuffer buffer = ByteBuffer.wrap(bytes);

	            LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	            Map<String, Object> result = dispatcher.runSync("uploadBulkQuestion", UtilMisc.toMap("file", buffer));

	            if ("error".equals(result.get("responseMessage"))) {
	                return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
	            } else {
	                result.put("successMessage", "Questions uploaded successfully!");
	            }

	            return Response.status(Response.Status.CREATED).entity(result).build();

	        } catch (Exception e) {
	            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                    .entity(ServiceUtil.returnError("Unexpected error occurred, try again after sometime!"))
	                    .build();
	        }
	    }
	
}




   