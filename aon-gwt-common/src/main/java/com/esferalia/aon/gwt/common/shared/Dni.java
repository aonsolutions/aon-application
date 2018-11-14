package com.esferalia.aon.gwt.common.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Dni implements Serializable{
	
	private char[] letters = 	new char[]{'T','R','W','A','G','M','Y','F','P','D','X','B','N','J','Z','S','Q','V','H','L','C','K','E'};
	private char[] lettersMin = new char[]{'t','r','w','a','g','m','y','f','p','d','x','b','n','j','z','s','q','v','h','l','c','k','e'};
	private String dni;
	
	public Dni(){
		super();
	}
	
	public Dni(String dni){
		this.dni = dni;
	}
	
	public boolean checkDNI(){
		if(null == this.dni || "" == this.dni)
			return false;
		
		char letter = this.dni.toCharArray()[8];
		String dniNumberWithoutLetter = this.dni.substring(0, 8);
		Integer dniNumber = Integer.parseInt(dniNumberWithoutLetter);
		
		Integer letterNum = dniNumber % 23;
		
		return (letter == letters[letterNum] || letter == lettersMin[letterNum]) ? true : false;
	}
	
}
