package com.esferalia.aon.payroll.tgss.cra;

public class StringUtils {
	
	public static String leftPad(String input, Integer outputSize, Character leftPadChar) {
		Integer size = outputSize;
		String outputStr = input;
		
		if(outputStr.length() < size){
			while (outputStr.length() < size)
				outputStr = leftPadChar + outputStr;
		} else {
			outputStr = input.substring((input.length() - outputSize), input.length());
		}
		
		
		return outputStr;
	}
	
	public static String rightPad(String input, Integer outputSize, Character rightPadChar) {
		Integer size = outputSize;
		String outputStr = input;
		
		if(outputStr.length() < size){
			while (outputStr.length() < size)
				outputStr = outputStr + rightPadChar;
		} else {
			outputStr = input.substring(0, outputSize);
		}
		
		return outputStr;
	}
	
	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isSpace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

}
