package com.esferalia.aon.gwt.payroll.shared;

public final class StringUtils {
	
	public static String leftPad(String input, Integer outputSize, Character leftPadChar) {
		Integer size = outputSize;
		String outputStr = input;
		
		while (outputStr.length() < size)
			outputStr = leftPadChar + outputStr;
		
		return outputStr;
	}
	
	public static String rightPad(String input, Integer outputSize, Character rightPadChar) {
		Integer size = outputSize;
		String outputStr = input;
		
		while (outputStr.length() < size)
			outputStr = outputStr + rightPadChar;
		
		return outputStr;
	}
	
}
