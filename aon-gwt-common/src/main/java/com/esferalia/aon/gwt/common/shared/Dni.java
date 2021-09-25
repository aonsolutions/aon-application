package com.esferalia.aon.gwt.common.shared;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
public final class Dni implements Serializable{
	
	private static char[] letters = 	new char[]{'T','R','W','A','G','M','Y','F','P','D','X','B','N','J','Z','S','Q','V','H','L','C','K','E'};
	private static char[] lettersMin = new char[]{'t','r','w','a','g','m','y','f','p','d','x','b','n','j','z','s','q','v','h','l','c','k','e'};
	
	protected Dni(){
		super();
	}
	
	public static boolean checkDNI(String document){
		if(AonStringUtils.isBlank(document)) return false;
		
		char letter = document.toCharArray()[8];
		String dniNumberWithoutLetter = document.substring(0, 8);
		Integer dniNumber = Integer.parseInt(dniNumberWithoutLetter);
		
		Integer letterNum = dniNumber % 23;
		
		return letter == letters[letterNum] || letter == lettersMin[letterNum];
	}
	
}
