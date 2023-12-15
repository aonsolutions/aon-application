package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod1102023Gipuzkoa {

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
	
	public String setIdentifyNif(String text) {
		String identifyNif = "";		
		Pattern pattern = Pattern.compile(nifRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			identifyNif = matcher.group(1).trim();
		}
		return identifyNif;
	}
	
	public String setIdentifyName(String text) {
		String identifyName = "";
		String identifyNameRegex = "Apellidos y nombre o razón social\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(identifyNameRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			identifyName = matcher.group(3).trim();
		}
		
		return identifyName;
	}
	
	public String setYear(String text) {
		String year = "";
		String yearRegex = "TrimestreEkitaldia Hiruhilekoa\\s([0-9]{4})";
		
		Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			year = matcher.group(1).trim();
		}
		
		return year;
	}
	
	public String setPeriod(String text) {
		String period = "";
		String periodRegex = "TrimestreEkitaldia Hiruhilekoa\\s([0-9]{4})\\s([0-9]{1})";
		
		Pattern pattern = Pattern.compile(periodRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group(2).trim() + " Trimestre";
		}
		
		return period;
		
	}
	
	public String setAmount(String text) {
		String amount = "";
		String amountRegex = "([\\d]{1,},[\\d]{2,})A INGRESAR";
		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		return amount;
	}
	
	public String setIssueDate(String text) {
		String issueDate = "";
		String issueDateRegex = "Fecha de presentación:\\s([0-9]{1,}.[0-9]{1,}.[0-9]{2,})";
		
		Pattern pattern = Pattern.compile(issueDateRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			issueDate = matcher.group(1).trim();
		}
		
		return issueDate;
	}
	
	
}
