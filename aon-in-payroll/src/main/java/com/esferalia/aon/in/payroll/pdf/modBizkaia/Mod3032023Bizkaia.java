package com.esferalia.aon.in.payroll.pdf.modBizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod3032023Bizkaia {

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
	
	public String setPersonNif(String text) {
		String nif = "";
		String nifPersonRegex = "NIF Apellidos y nombre o razón social\\s"+nifRegex;
		Pattern pattern = Pattern.compile(nifPersonRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
		return nif;
	}
	
	public String setPersonName(String text) {
		String personName = "";
		String personNameRegex = "NIF Apellidos y nombre o razón social\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(personNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			personName = matcher.group(3).trim();
		}
		
		return personName;
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
	
	public String setPrincipalActivity(String text) {
		String activity = "";
		String activityRegex = "Actividad principal Epígrafe IAE / Código\\s*\\n([^\\d\\n]+)";
		
		Pattern pattern = Pattern.compile(activityRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			activity = matcher.group(1).trim();
		}
		return activity;
	}
	
	public String setPresentatorNif(String text) {
		String presentatorNif = "";
		String presentatorNifRegex = "Presentador/a\\s.*\\s"+nifRegex;
		
		Pattern pattern = Pattern.compile(presentatorNifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			presentatorNif = matcher.group(1).trim();
		}
		
		return presentatorNif;
	}

	public String setPresentatorName(String text) {
		String presentator = "";
		String presentatorRegex = "Presentador/a\\s.*\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(presentatorRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			presentator = matcher.group(3).trim();
		}
		
		return presentator;
	}
	
	public String setAmount(String text) {
		String amount = "";
		String amountRegex = "A ingresar\\s.*([0-9].[0-9]{2,}.*[0-9])";
		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		return amount;
	}
	
	public String setModel(String text) {
		String model = "";
		String modelRegex = "303";
		
		Pattern pattern = Pattern.compile(modelRegex, Pattern.CASE_INSENSITIVE);
		Matcher macther = pattern.matcher(text);
		
		if (macther.find()) {
			model = macther.group().trim();
		}
		return model;
	}
	
	
	
	public void setYearAndPeriod(String text) {
		String year ="";
		String period ="";
		String yearRegex ="Ejercicio Período\\s.*\\s.*(20[0-9]{2})\\s.([A-Z]*[0-9])";
		
		Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			year = matcher.group(1).trim();
			period = matcher.group(2).trim();
			System.out.println("Año : " + year + " Periodo : " + period);
		}
	}
	
	public String setIssueDate(String text) {
		String issueDate = "";

		String issueDateRegex = "Fecha y número de envío\\s([0-9]{2})\\s([A-Z]{1,})\\s([A-Z]{1,})\\s([A-Z]{1,})\\s([0-9]{4})";
		Pattern pattern = Pattern.compile(issueDateRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			issueDate= matcher.group(1).trim() + " " + matcher.group(2).trim()+ " " +  matcher.group(3).trim() + " " +  matcher.group(4).trim() + " " +  matcher.group(5).trim();
		}
		
		return issueDate;
		
	}
	
	public String setHacienda(String text) {
		String hacienda = "";
		String haciendaRegex = "Bizkaia";
		
		Pattern pattern = Pattern.compile(haciendaRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			hacienda = matcher.group().trim();
		}
				
		return hacienda;
	}
	
	
	
	
}
