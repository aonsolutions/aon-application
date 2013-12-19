package com.code.aon.file.tax;

import org.apache.commons.lang.StringUtils;

public class FileTaxUtil {
	private static char[] SEEK= new char[]{'á','é','í','ó','ú','Á','É','Í','Ó','Ú','º','ª'};
	private static char[] ALTER = new char[]{'a','e','i','o','u','A','E','I','O','U',' ',' '};

	public static String changeInvalidCharacters(String token) {
		if (StringUtils.isNotBlank(token)) {
			for (int i = 0; i < SEEK.length ; i ++) {
				token = StringUtils.replaceChars(token, SEEK[i], ALTER[i]);
			}
		}
		return token;
	}

}
