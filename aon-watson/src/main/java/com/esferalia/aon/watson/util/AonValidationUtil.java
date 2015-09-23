package com.esferalia.aon.watson.util;

public class AonValidationUtil {
	
	
    public static boolean isValidAccount(String code) {
    	return (AonStringUtils.isNotEmpty(code) && AonStringUtils.length(code) == 9);
    }
    
}

