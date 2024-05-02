package com.esferalia.aon.in.payroll.pdf.mod.Gipuzkoa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.mod.Gipuzkoa.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod1152023Gipuzkoa implements IModelDocumentParser {

	private void setPeriod(FiscalModel fm,String text) {
		String period ="";
		String periodRegex ="Periodo:(\\s.*)";
		ParserUtils pu = new ParserUtils();
		
		Pattern pattern = Pattern.compile(periodRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group(1).trim();

			period = period.replace(" ", "");
			period = pu.parsePeriodGipuzkoa(period);
		}
		fm.setPeriod(Period.safeValueOf(period));
	}
	
	private void setExercise(FiscalModel fm, String text) {
		String exercise = "";
		String exerciseRegex = "Ejercicio\\s.*([0-9]{4})";
		
		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).trim();
		}
		
		int auxYear = Integer.parseInt(exercise);
		fm.setYear(auxYear);
	}
	
	private void setNif(FiscalModel fm, String text) {
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
		}
		fm.setDocument(nif);
	}
	
	private void setSocialReason(FiscalModel fm, String text) {
		String name ="";
		String nameRegex ="nombre o razón social\\s+([A-Z]{1}[0-9]{8})\\s([A-Z].*)";
		
		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(2).trim();
		}
		
		fm.setName(name);
	}
	
	private void setAmount(FiscalModel fm, String text) {
		String amount ="";
		String amountRegex ="ORDAINTZEKOA\\s+([^\\n]+)";
		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		double total = Double.parseDouble(amount.replace(",", "."));
		fm.setDeclarationResult(total);
	}
	
//	private String setPresentationDate(String text) {
//		String presentationDate ="";
//		String presentationDateRegex = "Aurkezpen data / Fecha de presentación:\\s+([0-9]{2}[/][0-9]{2}[/][0-9]{4})";
//		
//		Pattern pattern = Pattern.compile(presentationDateRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			presentationDate= matcher.group(1).trim();
//			System.out.println("Fecha de presentacion :" + presentationDate);
//		}
//		
//		return presentationDate;
//	}
//	
	public void setHacienda(FiscalModel fm , String text) {
		ParserUtils pu = new ParserUtils();
		if (pu.haciendaSearch(text)) {
			fm.setAdministration(Administration.GIPUZKOA);
		}
	}
	
//	public void setModel(String text) {
//		ParserUtils pu = new ParserUtils();
//		String model = "";
//		if (pu.modelSearch(text)) {
//			model = "115";	
//		}
//		System.out.println("Modelo : " + model);
//	}
	

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override
	public FiscalModel parse(String text) {
		FiscalModel fiscalModel = new FiscalModel();
		setNif(fiscalModel, text);
		setSocialReason(fiscalModel, text);
		setAmount(fiscalModel, text);
		setExercise(fiscalModel, text);
		setPeriod(fiscalModel, text);
		setHacienda(fiscalModel, text);
		return fiscalModel;
	}
	
	
}
