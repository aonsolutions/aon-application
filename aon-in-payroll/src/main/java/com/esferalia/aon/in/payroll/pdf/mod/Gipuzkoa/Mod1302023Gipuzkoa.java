package com.esferalia.aon.in.payroll.pdf.mod.Gipuzkoa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.mod.Gipuzkoa.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod1302023Gipuzkoa implements IModelDocumentParser{

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
	
	private void setDeclarantNif(FiscalModel fm, String text) {
		String declarantNif = "";
		String declarantNifRegex = "NIF Apellidos y nombre\\s"+nifRegex;
		
		Pattern pattern = Pattern.compile(declarantNifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			declarantNif = matcher.group(1).trim();
		}
		
		fm.setDocument(declarantNif);
	}
	
	private void setDeclarantName(FiscalModel fm, String text) {
		String declarantName = "";
		String declarantNameRegex = "NIF Apellidos y nombre\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(declarantNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			declarantName = matcher.group(3).trim();
		}
		
		fm.setName(declarantName);
	}
	
//	private String setPrincipalActivity(String text) {
//		String principalActivity = "";
//		String principalActivityRegex = "Descripción de la actividad Sección IAE Epígrafe IAE(\\s.*([A-Z][^0-9]))";
//		
//		Pattern pattern = Pattern.compile(principalActivityRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			principalActivity = matcher.group(1).trim();
//		}
//		
//		return principalActivity;
//	}
	
	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "ORDAINTZEKOA, GUZTIRA \\s.*(.[\\d],[\\d].)";
		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		double total = Double.parseDouble(amount.replace(",", "."));
		fm.setDeclarationResult(total);
	}
	
//	private String setHacienda(String text) {
//		String hacienda = "";
//		String haciendaRegex = "Gipuzkoa";
//		
//		Pattern pattern = Pattern.compile(haciendaRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			hacienda = matcher.group().trim();
//		}
//		
//		return hacienda;
//	}
	
	private void setModel(FiscalModel fm, String text) {
		String model = "";
		String modelRegex = "130";
		
		Pattern pattern = Pattern.compile(modelRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			model = matcher.group().trim();
		}
		
		fm.setModel(FiscalModelType.safeValueOf(model));
	}
	
	private void setExercise(FiscalModel fm, String text) {
		String exercise = "";
		String exerciseRegex = "([0-9]{4})\\s([0-9].\\s[A-Z-a-z]{1,})";
		
		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			exercise = matcher.group(1).trim();
		}
		
		int year = Integer.parseInt(exercise);
		fm.setYear(year);
	}
	
	
	private void setPeriod(FiscalModel fm , String text) {
		String period = "";
		String periodRegex = "([0-9]{4})\\s([0-9].\\s[A-Z-a-z]{1,})";
		ParserUtils pu = new ParserUtils();
		
		Pattern pattern = Pattern.compile(periodRegex , Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			period = matcher.group(2).trim();
			period = pu.parsePeriodGipuzkoa(period);
			System.out.println(period);
		}
		fm.setPeriod(Period.safeValueOf(period.replace(" ", "")));
	}
	
	public void setHacienda(FiscalModel fm , String text) {
		ParserUtils pu = new ParserUtils();
		if (pu.haciendaSearch(text)) {
			fm.setAdministration(Administration.GIPUZKOA);
		}
	}

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override
	public FiscalModel parse(String text) {
		FiscalModel fiscalModel = new FiscalModel();
		setDeclarantNif(fiscalModel, text);
		setDeclarantName(fiscalModel, text);
		setAmount(fiscalModel, text);
		setExercise(fiscalModel, text);
		setModel(fiscalModel, text);
		setPeriod(fiscalModel, text);
		setHacienda(fiscalModel, text);
		return fiscalModel;
	}
	
}
