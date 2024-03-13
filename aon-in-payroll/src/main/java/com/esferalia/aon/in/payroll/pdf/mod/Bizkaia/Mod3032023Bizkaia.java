package com.esferalia.aon.in.payroll.pdf.mod.Bizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.mod.Bizkaia.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod3032023Bizkaia implements IModelDocumentParser{

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
	
	private void setPersonNif(FiscalModel fm, String text) {
		String nif = "";
		String nifPersonRegex = "NIF Apellidos y nombre o razón social\\s"+nifRegex;
		Pattern pattern = Pattern.compile(nifPersonRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
		fm.setDocument(nif);
	}
	
	private void setPersonName(FiscalModel fm, String text) {
		String personName = "";
		String personNameRegex = "NIF Apellidos y nombre o razón social\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(personNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			personName = matcher.group(3).trim();
		}
		
		fm.setName(personName);
	}
	
	private void setEmail(FiscalModel fm, String text) {
		String email = "";
		String emailRegex = ".*[A-Z]@[A-Z0-9.-].*[A-Z]";
		
		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			email = matcher.group().trim();
		}
		
		fm.setContactEmail(email);
	}
	

	private void setPhoneNumber(FiscalModel fm, String text) {
		String phoneNumber = "";
		String phoneNumberRegex = "[^a-z][0-9]{9}";
		
		Pattern pattern = Pattern.compile(phoneNumberRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			phoneNumber = matcher.group().trim();
		}
		
		fm.setContactPhone(phoneNumber);
	}
	
//	private String setPrincipalActivity(String text) {
//		String activity = "";
//		String activityRegex = "Actividad principal Epígrafe IAE / Código\\s*\\n([^\\d\\n]+)";
//		
//		Pattern pattern = Pattern.compile(activityRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		if (matcher.find()) {
//			activity = matcher.group(1).trim();
//		}
//		return activity;
//	}
	
//	private String setPresentatorNif(String text) {
//		String presentatorNif = "";
//		String presentatorNifRegex = "Presentador/a\\s.*\\s"+nifRegex;
//		
//		Pattern pattern = Pattern.compile(presentatorNifRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			presentatorNif = matcher.group(1).trim();
//		}
//		
//		return presentatorNif;
//	}
//
	private void setPresentatorName(FiscalModel fm , String text) {
		String presentator = "";
		String presentatorRegex = "Presentador/a\\s.*\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(presentatorRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			presentator = matcher.group(3).trim();
		}
		fm.setContactPerson(presentator);
		
	}
	
	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "A ingresar\\s.*([0-9].[0-9]{2,}.*[0-9])";
		String auxAmount = "";
		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
			auxAmount = amount.replace("." , "");
		}

		double total = Double.parseDouble(auxAmount.replace(",", "."));
		fm.setDeclarationResult(total);
		
	}
	
	private void setModel(FiscalModel fm, String text) {
		String model = "";
		String modelRegex = "303";
		
		Pattern pattern = Pattern.compile(modelRegex, Pattern.CASE_INSENSITIVE);
		Matcher macther = pattern.matcher(text);
		
		if (macther.find()) {
			model = macther.group().trim();
		}
		fm.setModel(FiscalModelType.safeValueOf(model));
	}
	
	
	
	private void setExercise(FiscalModel fm, String text) {
		String exercise ="";
		String yearRegex ="Ejercicio Período\\s.*\\s.*(20[0-9]{2})\\s.([A-Z]*[0-9])";
		
		Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise= matcher.group(1).trim();
		}
		
		int year = Integer.parseInt(exercise);
		fm.setYear(year);
	}
	
	private void setPeriod(FiscalModel fm, String text) {
		String period ="";
		String yearRegex ="Ejercicio Período\\s.*\\s.*(20[0-9]{2})\\s.([A-Z]*[0-9])";
		
		Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group(2).trim();
		}
		
		fm.setPeriod(Period.safeValueOf(period));
	}
	
	
	
//	private String setIssueDate(String text) {
//		String issueDate = "";
//
//		String issueDateRegex = "Fecha y número de envío\\s([0-9]{2})\\s([A-Z]{1,})\\s([A-Z]{1,})\\s([A-Z]{1,})\\s([0-9]{4})";
//		Pattern pattern = Pattern.compile(issueDateRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		if (matcher.find()) {
//			issueDate= matcher.group(1).trim() + " " + matcher.group(2).trim()+ " " +  matcher.group(3).trim() + " " +  matcher.group(4).trim() + " " +  matcher.group(5).trim();
//		}
//		
//		return issueDate;
//		
//	}
//	
	private void setHacienda(FiscalModel fm , String text) {
		ParserUtils pu = new ParserUtils();
		if (pu.haciendaSearch(text)) {
			fm.setAdministration(Administration.BIZKAIA);
		}
	}

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override
	public FiscalModel parse(String text) {
		FiscalModel fiscalModel = new FiscalModel();
		setPersonNif(fiscalModel, text);
		setPersonName(fiscalModel, text);
		setEmail(fiscalModel, text);
		setAmount(fiscalModel, text);
		setExercise(fiscalModel, text);
		setPeriod(fiscalModel, text);
		setPhoneNumber(fiscalModel, text);
		setModel(fiscalModel, text);
		setHacienda(fiscalModel, text);
		setPresentatorName(fiscalModel, text);
		return fiscalModel;
	}
	
	
	
	
}
