package com.vastpro.restapi.resources;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
 
/*
import com.vastpro.sphinx.util.ConvertValue;
import com.vastpro.sphinx.util.QuestionColumnConfigUtil;
import com.vastpro.sphinx.util.QuestionColumnConfigUtil.ColumnConfig;
 */

 
@Path("/question")
public class QuestionResource {

	@Context
	private HttpServletRequest request;
 
	@Context
	private ServletContext servletContext;

	//convert value
	public static Integer toInteger(Object value) {
		if(value==null) {
			return null;
		}
		
		if(value instanceof Integer) {
			return (Integer) value;
		}
		
		if(value instanceof String) {
			try {
				return Integer.parseInt((String) value);
			}catch(NumberFormatException e) {
				return null;
			}
		}
		return null;
	}
	
	/*
	//QuesgionColumnConfigUtil
	public class QuestionColumnConfigUtil {
		
		private static final String CONFIG_NAME="questionColumnConfig";
		
		public static class ColumnConfig{
			public final int index;
			public final String field;
			public final String label;
			public final boolean required;
			public final String type;
			
			
			public ColumnConfig(int index, String field, String label, boolean required, String type) {
				this.index = index;
				this.field = field;
				this.label = label;
				this.required = required;
				this.type = type;
			}
			
		}
			
			private static List<ColumnConfig> cachedConfigs = null;
			
			public static List<ColumnConfig> getColumnConfigs() {
				if (cachedConfigs != null)
					return cachedConfigs;
	 
				
				List<ColumnConfig> configs = new ArrayList<>();
				
				int count = Integer.parseInt(UtilProperties.getPropertyValue(CONFIG_NAME, "column.count", "0"));
				
				for (int i = 0; i < count; i++) {
					String prefix = "column." + i + ".";
					
					String label = UtilProperties.getPropertyValue(CONFIG_NAME, prefix + "label", "");
					String field = UtilProperties.getPropertyValue(CONFIG_NAME, prefix + "field", "");
					boolean required = "true".equalsIgnoreCase(UtilProperties.getPropertyValue(CONFIG_NAME, prefix + "required", "false"));
					String type = UtilProperties.getPropertyValue(CONFIG_NAME, prefix + "type", "String");
					
					
					if (!field.isEmpty()) {
						configs.add(new ColumnConfig(i, field, label, required, type));
					}
					
				}
				cachedConfigs = configs;
				return cachedConfigs;
	 
			}
			
		
	}
	 */
	@POST
	@Path("/createquestion")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createQuestion(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		LocalDispatcher dispatcher=(LocalDispatcher)request.getAttribute("dispatcher");
		if(dispatcher==null) {
			dispatcher=ServiceContainer.getLocalDispatcher("sphinx", (Delegator)request.getAttribute("delegator"));
		}
		try {	
			Map<String,Object> input= new HashMap<String, Object>();
			input.put("questionDetail", request.getAttribute("questionDetail"));
			input.put("optionA", request.getAttribute("optionA"));
			input.put("optionB", request.getAttribute("optionB"));
			input.put("optionC", request.getAttribute("optionC"));
			input.put("optionD", request.getAttribute("optionD"));
			input.put("answer", request.getAttribute("answer"));
			input.put("numAnswers", request.getAttribute("numAnswers"));
			input.put("questionTypeId", request.getAttribute("questionTypeId"));
			input.put("difficultyLevel", request.getAttribute("difficultyLevel"));
			input.put("answerValue", request.getAttribute("answerValue"));
			input.put("topicId", request.getAttribute("topicId"));
			input.put("negativeMarkValue", request.getAttribute("negativeMarkValue"));

			Map<String, Object> result = dispatcher.runSync("createQuestionService", input);
			return Response.ok(result).build();
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).entity(Map.of("error", e.getMessage())).build();
		}
	}

	
	@POST
	@Path("/getquesbyid")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getQuestion(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		LocalDispatcher dispatcher=(LocalDispatcher)request.getAttribute("dispatcher");
		if(dispatcher==null) {
			dispatcher=ServiceContainer.getLocalDispatcher("sphinx", (Delegator)request.getAttribute("delegator"));
		}
		try {	
			Map<String,Object> input= new HashMap<String, Object>();
			input.put("questionId", request.getAttribute("questionId"));
		

			Map<String, Object> result = dispatcher.runSync("getQuesById", input);
			return Response.ok(result).build();
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).entity(Map.of("error", e.getMessage())).build();
		}
	}

	
	
	@PUT
	@Path("/updatequestion")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateQuestion(@Context HttpServletRequest request,@Context HttpServletResponse response) {
		//getting dispatcher from request
		LocalDispatcher dispatcher=(LocalDispatcher)request.getAttribute("dispatcher");
		if(dispatcher==null) {
			dispatcher=ServiceContainer.getLocalDispatcher("sphinx", (Delegator)request.getAttribute("delegator"));
		}
		Map<String, Object> result = new HashMap<>();
	    try {
	        // questionId must be sent by frontend  
	        String questionIdStr = (String) request.getAttribute("questionId");
	        if (questionIdStr == null) {
	        	result.put("status",  "ERROR");
	        	result.put("message", "questionId is required");
	        	return Response.status(400).entity(result).build();
	        }

	        
	        Map<String,Object>input=new HashMap<String, Object>();
	        input.put("questionId", questionIdStr);
	        input.put("questionDetail", request.getAttribute("questionDetail"));
			input.put("optionA", request.getAttribute("optionA"));
			input.put("optionB", request.getAttribute("optionB"));
			input.put("optionC", request.getAttribute("optionC"));
			input.put("optionD", request.getAttribute("optionD"));
			input.put("answer", request.getAttribute("answer"));
			input.put("numAnswers", request.getAttribute("numAnswers"));
			input.put("questionTypeId", request.getAttribute("questionTypeId"));
			input.put("difficultyLevel", request.getAttribute("difficultyLevel"));
			input.put("answerValue", request.getAttribute("answerValue"));
			input.put("topicId", request.getAttribute("topicId"));
			input.put("negativeMarkValue", request.getAttribute("negativeMarkValue"));
 
			
			System.out.println("negativeMarkValue"+request.getAttribute("negativeMarkValue"));
			System.out.println("questionTypeId"+request.getAttribute("questionTypeId"));
	        // Call service
	        Map<String, Object> serviceResult  = dispatcher.runSync("updateQuestionMaster", input);
 
	        if (ServiceUtil.isError(serviceResult)) {
	        	result.put("status",  "ERROR");
	        	result.put("message", ServiceUtil.getErrorMessage(serviceResult));
	            return Response.status(500).entity(result ).build();
	        }
 
	        result.put("status","SUCCESS");
	        result.put("message","Question updated successfully");
	        return Response.ok(result).build();
 
	    } catch (Exception e) {
	    	result.put("status",  "ERROR");
	    	result.put("message", e.getMessage());
	        return Response.status(500).entity(result).build();
	    }
	}
	

	@DELETE
	@Path("/deletequestion")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteQuestion(@Context HttpServletRequest request,@Context HttpServletResponse response) {
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	
	  		LocalDispatcher dispatcher=(LocalDispatcher)request.getAttribute("dispatcher");
	  		if(dispatcher==null) {
	  			dispatcher=ServiceContainer.getLocalDispatcher("sphinx", (Delegator)request.getAttribute("delegator"));
	  		}
	    try {
	        String questionId=String.valueOf(request.getAttribute("questionId"));
	           
	        Map<String,Object>input=new HashMap<String, Object>();
	        input.put("questionId", questionId);
 
	        Map<String, Object> serviceResult = dispatcher.runSync("deleteQuesMaster",input);
 
	        if (ServiceUtil.isError(serviceResult)) {
	            result.put("status",  "ERROR");
	            return Response.status(500).entity(result).build();
	        }
 
	        result.put("status","SUCCESS");
	        result.put("message", "question deleted Successfully");
	        return Response.ok(result).build();
 
	    } catch (Exception e) {
	        result.put("status",  "ERROR");
	        result.put("message", e.getMessage());
	        return Response.status(500).entity(result).build();
	    }
	}
