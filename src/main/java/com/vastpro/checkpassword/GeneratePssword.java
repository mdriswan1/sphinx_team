package com.vastpro.checkpassword;

import java.security.SecureRandom;

public class GeneratePssword {
	final String CHARACTER="ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890 !@#$%^&*()_";
	public String generatePassword(int digit) {
		StringBuilder builder=new StringBuilder();
		SecureRandom random=new SecureRandom();
		for(int i=0;i<digit;i++) {
			int ind=random.nextInt(CHARACTER.length());
			builder.append(CHARACTER.charAt(ind));
		}
		return builder.toString();
	}
	

}
