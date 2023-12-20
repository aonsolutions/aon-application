package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod39020223Gipuzkoa {
	
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
		String nif =" ";
		
		Pattern pattern = Pattern.compile(nifRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
		
		return nif;
	}
	
	public String setname(String text) {
		String name = "";
		String nameRegex = nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(nameRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		
		return name;
	}
	
	public String setIssueDate(String text) {
		String issueDate = "";
		String issueDateRegex = "Aurkezpen data / Fecha de presentación:(\\s[0-9]{1,}.[0-9]{1,}.[0-9]{2,})";
		
		Pattern pattern = Pattern.compile(issueDateRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			issueDate = matcher.group(1).trim();
		}
		
		return issueDate;
	}
	
	public String setAmount(String text) {
		String amount = "";
		String amountRegex = "([0-9]{1,}.[0-9]{1,}.[0-9]{1,})\\s.*\\s.*Aurkezpen data";
		
		Pattern pattern = Pattern.compile(amountRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		return amount;
	}
	
	public String setHacienda(String text) {
		String hacienda = "";
		String haciendaRegex = "gipuzkoa";
		
		Pattern pattern = Pattern.compile(haciendaRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			hacienda = "Diputacion Foral de Gipuzkoa";
		}
		
		return hacienda;
	}
}
