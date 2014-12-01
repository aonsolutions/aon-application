package com.esferalia.aon.watson.server;

import com.esferalia.aon.watson.util.AonStringUtils;


public class AonNumberUtils {

	public static Integer toInteger(String value) {
		if (!AonStringUtils.isBlank(value)) {
			return Integer.parseInt(value);
		}
		return null;
	}

}
