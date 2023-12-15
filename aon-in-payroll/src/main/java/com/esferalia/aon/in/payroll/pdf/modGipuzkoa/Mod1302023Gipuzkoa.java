package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod1302023Gipuzkoa {

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
	
	public String setDeclarantNif(String text) {
		String declarantNif = "";
		String declarantNifRegex = "NIF Apellidos y nombre\\s"+nifRegex;
		
		Pattern pattern = Pattern.compile(declarantNifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			declarantNif = matcher.group(1).trim();
		}
		
		return declarantNif;
	}
	
	public String setDeclarantName(String text) {
		String declarantName = "";
		String declarantNameRegex = "NIF Apellidos y nombre\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(declarantNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			declarantName = matcher.group(3).trim();
		}
		
		return declarantName;
	}
	
	public String setPrincipalActivity(String text) {
		String principalActivity = "";
		String principalActivityRegex = "Descripción de la actividad Sección IAE Epígrafe IAE(\\s.*([A-Z][^0-9]))";
		
		Pattern pattern = Pattern.compile(principalActivityRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			principalActivity = matcher.group(1).trim();
		}
		
		return principalActivity;
	}
	
	public String setAmount(String text) {
		String amount = "";
		String amountRegex = "ORDAINTZEKOA, GUZTIRA \\s.*(.[\\d],[\\d].)";
		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		return amount;
	}
	
	public String setHacienda(String text) {
		String hacienda = "";
		String haciendaRegex = "Gipuzkoa";
		
		Pattern pattern = Pattern.compile(haciendaRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			hacienda = matcher.group().trim();
		}
		
		return hacienda;
	}
	
	public String setModel(String text) {
		String model = "";
		String modelRegex = "130";
		
		Pattern pattern = Pattern.compile(modelRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			model = matcher.group().trim();
		}
		
		return model;
	}
	
	public String setExercise(String text) {
		String exercise = "";
		String exerciseRegex = "([0-4]{4})([^0-9].+)(Ejercicio)";
		
		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(2).trim();
		}
		
		return exercise;
	}
	
}
