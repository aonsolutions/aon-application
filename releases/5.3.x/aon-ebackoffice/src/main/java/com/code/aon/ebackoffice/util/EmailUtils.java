package com.code.aon.ebackoffice.util;

import org.apache.commons.validator.EmailValidator;

public class EmailUtils {
	
	public static boolean validateEmailAddress(String sEmail){
		EmailValidator emailValidator = EmailValidator.getInstance();
		return emailValidator.isValid(sEmail);
	}
		

}
