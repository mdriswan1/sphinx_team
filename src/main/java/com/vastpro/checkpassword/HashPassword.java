package com.vastpro.checkpassword;

import org.apache.sshd.common.config.keys.loader.openssh.kdf.BCrypt;


public class HashPassword {
	
	 

	    public static String hashPassword(String password) {
	    	return BCrypt.hashpw(password, BCrypt.gensalt(4));
	    }
	    public static boolean checkPassword(String rawPassword, String hashpass) {
	    	return BCrypt.checkpw(rawPassword, hashpass);
	    }
	    public static void main(String[] args) {
			System.out.println(hashPassword("356a192b7913cf4c54574d18bdf24699395428d4"));
		}

}
