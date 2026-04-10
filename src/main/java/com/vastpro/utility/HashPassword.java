package com.vastpro.utility;

import org.apache.sshd.common.config.keys.loader.openssh.kdf.BCrypt;

/**
 * This class is used for hashing password and checking it
 */
public class HashPassword {

	/**
	 * Method is used to hash password using bcrypt
	 * 
	 * @param password
	 *            password which we want to hash
	 * @return hashed password
	 */
	public static String hashPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(4));
	}

	public static boolean checkPassword(String rawPassword, String hashpass) {
		return BCrypt.checkpw(rawPassword, hashpass);
	}

}
