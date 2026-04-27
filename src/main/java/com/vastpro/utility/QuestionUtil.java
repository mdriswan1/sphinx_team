package com.vastpro.utility;

/**
 * This class is used to do question related operations
 */
public class QuestionUtil {
	public static String convertQuesTypeId(String quesTypeId) {

		switch (quesTypeId) {
		case "SINGLE_CHOICE":
			return "Single choice";
		case "MULTI_CHOICE":
			return "Multi choice";
		case "TRUE_FALSE":
			return "True/False";
		case "FILL_BLANKS":
			return "Fill in the blank";
		case "DETAILED_ANSWER":
			return "Detailed answer";
		}

		return null;

	}
}
