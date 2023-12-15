package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod3492023Gipuzkoa {

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
		String identifyNifRegex = "DNI / CIF EKITALDIA"+nifRegex;
		
		Pattern pattern = Pattern.compile(identifyNifRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			identifyNif = matcher.group(1).trim();
		}
		return identifyNif;
	}
	
	public String setIdentifyName(String text) {
		String identifyName = "";
		String identifyNameRegex = "Apellidos, Nombre y Razón Social ALDIA 2 TPERIODO\\s.*(\\s.*)";
		
		Pattern pattern = Pattern.compile(identifyNameRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			identifyName = matcher.group(1).trim();
		}
		
		return identifyName;
	}
	
	public String setExercise(String text) {
		String exercise = "";
		String exerciseRegex = "([\\d])\\s([\\d])EJERCICIO";
		
		Pattern pattern = Pattern.compile(exerciseRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).trim() + matcher.group(2).trim();
		}		
		return exercise;
	}
	
	public String setPeriod(String text) {
		String period = "";
		String periodRegex = "ALDIA\\s([0-9])\\s([A-Z])";
		
		Pattern pattern = Pattern.compile(periodRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group(1).trim() + matcher.group(2).trim();
		}
		
		return period;
	}
	
	public String setAmount(String text) {
		String amount = "";
		String amountRegex = "IMPORTE\\s.*\\s.\\s([\\d]{1,}.)([\\d]{1,},)([\\d]{1,})";
		
		Pattern pattern = Pattern.compile(amountRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim() + matcher.group(2).trim() + matcher.group(3).trim();
		}
		
		return amount;
	}
	
	public List<String> operationsLines(String texto) {
        List<String> capturedLines = new ArrayList<>();
        
        // Patrón regex para capturar líneas con datos después de código/país
        Pattern patron = Pattern.compile("\\b[A-Z]{2}\\s+\\d+\\w*\\s+.+");
        Matcher matcher = patron.matcher(texto);
        
        while (matcher.find()) {
        	capturedLines.add(matcher.group());
        }
        return capturedLines;
    }
	
	
	
}
