package com.esferalia.aon.in.payroll.pdf.modAlava;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod1152023Alava {
	
	public void setNif(String text) {
		String nif = "";
		String nifRegex = "([A-Z]{1})([0-9]{7}).([0-9]{1})";
		Pattern pattern = Pattern.compile(nifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		//while captura ambos
		if (matcher.find()) {
			nif = matcher.group().trim();
			System.out.println("NIF : " + nif);
		}
	}
	
	public void setPeriod(String text) {
		String period = "";
		String periodRegex = "PERIODO:\\s+([0-9]{6})(-)([0-9]{6})";
		
		Pattern pattern = Pattern.compile(periodRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()){
			period = matcher.group().trim();
			System.out.println(period);
		}
	}
	
	public void setName (String text) {
		String name = "";
		String nameRegex = "\\s.*SL";
		
		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			name = matcher.group().trim();
			System.out.println(name);
		}
	}
	
	public void setExercise(String text) {
		String exercise = "";
		String exerciseRegex = "EJERCICIO:\\s+([0-9]{4})";
		
		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).trim();
			System.out.println("Ejercicio: " + exercise);
		}
	}
	
	public void setAmount(String text) {
		String amount = "";
		String amountRegex = "IMPORTE:\\s+([^A-Z][^\\n].[,][0-9].)";
//		String amountRegex = "IMPORTE:\\s.([^A-Z][0-9])(,)([^A-Z][0-9])";

		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
//		System.out.println(matcher.find());
		if (matcher.find()) {
			amount = matcher.group(1).trim();
			System.out.println("Importe : " + amount);
		}
		
	}
	
	public void setLeases(String text) {
		String lease ="";
		String leaseRegex = "ARRENDAMIENTOS\\s+([^A-Z][^\\n]+)";
		
		Pattern pattern = Pattern.compile(leaseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			lease = matcher.group(1).trim();
			System.out.println("Arrendamientos : " + lease);
		}
	}
	
	public void setWithHoldings(String text) {
		String withHoldings ="";
		String leaseRegex = "RETENCIONES\\s+([^A-Z][^\\n]+)";
		
		Pattern pattern = Pattern.compile(leaseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			withHoldings = matcher.group(1).trim();
			System.out.println("Retenciones : " + withHoldings);
		}
	}
	
	public void setLessors(String text) {
		String lessors ="";
		
		String lessorsRegex = "ARRENDADORES\\s+([^A-Z][^\\n]+)";
		
		Pattern pattern = Pattern.compile(lessorsRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			lessors = matcher.group(1).trim();
			System.out.println("Arrendadores : " + lessors);
		}
	}
	
	public void setHacienda(String text) {
		ParserUtils pu = new ParserUtils();
		String hacienda ="";
		if (pu.haciendaSearch(text)) {
			hacienda = "Diputacion Foral de Alava";
		}
		System.out.println(hacienda);
	}
	
	public void setModel(String text) {
		String model = "";
		String modelRegex = "MODELO:\\s+([0-9]{3}[A-Z]{1})";
		Pattern pattern = Pattern.compile(modelRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			model = matcher.group(1).trim();
			System.out.println("MODELO: " + model);
		} 
	}
	
	public void parser(String text) {
		System.out.println("MODELO ALAVA");
		setNif(text);
		setModel(text);
		setHacienda(text);
		setExercise(text);
		setPeriod(text);
		setLeases(text);
		setLessors(text);
		setWithHoldings(text);
		setAmount(text);
		System.out.println("\n");
		
	}
	

}
