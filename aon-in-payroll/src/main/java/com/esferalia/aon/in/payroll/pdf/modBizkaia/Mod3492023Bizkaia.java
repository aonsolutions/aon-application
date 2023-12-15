package com.esferalia.aon.in.payroll.pdf.modBizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod3492023Bizkaia {

	String nifRegex = "("
			//  -------- LEGAL_PERSON_NIF PATTERN  
			// -------- (1) --> X00000000
				+"[A-JUV]"
				+"[\\s]*"
				+"[-_/]?"
				+"[\\s]*"
				+"[0-9]{2}"
				+"[-_/\\.]?"
				+"[0-9]{3}"
				+"[-_/\\.]?"
				+"[0-9]{3}"
			//  -------- LEGAL_PERSON_NIF PATTERN 
			// -------- (2) --> X0000000X
			+"|"
				+"[NPQRSW]"
				+"[\\s-_/]?"
				+"[0-9]{7}"
				+"[\\s-_/]?"
				+"([A-J])"
			//  -------- DNI PATTERN 
			// -------- (1) --> 00000000X
			+"|"
				+"[0-9]?"
				+"[0-9]"
				+"[\\s-_/\\.]?"
				+"[0-9]{3}"
				+"[\\s-_/\\.]?"
				+"[0-9]{3}"
				+"[\\s-_/]?"
				+"[A-Z]"
			//  -------- NIE PATTERN 
			// -------- (1) --> X0000000X
			+"|"
				+"[XYZ]"
				+"[\\s-_/]?"
				+"[0-9]{7}"
				+"[\\s-_/]?"
				+"[A-HJ-NP-TV-Z]"
			+")"
			;
	
	public String setNifDeclarant(String text) {
		String nif = "";
		String regex = "Apellidos y nombre o razón social\\s"+nifRegex;
		
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
		
		return nif;
	}
	
	public String setNameDeclarant(String text) {
		String name = "";
		String nameRegex = "Apellidos y nombre o razón social\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		return name;
	}
	
	public String setContactPerson(String text) {
		String name = "";
		String declarantNameRegex = "Persona de contacto\\s.*(\\s.*)";
		
		Pattern pattern = Pattern.compile(declarantNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(1).trim();
		}
		return name;
	}
	
	public String setEmail(String text) {
		String email = "";
		String emailRegex = "([^a-z][0-9]{9})(.*[A-Z]@[A-Z0-9.-].*[A-Z])";
		
		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			email = matcher.group(2).trim();
		}
		
		return email;
	}
	
	public String setPhoneNumber(String text) {
		String phoneNumber = "";
		String phoneNumberRegex = "[^a-z][0-9]{9}";
		
		Pattern pattern = Pattern.compile(phoneNumberRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			phoneNumber = matcher.group().trim();
		}
		
		return phoneNumber;
	}
	
	public String setRepresentativeNif(String text) {
		String representativeNif = "";
		String representativeNifRegex = "Representante\\s.*\\s.*"+nifRegex;
		
		Pattern pattern = Pattern.compile(representativeNifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			representativeNif = matcher.group(1).trim();
		}
		
		return representativeNif;
	}
	
	public String setRepresentativeName(String text) {
		String representativeName = "";
		String representativeNameRegex = "Representante\\s.*\\s.*"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(representativeNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			representativeName = matcher.group(3).trim();
		}
		
		return representativeName;
	}
}
