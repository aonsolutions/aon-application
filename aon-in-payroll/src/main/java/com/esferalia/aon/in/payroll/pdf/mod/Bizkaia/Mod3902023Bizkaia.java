package com.esferalia.aon.in.payroll.pdf.mod.Bizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.mod.Bizkaia.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod3902023Bizkaia implements IModelDocumentParser{

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
	
	private void setNif(FiscalModel fm, String text) {
		String nif = "";
		String nifRegexxx = "NIF Apellidos y nombre o razón social N.º grupo\\s"+nifRegex;
		
		Pattern pattern = Pattern.compile(nifRegexxx, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
			fm.setDocument(nif);
	}
	
	private void setName(FiscalModel fm, String text) {
		String name = "";
		String nameRegex = "NIF Apellidos y nombre o razón social N.º grupo\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(nameRegex , Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		
		fm.setName(name);
	}
	
	private void setPhoneNumber(FiscalModel fm, String text) {
		String phoneNumber = "";
		String phoneNumberRegex = "[^a-z][0-9]{9}";
		
		Pattern pattern = Pattern.compile(phoneNumberRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			phoneNumber = matcher.group().trim();
		}
		
		fm.setPhone(phoneNumber);
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
	
		
//	private String setPrincipalActivity(String text) {
//		String activity = "";
//		String activityRegex = "Actividad principal Epígrafe IAE / Código\\s*\\n([^\\d\\n]+)";
//		
//		Pattern pattern = Pattern.compile(activityRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		if (matcher.find()) {
//			activity = matcher.group().trim();
//		}
//		return activity;
//	}
//	
	private void setPresentatorName(FiscalModel fm , String text) {
		String name = "";
		String nameRegex = "Presentador/a\\sNIF Apellidos y nombre o razón social\\s"+nifRegex+"(\\s.*)";
		
		
		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		fm.setContactPerson(name);
	}
	
	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "A ingresar\\s.*([0-9]{2,}.[0-9]{2,},[0-9].)";
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
	
	private void setYear(FiscalModel fm , String text) {
		String year ="";
		String yearRegex ="Ejercicio\\s.*\\s.([0-9]){1}([0-9]{4})";
		
		Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			year = matcher.group(2).trim();
		}
		int auxYear = Integer.parseInt(year);
		fm.setYear(auxYear);
		
	}
	
	private void setPeriod(FiscalModel fm, String text) {
		String period ="";
		String yearRegex ="Ejercicio\\s.*\\s.[0-9]{1,}\\s(.+)";
		ParserUtils pu = new ParserUtils();
		Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group(1).trim();
			period = pu.parsePeriodBizkaia(period);
		}
		
		fm.setPeriod(Period.safeValueOf(period));
	}
	
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
		setNif(fiscalModel, text);
		setName(fiscalModel, text);
		setEmail(fiscalModel, text);
		setPhoneNumber(fiscalModel, text);
		setPeriod(fiscalModel, text);
		setYear(fiscalModel, text);
		setAmount(fiscalModel, text);
		setHacienda(fiscalModel, text);
		setPresentatorName(fiscalModel, text);
		return fiscalModel;
	}	
	
}
