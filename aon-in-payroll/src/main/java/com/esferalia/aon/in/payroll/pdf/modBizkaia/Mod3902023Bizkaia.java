package com.esferalia.aon.in.payroll.pdf.modBizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod3902023Bizkaia {

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
	
	public String setNif(String text) {
		String nif = "";
		String nifRegexxx = "NIF Apellidos y nombre o razón social N.º grupo\\s"+nifRegex;
		
		Pattern pattern = Pattern.compile(nifRegexxx, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
		
		
		return nif;
	}
	
	public String setName(String text) {
		String name = "";
		String nameRegex = "NIF Apellidos y nombre o razón social N.º grupo\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(nameRegex , Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		
		return name;
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
	
	public String setEmail(String text) {
		String email = "";
		String emailRegex = ".*[A-Z]@[A-Z0-9.-].*[A-Z]";
		
		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			email = matcher.group().trim();
		}
		
		return email;
	}
	
		
	public String setPrincipalActivity(String text) {
		String activity = "";
		String activityRegex = "Actividad principal Epígrafe IAE / Código\\s*\\n([^\\d\\n]+)";
		
		Pattern pattern = Pattern.compile(activityRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			activity = matcher.group().trim();
		}
		return activity;
	}
	
	public String setPresentatorName(String text) {
		String name = "";
		String nameRegex = "Presentador/a\\sNIF Apellidos y nombre o razón social\\s"+nifRegex+"(\\s.*)";
		
		
		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		return name;
	}
	
	public String setAmount(String text) {
		String amount = "";
		String amountRegex = "A ingresar\\s.*([0-9]{2,}.[0-9]{2,},[0-9].)";
		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		return amount;
	}
	
	public String setYear(String text) {
		String year ="";
		String yearRegex ="Ejercicio\\s.*\\s.*\\s([\\d]{4})\\s(.*)";
		
		Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			year = matcher.group(1).trim();
			System.out.println( year );
		}
		
		return year;
	}
	
	public String setPeriod(String text) {
		String period ="";
		String yearRegex ="Ejercicio\\s.*\\s.*\\s([\\d]{4})\\s(.*)";
		
		Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group(2).trim();
			System.out.println( period);
		}
		
		return period;
	}	
	
}
