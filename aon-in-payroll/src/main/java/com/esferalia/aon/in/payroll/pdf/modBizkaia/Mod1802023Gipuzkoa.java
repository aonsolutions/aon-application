package com.esferalia.aon.in.payroll.pdf.modBizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod1802023Gipuzkoa {

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
		String identifyNifRegex = "IFZ / NIF Abizenak eta izena edo sozietatearen izena / Apellidos y nombre o razón social\\s"+nifRegex;
		
		Pattern pattern = Pattern.compile(identifyNifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			identifyNif = matcher.group(1).trim();
		}
		return identifyNif;
	} 
	
	public String setIdentifyName(String text) {
		String identifyName = "";
		String identifyNameRegex = "IFZ / NIF Abizenak eta izena edo sozietatearen izena / Apellidos y nombre o razón social\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(identifyNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			identifyName = matcher.group(3).trim();
		}
		
		return identifyName;
	}
	
	public String setRelatedPersonName(String text) {
		String relatedPersonName = "";
		String relatedPersonNameRegex = "HARREMANETARAKO PERTSONA / PERSONA CON QUIEN RELACIONARSE\\s.*(\\s.*)([^A-Z][\\d]{9})";
		Pattern pattern = Pattern.compile(relatedPersonNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			relatedPersonName = matcher.group(1).trim();
		}
		return relatedPersonName;
	}
	
	public String setPhoneNumber(String text) {
		String phoneNumber = "";
		String phoneNumberRegex = "HARREMANETARAKO PERTSONA / PERSONA CON QUIEN RELACIONARSE\\s.*(\\s.*)([^A-Z][\\d]{9})";
		
		Pattern pattern = Pattern.compile(phoneNumberRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			phoneNumber = matcher.group(2).trim();
		}
		
		return phoneNumber;
	}
	
	//NO HAY DATOS PARA REPRESENTANTE
	//public String setRepresentName
	//public String setRepresentNif
	
	public String setDeclarantNif(String text) {
		String declarantNif = "";
		String declarantNifRegex = nifRegex+"\\s.*NIF declarante";
		
		Pattern pattern = Pattern.compile(declarantNifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			declarantNif = matcher.group(1).trim();
		}
		
		return declarantNif;
	}
	
	public String setExercise(String text) {
		String exercise = "";
		String exerciseRegex = ".*([0-9]{4}).*Ejercicio";
		
		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).trim();
		}
		return exercise;
	}
	
	public String setAmount(String text) {
		String amount = "";
		String amountregex = "([\\d]*)(,)([\\d]{2,})\\s.*Importe total de las retenciones e ingresos a cuenta";
		
		Pattern pattern = Pattern.compile(amountregex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim() + matcher.group(2).trim() + matcher.group(3).trim();
		}
		
		return amount;
	}
	
	public String setPerceivers(String text) {
		String perceivers = "";
		String perceiversRegex = "Jasotzaileen kopurua, guztira\\s.*\\s.*([0-9]{1,})";
		
		Pattern pattern = Pattern.compile(perceiversRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			perceivers = matcher.group(1).trim();
		}
		
		return perceivers;
	}
	
	public String setAmountsPaid(String text) {
		String amountsPaid = "";
		String amountsPaidRegex = "Egindako ordainketen zenbateko osoa\\s([0-9]{2,})\\s([0-9]{1,}.)([0-9]{1,},)([0-9]{1,})";
		
		Pattern pattern = Pattern.compile(amountsPaidRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amountsPaid = matcher.group(2).trim() + matcher.group(3).trim() + matcher.group(4).trim();
		}
		
		return amountsPaid;
	}
	
}