/*	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadQuestions(
	        @Context HttpServletRequest request,
	        @FormDataParam("file") InputStream fileInputStream,
	        @FormDataParam("file") FormDataContentDisposition fileDetail) {
	    
	    Map<String, Object> response = new HashMap<>();
	    
	    // Validate file
	    if (fileInputStream == null || fileDetail == null) {
	        response.put("status", "ERROR");
	        response.put("message", "No file received");
	        return Response.status(400).entity(response).build();
	    }
	    
	    String fileName = fileDetail.getFileName();
	    if (!fileName.toLowerCase().endsWith(".xlsx") && !fileName.toLowerCase().endsWith(".xls")) {
	        response.put("status", "ERROR");
	        response.put("message", "Only Excel files (.xlsx, .xls) are allowed");
	        return Response.status(400).entity(response).build();
	    }
	    
	    // Get dispatcher
	    LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	    if (dispatcher == null) {
	        dispatcher = ServiceContainer.getLocalDispatcher("sphinx", 
	            (Delegator) request.getAttribute("delegator"));
	    }
	    
	    List<Map<String, Object>> errors = new ArrayList<>();
	    int successCount = 0;
	    int rowNumber = 1; // Start from 1 (header is row 0)
	    
	    try (Workbook workbook = WorkbookFactory.create(fileInputStream)) {
	        Sheet sheet = workbook.getSheetAt(0);
	        
	        // Skip header row (row 0), start from row 1
	        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	            Row row = sheet.getRow(i);
	            rowNumber = i + 1; // For user-friendly error messages (1-indexed)
	            
	            if (row == null || isRowEmpty(row)) {
	                continue;
	            }
	            
	            try {
	                Map<String, Object> questionData = parseRow(row, rowNumber);
	                
	                // Validate required fields
	                String validationError = validateQuestionData(questionData);
	                if (validationError != null) {
	                    errors.add(Map.of("row", rowNumber, "error", validationError));
	                    continue;
	                }
	                
	                // Call the service to create question
	                Map<String, Object> serviceResult = dispatcher.runSync("createQuestionService", questionData);
	                
	                if (ServiceUtil.isError(serviceResult)) {
	                    errors.add(Map.of("row", rowNumber, "error", ServiceUtil.getErrorMessage(serviceResult)));
	                } else {
	                    successCount++;
	                }
	                
	            } catch (Exception e) {
	                errors.add(Map.of("row", rowNumber, "error", e.getMessage()));
	            }
	        }
	        
	        response.put("status", errors.isEmpty() ? "SUCCESS" : "PARTIAL");
	        response.put("message", successCount + " questions uploaded successfully");
	        response.put("successCount", successCount);
	        response.put("errorCount", errors.size());
	        if (!errors.isEmpty()) {
	            response.put("errors", errors);
	        }
	        
	        return Response.ok(response).build();
	        
	    } catch (IOException e) {
	        response.put("status", "ERROR");
	        response.put("message", "Failed to read Excel file: " + e.getMessage());
	        return Response.status(500).entity(response).build();
	    } catch (Exception e) {
	        response.put("status", "ERROR");
	        response.put("message", "Upload failed: " + e.getMessage());
	        return Response.status(500).entity(response).build();
	    }
	}

	// Helper method to parse a row into question data
	private Map<String, Object> parseRow(Row row, int rowNumber) {
	    Map<String, Object> data = new HashMap<>();
	    
	    // Column mapping (0-indexed):
	    // 0: topicId, 1: questionDetail, 2: optionA, 3: optionB, 4: optionC, 
	    // 5: optionD, 6: optionE, 7: answer, 8: numAnswers, 9: questionTypeId,
	    // 10: difficultyLevel, 11: answerValue, 12: negativeMarkValue
	    
	    data.put("topicId", getCellStringValue(row.getCell(0)));
	    data.put("questionDetail", getCellStringValue(row.getCell(1)));
	    data.put("optionA", getCellStringValue(row.getCell(2)));
	    data.put("optionB", getCellStringValue(row.getCell(3)));
	    data.put("optionC", getCellStringValue(row.getCell(4)));
	    data.put("optionD", getCellStringValue(row.getCell(5)));
	    data.put("optionE", getCellStringValue(row.getCell(6)));
	    data.put("answer", getCellStringValue(row.getCell(7)));
	    data.put("numAnswers", getCellLongValue(row.getCell(8)));
	    data.put("questionTypeId", getCellStringValue(row.getCell(9)));
	    data.put("difficultyLevel", getCellLongValue(row.getCell(10)));
	    data.put("answerValue", getCellBigDecimalValue(row.getCell(11)));
	    data.put("negativeMarkValue", getCellBigDecimalValue(row.getCell(12)));
	    
	    return data;
	}

	private String getCellStringValue(Cell cell) {
	    if (cell == null) return null;
	    
	    switch (cell.getCellType()) {
	        case STRING:
	            String value = cell.getStringCellValue();
	            return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
	        case NUMERIC:
	            return String.valueOf((long) cell.getNumericCellValue());
	        case BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue());
	        case BLANK:
	        default:
	            return null;
	    }
	}

	private Long getCellLongValue(Cell cell) {
	    if (cell == null) return null;
	    
	    switch (cell.getCellType()) {
	        case NUMERIC:
	            return (long) cell.getNumericCellValue();
	        case STRING:
	            try {
	                return Long.parseLong(cell.getStringCellValue().trim());
	            } catch (NumberFormatException e) {
	                return null;
	            }
	        default:
	            return null;
	    }
	}

	private BigDecimal getCellBigDecimalValue(Cell cell) {
	    if (cell == null) return null;
	    
	    switch (cell.getCellType()) {
	        case NUMERIC:
	            return BigDecimal.valueOf(cell.getNumericCellValue());
	        case STRING:
	            try {
	                return new BigDecimal(cell.getStringCellValue().trim());
	            } catch (NumberFormatException e) {
	                return null;
	            }
	        default:
	            return null;
	    }
	}

	private boolean isRowEmpty(Row row) {
	    for (int i = 0; i < row.getLastCellNum(); i++) {
	        Cell cell = row.getCell(i);
	        if (cell != null && cell.getCellType() != CellType.BLANK) {
	            return false;
	        }
	    }
	    return true;
	}

	private String validateQuestionData(Map<String, Object> data) {
	    if (data.get("topicId") == null) return "topicId is required";
	    if (data.get("questionDetail") == null) return "questionDetail is required";
	    if (data.get("optionA") == null) return "optionA is required";
	    if (data.get("optionB") == null) return "optionB is required";
	    if (data.get("optionC") == null) return "optionC is required";
	    if (data.get("optionD") == null) return "optionD is required";
	    if (data.get("answer") == null) return "answer is required";
	    if (data.get("numAnswers") == null) return "numAnswers is required";
	    if (data.get("questionTypeId") == null) return "questionTypeId is required";
	    return null;
	}
*/
/*
	@GET
	@Path("/getquesbytopic")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getQuestionsByTopic(@Context HttpServletRequest request,@QueryParam("topicId") String topicId, @QueryParam("pageNo") String pageNoStr,@QueryParam("pageSize") String pageSizeStr ){
		 //getting dispatcher from request
  		LocalDispatcher dispatcher=(LocalDispatcher)request.getAttribute("dispatcher");
  		if(dispatcher==null) {
  			dispatcher=ServiceContainer.getLocalDispatcher("sphinx", (Delegator)request.getAttribute("delegator"));
  		}
		Map<String,Object>result=new HashMap<>();
		try {
		
			Integer pageNo=ConvertValue.toInteger(pageNoStr) ;
			Integer pageSize=ConvertValue.toInteger(pageSizeStr);

			Map<String,Object>serviceCtx=new HashMap<>();
			serviceCtx.put("topicId", topicId);
			serviceCtx.put("pageNo", pageNo);
			serviceCtx.put("pageSize",pageSize);	

			Map<String,Object>serviceResult=dispatcher.runSync("getQuestionsByTopic",serviceCtx);

			if(ServiceUtil.isError(serviceResult)) {
				result.put("status", "ERROR");
				result.put("message", ServiceUtil.getErrorMessage(serviceResult));
				return Response.status(500).entity(result).build();
			}
			result.put("status", "SUCCESS");
			result.put("topicId", serviceResult.get("topicId"));
			result.put("topicName", serviceResult.get("topicName"));
			result.put("totalCount", serviceResult.get("totalCount"));
			result.put("questionList", serviceResult.get("questionList"));

 
			result.put("pageNo",serviceResult.get("pageNo"));
			result.put("pageSize",serviceResult.get("pageSize"));
			result.put("totalPages", serviceResult.get("totalPages"));
			result.put("hasNext", serviceResult.get("hasNext"));
			result.put("hasPrevious",serviceResult.get("hasPrevious"));

			return Response.ok(result).build();

		}catch(GenericServiceException e) {
			result.put("status", "ERROR");
			result.put("message", e.getMessage());
			e.printStackTrace();
			return Response.status(500).entity(result).build();
		}
	}
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadQuestions(@Context HttpServletRequest request, @FormDataParam("file") InputStream file,@FormDataParam("file") FormDataContentDisposition fileDetail) {
		 if (file == null || fileDetail == null) {
		        return Response.status(400)
		                .entity(ServiceUtil.returnError("File not received. Check Postman key name."))
		                .build();
		    }
		String fileName=fileDetail.getFileName();
		if(!fileName.toLowerCase().endsWith(".xlsx")) {
			return Response.status(400).entity(ServiceUtil.returnError("only files with .xlsx are allowed")).build();
		}
		 //getting dispatcher from request
  		LocalDispatcher dispatcher=(LocalDispatcher)request.getAttribute("dispatcher");
  		if(dispatcher==null) {
  			dispatcher=ServiceContainer.getLocalDispatcher("sphinx", (Delegator)request.getAttribute("delegator"));
  		}
		try {
			Workbook workbook = WorkbookFactory.create(file);
			Sheet sheet = workbook.getSheetAt(0);
			List<Map<String, Object>> questions = new ArrayList<>();
			for(int i=1;i<=sheet.getLastRowNum();i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;
				Map<String, Object> question = new HashMap<>();
				List<ColumnConfig> columns=QuestionColumnConfigUtil.getColumnConfigs();
				for (ColumnConfig col : columns) {
					Cell cell = row.getCell(col.index);
 
 
					if (col.required && (cell == null || cell.getCellType() == CellType.BLANK)) {
						return Response.status(400)
										.entity(ServiceUtil.returnError(
														"Row " + i + ", Column " + col.index + " " + col.label + " is required", null))
										.build();
					}
 
					if (cell == null) {
						question.put(col.field, null);
						continue;
					}
 
 
					switch (cell.getCellType()) {
 
						case NUMERIC:
							double numVal = cell.getNumericCellValue();
 
						    if ("Number".equalsIgnoreCase(col.type)) {
 
						        if ("answerValue".equals(col.field) || "negativeMarkValue".equals(col.field)) {
						            question.put(col.field, numVal); // Double
						        } else {
						            question.put(col.field, (long) numVal); // Long
						        }
 
						    } else {
						        question.put(col.field, String.valueOf((long) numVal));
						    }
						    break;
 
						case STRING:
							String strVal = cell.getStringCellValue();
							question.put(col.field, strVal != null ? strVal.trim() : null);
							break;
 
						case BOOLEAN:
							question.put(col.field, cell.getBooleanCellValue());
							break;
 
						case BLANK:
							question.put(col.field, null);
							break;
						default:
							question.put(col.field, null);
							break;
					}
				}
				questions.add(question);
			}
			for (Map<String, ? extends Object> question : questions) {
			Map<String,Object>result=dispatcher.runSync("createQuestionService", question);

			}
			return Response.status(201).entity(ServiceUtil.returnSuccess("Question uploaded successfully")).build();
		}catch(EncryptedDocumentException  e) {
			return Response.status(500).entity(ServiceUtil.returnError(e.getMessage())).build();
		}catch(IOException e) {
			return Response.status(500).entity(ServiceUtil.returnError(e.getMessage())).build();
		}catch(GenericServiceException e) {
			return Response.status(500).entity(ServiceUtil.returnError(e.getMessage())).build();
		}	
	}
	*/
}





