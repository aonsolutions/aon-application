package com.esferalia.aon.gwt.common.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public final class Dni implements Serializable{
	
	private static final char[] LETTERS = {'T','R','W','A','G','M','Y','F','P','D','X','B','N','J','Z','S','Q','V','H','L','C','K','E'};

	protected Dni(){
		super();
	}
	
	public static boolean checkDNI(String document) {
        if (document == null || document.length() != 9) return false;

        try {
            String dniNumberWithoutLetter = document.substring(0, 8);
            char letter = Character.toUpperCase(document.charAt(8));
            int dniNumber = Integer.parseInt(dniNumberWithoutLetter);
            int letterIndex = dniNumber % 23;
            return letter == LETTERS[letterIndex];
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean checkNIE(String document) {
        if (document == null || document.length() != 9) return false;

        try {
            char firstChar = Character.toUpperCase(document.charAt(0));
            String numericPrefix;

            switch (firstChar) {
                case 'X': numericPrefix = "0"; break;
                case 'Y': numericPrefix = "1"; break;
                case 'Z': numericPrefix = "2"; break;
                default: return false;
            }

            String nieNumberWithoutLetter = document.substring(1, 8);
            char letter = Character.toUpperCase(document.charAt(8));
            int nieNumber = Integer.parseInt(numericPrefix + nieNumberWithoutLetter);
            int letterIndex = nieNumber % 23;

            return letter == LETTERS[letterIndex];
        } catch (NumberFormatException e) {
            return false;
        }
    }
	
}
