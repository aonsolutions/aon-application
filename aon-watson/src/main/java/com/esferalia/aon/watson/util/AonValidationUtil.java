package com.esferalia.aon.watson.util;

public class AonValidationUtil {
	
    public static boolean isValidAccount(String code) {
    	return isValidAccount(code,true);
    }

    public static boolean isValidAccount(String code, boolean required) {
    	return required
    			?(AonStringUtils.isNotEmpty(code) && AonStringUtils.length(code) == 9)
    			:(AonStringUtils.isEmpty(code) || AonStringUtils.length(code) == 9)
    			;
    		
    }
	
    public static boolean isValidRequired(String value, boolean required) {
    	return required
    			?AonStringUtils.isNotEmpty(value)
    			:AonStringUtils.isEmpty(value)
    			;
    		
    }
    
}