//=====================














/*
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;


 * QuestionResource
 * 
 * This class handles question related Requests and Responses

@Path("/question")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuestionResource {

	@POST
	@Path("/createquestion")
	public Response createQuestion(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		try {
			// transfer the data to create question service service
			// exam name, topic, question details, option A, option B, option C, option D,
			// answer

			Map<String, Object> input = new HashMap<>();
			String topicId = (String) request.getAttribute("topicId");
			String questionDetail = (String) request.getAttribute("questionDetail");
			String optionA = (String) request.getAttribute("optionA");
			String optionB = (String) request.getAttribute("optionB");
			String optionC = (String) request.getAttribute("optionC");
			String optionD = (String) request.getAttribute("optionD");
			String optionE = (String) request.getAttribute("optionE");
			String answer = (String) request.getAttribute("answer");
			long numAnswers = (Integer) request.getAttribute("numAnswers");
			String questionType = (String) request.getAttribute("questionType");
			long difficultyLevel = (Integer) request.getAttribute("difficultyLevel");
			long answerValue = (Integer) request.getAttribute("answerValue");
			long negMarkValue = (Integer) request.getAttribute("negativeMarkValue");
			input.put("topicId", topicId);
			input.put("questionDetail", questionDetail);
			input.put("optionA", optionA);
			input.put("optionB", optionB);
			input.put("optionC", optionC);
			input.put("optionD", optionD);
			input.put("optionE", optionE);
			input.put("answer", answer);
			input.put("numAnswers", numAnswers);
			input.put("questionType", questionType);
			input.put("difficultyLevel", difficultyLevel);
			input.put("answerValue", answerValue);
			input.put("negativeMarkValue", negMarkValue);

			
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			Map<String, Object> res = dispatcher.runSync("createQuestion", input);

			Map<String, Object> result = new HashMap<>();
			if (ServiceUtil.isError(res)) {
				Debug.log("built in service return value: " + input);
				result.put("Status", "ERROR");
				result.put("message",ServiceUtil.getErrorMessage(res));
				return Response.status(500).entity(result).build();

			}

			result.put("status", "Success");
			result.put("message", "Updated Successfully");
			return Response.ok(result).build();
		} catch (Exception e) {
			Debug.log("Question Master: " + e.getMessage());
			
			return Response.ok(Map.of("message", e.getMessage())).build();
		}
	}

	@POST
	@Path("/updatequestion")
	public Response updateQuestion(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		try {
			// transfer the data to create question service service
			// exam name, topic, question details, option A, option B, option C, option D,
			// answer

			Map<String, Object> input = new HashMap<>();

			String topicId = (String) request.getAttribute("topicId");
			String questionDetail = (String) request.getAttribute("questionDetail");
			String optionA = (String) request.getAttribute("optionA");
			String optionB = (String) request.getAttribute("optionB");
			String optionC = (String) request.getAttribute("optionC");
			String optionD = (String) request.getAttribute("optionD");
			String optionE = (String) request.getAttribute("optionE");
			String answer = (String) request.getAttribute("answer");
			long numAnswers = (Integer) request.getAttribute("numAnswers");
			long questionType = (Integer) request.getAttribute("questionType");
			long difficultyLevel = (Integer) request.getAttribute("difficultyLevel");
			long answerValue = (Integer) request.getAttribute("answerValue");
			long negMarkValue = (Integer) request.getAttribute("negativeMarkValue");

//		input.put("questionId", (long)request.getAttribute("questionId")); // PK is mandatory

			input.put("topicId", topicId);
			input.put("questionDetail", questionDetail);
			input.put("optionA", optionA);
			input.put("optionB", optionB);
			input.put("optionC", optionC);
			input.put("optionD", optionD);
			input.put("optionE", optionE);
			input.put("answer", answer);
			input.put("numAnswers", numAnswers);
			input.put("questionType", questionType);
			input.put("difficultyLevel", difficultyLevel);
			input.put("answerValue", answerValue);
			input.put("negativeMarkValue", negMarkValue);
			
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			Map<String, Object> res = dispatcher.runSync("updateQuestion", input);

			Map<String, Object> result = new HashMap<>();
			if (ServiceUtil.isError(res)) {
				Debug.log("built in service return value: " + input);
				result.put("Status", "ERROR");
				result.put("message",ServiceUtil.getErrorMessage(res));
				return Response.status(500).entity(result).build();

			}

			result.put("status", "Success");
			result.put("message", "Updated Successfully");
			return Response.ok(result).build();
		} catch (Exception e) {
			Debug.log("Question Master: " + e.getMessage());
			
			return Response.ok(Map.of("message", e.getMessage())).build();
		}
	}
}

*/