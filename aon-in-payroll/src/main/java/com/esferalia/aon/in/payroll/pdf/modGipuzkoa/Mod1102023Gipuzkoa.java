package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod1102023Gipuzkoa implements IModelDocumentParser {

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
		Pattern pattern = Pattern.compile(nifRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			identifyNif = matcher.group(1).trim();
		}
		fm.setDocument(identifyNif);
	}
	
	private void setIdentifyName(FiscalModel fm, String text) {
		String identifyName = "";
		String identifyNameRegex = "Apellidos y nombre o razón social\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(identifyNameRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			identifyName = matcher.group(3).trim();
		}
		
		fm.setName(identifyName);
	}
	
	private void setYear(FiscalModel fm, String text) {
		String year = "";
		String yearRegex = "TrimestreEkitaldia Hiruhilekoa\\s([0-9]{4})";
		
		Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			year = matcher.group(1).trim();
		}
		int auxYear = Integer.parseInt(year);
		fm.setYear(auxYear);
		
	}
	
	private void setPeriod(FiscalModel fm, String text) {
		String period = "";
		String periodRegex = "TrimestreEkitaldia Hiruhilekoa\\s([0-9]{4})\\s([0-9]{1})";
		ParserUtils pu = new ParserUtils();
		Pattern pattern = Pattern.compile(periodRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group(2).trim() + " Trimestre";
			System.out.println(period);
			period = pu.parsePeriodGipuzkoa(period);
		}
		fm.setPeriod(Period.safeValueOf(period.replace(" ", "")));
		
	}
	
	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "([\\d]{1,},[\\d]{2,})A INGRESAR";
		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		double total = Double.parseDouble(amount.replace(",", "."));
		fm.setDeclarationResult(total);
	}
	
//	private String setIssueDate(String text) {
//		String issueDate = "";
//		String issueDateRegex = "Fecha de presentación:\\s([0-9]{1,}.[0-9]{1,}.[0-9]{2,})";
//		
//		Pattern pattern = Pattern.compile(issueDateRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			issueDate = matcher.group(1).trim();
//		}
//		
//		return issueDate;
//	}

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override
	public FiscalModel parse(String text) {
		FiscalModel fiscalModel = new FiscalModel();
		setIdentifyNif(fiscalModel, text);
		setIdentifyName(fiscalModel, text);
		setPeriod(fiscalModel, text);
		setYear(fiscalModel, text);
		setAmount(fiscalModel, text);
		return fiscalModel;
	}
	
	
}
