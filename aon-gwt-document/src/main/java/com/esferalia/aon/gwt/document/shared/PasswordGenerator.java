package com.esferalia.aon.gwt.document.shared;

public class PasswordGenerator {
	
		public static final String NUMEROS = "0123456789";
		public static final String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		public static final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
		
		public static String getPinNumber() {
			return getPassword(NUMEROS, 4);
		}
		
		public static String getPassword() {
			return getPassword(8);
		}

		public static String getPassword(int length) {
			return getPassword(NUMEROS + MAYUSCULAS + MINUSCULAS, length);
		}

		public static String getPassword(String key, int length) {
			String pswd = "";
			for (int i = 0; i < length; i++) {
				pswd += key.charAt((int) (Math.random() * key.length()));
			}
			return pswd;
		}
	
}
