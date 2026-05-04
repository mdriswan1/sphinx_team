package com.vastpro.utility;

import java.security.SecureRandom;

/**
 * This class is used auto generate password
 */
public class GeneratePssword {
	final String CHARACTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_";

	/**
	 * Method is used to generate random password by using secure random
	 * 
	 * @return auto generated password
	 */
	public String generatePassword() {
		StringBuilder builder = new StringBuilder();
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < 7; i++) {
			int ind = random.nextInt(CHARACTER.length());
			builder.append(CHARACTER.charAt(ind));
		}
		return builder.toString();
	}

	public long generateOTP() {
		SecureRandom random = new SecureRandom();
		return 100000 + random.nextInt(900000);
	}
}
