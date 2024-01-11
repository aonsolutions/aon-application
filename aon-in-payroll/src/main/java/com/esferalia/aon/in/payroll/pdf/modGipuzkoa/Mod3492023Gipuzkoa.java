package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod3492023Gipuzkoa implements IModelDocumentParser{

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
	
	private void setIdentifyNif(FiscalModel fm, String text) {
		String identifyNif = "";
		String identifyNifRegex = "DNI / CIF EKITALDIA"+nifRegex;
		
		Pattern pattern = Pattern.compile(identifyNifRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			identifyNif = matcher.group(1).trim();
		}
		fm.setDocument(identifyNif);
	}
	
	private void setIdentifyName(FiscalModel fm, String text) {
		String identifyName = "";
		String identifyNameRegex = "Apellidos, Nombre y Razón Social ALDIA 2 TPERIODO\\s.*(\\s.*)";
		
		Pattern pattern = Pattern.compile(identifyNameRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			identifyName = matcher.group(1).trim();
		}
		
		fm.setName(identifyName);
	}
	
	private void setExercise(FiscalModel fm, String text) {
		String exercise = "";
		String exerciseRegex = "([\\d])\\s([\\d])EJERCICIO";
		
		Pattern pattern = Pattern.compile(exerciseRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).trim() + matcher.group(2).trim();
		}		
		int year = Integer.parseInt(exercise);
		fm.setYear(year);
	}
	
	private void setPeriod(FiscalModel fm , String text) {
		String period = "";
		String periodRegex = "ALDIA\\s([0-9])\\s([A-Z])";
		
		Pattern pattern = Pattern.compile(periodRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group(1).trim() + matcher.group(2).trim();
		}
		
		fm.setPeriod(Period.safeValueOf(period));

	}
	
	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "IMPORTE\\s.*\\s.\\s([\\d]{1,}.)([\\d]{1,},)([\\d]{1,})";
		String auxAmount = "";
		Pattern pattern = Pattern.compile(amountRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim() + matcher.group(2).trim() + matcher.group(3).trim();
			auxAmount = amount.replace("." , "");
		}

		double total = Double.parseDouble(auxAmount.replace(",", "."));
		fm.setDeclarationResult(total);
	}
	
//	public List<String> operationsLines(String texto) {
//        List<String> capturedLines = new ArrayList<>();
//        
//        // Patrón regex para capturar líneas con datos después de código/país
//        Pattern patron = Pattern.compile("\\b[A-Z]{2}\\s+\\d+\\w*\\s+.+");
//        Matcher matcher = patron.matcher(texto);
//        
//        while (matcher.find()) {
//        	capturedLines.add(matcher.group());
//        }
//        return capturedLines;
//    }

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override
	public FiscalModel parse(String text) {
		FiscalModel fiscalModel = new FiscalModel();
		setIdentifyNif(fiscalModel, text);
		setIdentifyName(fiscalModel, text);
		setAmount(fiscalModel, text);
		setPeriod(fiscalModel, text);
		setExercise(fiscalModel, text);
		return fiscalModel;
	}
	
	
	
}
