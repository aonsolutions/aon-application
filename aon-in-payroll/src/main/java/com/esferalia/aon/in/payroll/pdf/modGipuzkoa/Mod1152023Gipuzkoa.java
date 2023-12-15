package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod1152023Gipuzkoa {

	public void setPeriod(String text) {
		String period ="";
		String periodRegex ="Periodo:(\\s.*)";
		
		Pattern pattern = Pattern.compile(periodRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group(1).trim();
			System.out.println("PERIODO: " + period);
		}
	}
	
	public void setExercise(String text) {
		String exercise = "";
		String exerciseRegex = "Ejercicio\\s.*([0-9]{4})";
		
		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).trim();
			System.out.println("EJERCICIO: " + exercise);
		}
	}
	
	public void setNif(String text) {
		String nif = "";
		String nifRegex ="("
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
				+")";
		
		Pattern pattern = Pattern.compile(nifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			nif = matcher.group().trim();
			System.out.println("NIF: " + nif);
		}
	}
	
	public void setSocialReason(String text) {
		String name ="";
		String nameRegex ="nombre o razón social\\s+([A-Z]{1}[0-9]{8})\\s([A-Z].*)";
		
		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(2).trim();
			System.out.println("NOMBRE: " + name);
		}
	}
	
	public void setAmount(String text) {
		String amount ="";
		String amountRegex ="ORDAINTZEKOA\\s+([^\\n]+)";
		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
			System.out.println("Pago: " + amount);
		}
	}
	
	public void setPresentationDate(String text) {
		String presentationDate ="";
		String presentationDateRegex = "Aurkezpen data / Fecha de presentación:\\s+([0-9]{2}[/][0-9]{2}[/][0-9]{4})";
		
		Pattern pattern = Pattern.compile(presentationDateRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			presentationDate= matcher.group(1).trim();
			System.out.println("Fecha de presentacion :" + presentationDate);
		}
	}
	
	public void setHacienda(String text) {
		ParserUtils pu = new ParserUtils();
		String hacienda ="";
		if (pu.haciendaSearch(text)) {
			hacienda = "Diputacion foral de Gipuzkoa";
		}
		System.out.println("Hacienda : " + hacienda);
	}
	
//	public void setModel(String text) {
//		ParserUtils pu = new ParserUtils();
//		String model = "";
//		if (pu.modelSearch(text)) {
//			model = "115";	
//		}
//		System.out.println("Modelo : " + model);
//	}
	
	public void parser(String text) {
		System.out.println("MODELO GIPUZKOA");
		setNif(text);
		setSocialReason(text);
//		setModel(text);
		setPeriod(text);
		setExercise(text);
		setHacienda(text);
		setPresentationDate(text);
		setAmount(text);
		System.out.println("\n");
	}
	
	
}
