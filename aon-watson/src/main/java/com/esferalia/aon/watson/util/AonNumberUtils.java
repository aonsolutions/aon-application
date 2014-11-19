package com.esferalia.aon.watson.util;


public class AonNumberUtils {

	public static Integer toInteger(String value) {
		if (!AonStringUtils.isBlank(value)) {
			return Integer.parseInt(value);
		}
		return null;
	}

}
