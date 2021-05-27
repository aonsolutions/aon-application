package com.esferalia.aon.watson.util;


public class AonCadasdralReferenceUtils {

	private static char[] ALTERNATIVE = {'?','A','B','C','D','E','F','G','H','I','J','K','L','M','N'
		,'Ñ','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private static int[] PRODUCT = { 13, 15, 12, 5, 4, 17, 9, 21, 3, 7, 1};
	private static char[] RESULT = { 'M', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U',
			'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'B',
			'Z', 'X' };

	public static boolean isValidCadasdralReference(String code) {
		if (AonStringUtils.isBlank(code))
			return false;
		if (code.length() != 20)
			return false;
		if (code.charAt(18) != getDigit(code.substring(0, 7)
				+ code.substring(14, 18)))
			return false;
		if (code.charAt(19) != getDigit(code.substring(7, 14)
				+ code.substring(14, 18)))
			return false;
		return true;
	}
	
	private static char getDigit(String str) {
		int r = 0;
		for (int i = 0; i < str.length(); i++) {
			int d = (int) str.charAt(i);
			if (d < 48 || d > 57 ) {
				for (int x = 0; x < ALTERNATIVE.length; x++) {
					 if (ALTERNATIVE[x] == str.charAt(i)) {
						 d = x;
						 break;
					 }
				}
			} else {
				d = d - 48;	
			}
			r = r + (d * PRODUCT[i]);
		}
		char c = RESULT[ r % 23 ];
		return c;
	}
}
