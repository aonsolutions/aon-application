package com.esferalia.aon.in.payroll.pdf.modAlava;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod3002023Alava {

	String nifRegex = "("
			// -------- LEGAL_PERSON_NIF PATTERN
			// -------- (1) --> X00000000
			+ "[A-JUV]" + "[\\s]*" + "[-_/]?" + "[\\s]*" + "[0-9]{2}" + "[-_/\\.]?" + "[0-9]{3}" + "[-_/\\.]?"
			+ "[0-9]{3}"
			// -------- LEGAL_PERSON_NIF PATTERN
			// -------- (2) --> X0000000X
			+ "|" + "[NPQRSW]" + "[\\s-_/]?" + "[0-9]{7}" + "[\\s-_/]?" + "([A-J])"
			// -------- DNI PATTERN
			// -------- (1) --> 00000000X
			+ "|" + "[0-9]?" + "[0-9]" + "[\\s-_/\\.]?" + "[0-9]{3}" + "[\\s-_/\\.]?" + "[0-9]{3}" + "[\\s-_/]?"
			+ "[A-Z]"
			// -------- NIE PATTERN
			// -------- (1) --> X0000000X
			+ "|" + "[XYZ]" + "[\\s-_/]?" + "[0-9]{7}" + "[\\s-_/]?" + "[A-HJ-NP-TV-Z]" + ")";

	
	//NO COGE EL DNI
	public String setDeclarantNif(String text) {
		String nif = "";
		String nifRegexx = "eta Aurrekontu Saila Finanzas y Presupuestos\\s.*\\s.*([A-Z]{1}[0-9]{1,})";
		Pattern pattern = Pattern.compile(nifRegexx);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
	
		return nif;

	}
	
	public String setDeclarantName(String text) {
		String declarantName = "";
		String declarantNameRegex = "eta Aurrekontu Saila Finanzas y Presupuestos\\s.*(\\s([A-Z]+[\\D].?)*)";
		Pattern pattern = Pattern.compile(declarantNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			declarantName = matcher.group(1).trim();
		}
		return declarantName;
	}
	
	public String setPresentatorName(String text) {
		String presentatorName = "";
		String presentatorNameRegex ="eta Aurrekontu Saila Finanzas y Presupuestos\\s.*\\s.*(\\s([A-Z]+[\\D].?)*)";
		
		Pattern pattern = Pattern.compile(presentatorNameRegex);
		Matcher matcher = pattern.matcher(text);
		
		
		if (matcher.find()) {
			presentatorName = matcher.group(1).trim();
		}
		
		return presentatorName;
	}
	
	public String setPresentatorNif(String text) {
		String presentatorNif = "";
		String presentatorNifRegex = "eta Aurrekontu Saila Finanzas y Presupuestos\\s.*\\s.*\\s.*([A-Z]{1}[0-9]{1,})";
		
		Pattern pattern = Pattern.compile(presentatorNifRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			presentatorNif = matcher.group(1).trim();
		}
		
		return presentatorNif;
	}
	
	public String setModel(String text) {
		String model = "";
		String modelRegex = "MODELO:\\s([0-9]{3})";
		
		Pattern pattern = Pattern.compile(modelRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			model = matcher.group(1).trim();
		}
		
		return model;
	}
	
	public String setPeriod(String text) {
		String period = "";
		String periodRegex = "PERIODO:\\s+([0-9]{6})(-)([0-9]{6})";
		
		Pattern pattern = Pattern.compile(periodRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group().trim();
		}
		
		return period;
	}
	
	public String setExercise(String text) {
		String exercise = "";
		String exerciseRegex = "EJERCICIO:\\s([0-9]{2,})";
		
		Pattern pattern = Pattern.compile(exerciseRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group().trim();
		}
		return exercise;
	}
	
	public String setAmount(String text) {
		String amount = "";
		String amountRegex = "Importe:\\s([0-9]{1,}.?[0-9]{1,}.?[0-9]{1,})";
		
		Pattern pattern = Pattern.compile(amountRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		return amount;
		
	}
	
	public String setTypeOfPayment(String text) {
		String typeOfPayment = "";
		String typeOfPaymentRegex = "Opción de pago seleccionada:(\\s.*)";
		
		Pattern pattern = Pattern.compile(typeOfPaymentRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			typeOfPayment = matcher.group(1).trim();
		}
		
		return typeOfPayment;
	}
	
	public String setIssueDate(String text) {
		String issueDate = "";
		String issueDateRegex = "Fecha de cargo:\\s([0-9]{1,}.?[0-9]{1,}.?[0-9]{1,})";
		
		Pattern pattern = Pattern.compile(issueDateRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			issueDate = matcher.group(1).trim();
		}
		return issueDate;
	}
	
	
	
}
