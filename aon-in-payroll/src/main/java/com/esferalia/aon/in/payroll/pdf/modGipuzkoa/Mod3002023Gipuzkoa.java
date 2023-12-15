package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod3002023Gipuzkoa {
	
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
		String nifRegexx = "Apellidos y nombre o razón social AnagramaDNI . NIF\\s"+nifRegex;
		
		
		Pattern pattern = Pattern.compile(nifRegexx);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
		
		return nif;
	} 
	
	public String setName(String text) {
		String name = "";
		String nameRegex = "Apellidos y nombre o razón social AnagramaDNI . NIF\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		
		return name;
	}
	
	public String setPeriod(String text) {
		String period = "";
		String periodRegex = "Período:(\\s[0-9]{1})";
		
		Pattern pattern = Pattern.compile(periodRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if ( matcher.find()) {
			period = matcher.group(1).trim() + "Trimestre";
		}
		return period;
	}
	
	public String setExercise(String text) {
		String exercise = "";
		String exerciseRegex = "Ejercicio:\\s([0-9]{2,})";
		
		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).trim();
		}
		
		return exercise;
	}
	
	public String setAccruedFee(String text) {
		String accruedFee = "";
		String accruedFeeRegex = "SORTUTAKO KUOTA, GUZTIRA / TOTAL CUOTA DEVENGADA\\s..\\s([0-9]{1,},[0-9]{2})";
		
		Pattern pattern = Pattern.compile(accruedFeeRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			accruedFee = matcher.group(1).trim();
		}
		
		return accruedFee;
	}
	
	//NOT FINISH
	public String setDeduct(String text) {
		String deduct ="";
		String deductRegex = "KENDU BEHARREKOA, GUZTIRA / TOTAL A DEDUCIR\\s.*\\s.*([0-9]{1,}(.)[0-9]{2,})DIFERENTZIA / DIFERENCIA ";
		Pattern pattern = Pattern.compile(deductRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			deduct = matcher.group(1).trim();
		}
		
		return deduct;
	}
	
	public String setAmount(String text) {
		String amount = "";
		String amountRegex ="([0-9]{1,}.[0-9]{2,})EMAITZA / RESULTADO";
		
		Pattern pattern = Pattern.compile(amountRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		return amount;
		
	}
	
	public String setIssueDate(String text) {
		String issueDate = "";
		String issueDateRegex ="Fecha de presentación:\\s([0-9]{1,}.[0-9]{1,}.[0-9]{2,})";
		
		Pattern pattern = Pattern.compile(issueDateRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			issueDate = matcher.group(1).trim();
		}
		
		return issueDate;
	}
	
	
}
