package com.esferalia.aon.in.payroll.pdf.modBizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.modBizkaia.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;

public class Mod1902023Bizkaia implements IModelDocumentParser {

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
	
	public void setNifDeclarant(FiscalModel fm, String text) {
		String nif = "";
		String regex = "Declarante\\s.*\\s"+(nifRegex);
		
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
		
		fm.setDocument(nif);
	}
	
	public void setNameDeclarant(FiscalModel fm, String text) {
		String declarant = "";
		String declarantNameRegex = "Declarante\\s.*\\s.*"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(declarantNameRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			declarant = matcher.group(3).trim();
		}
		
		fm.setName(declarant);
	}
	
	public String setContactPerson(String text) {
		String contactPerson = "";
		String contactPersonRegex = "Persona de contacto(\\s.+)(\\s.+[A-Z])";
		
		Pattern pattern = Pattern.compile(contactPersonRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			contactPerson = matcher.group(2).trim();
		}
		
		return contactPerson;
	}
	
	public void setEmail(FiscalModel fm, String text) {
		String email = "";
		String emailRegex = ".*[A-Z]@[A-Z0-9.-].*[A-Z]";
		
		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			email = matcher.group().trim();
		}
		
		fm.setContactEmail(email);
	}
	
	public void setPhoneNumber(FiscalModel fm, String text) {
		String phoneNumber = "";
		String phoneNumberRegex = "[^a-z][0-9]{9}";
		
		Pattern pattern = Pattern.compile(phoneNumberRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			phoneNumber = matcher.group().trim();
		}
		
		fm.setContactPhone(phoneNumber);
	}
	
	public void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "\\sGuztira / Total\\s([\\d]{2})(\\s.*)";
		String auxAmount = "";
		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(2).trim();
			auxAmount = amount.replace("." , "");
		}

		double total = Double.parseDouble(auxAmount.replace(",", "."));
		fm.setDeclarationResult(total);
	}
	
	public String setIssueDate(String text) {
		String issueDate = "";
		String issueDateRegex = "Fecha y número de envío\\s([0-9]{2})\\s([A-Z]{1,})\\s([A-Z]{1,})\\s([A-Z]{1,})\\s([0-9]{4})";

		
		Pattern pattern = Pattern.compile(issueDateRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			issueDate= matcher.group(1).trim() + " " + matcher.group(2).trim()+ " " +  matcher.group(3).trim() + " " +  matcher.group(4).trim() + " " +  matcher.group(5).trim();
		}
		
		return issueDate;
	}

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override
	public FiscalModel parse(String text) {
		FiscalModel fiscalModel = new FiscalModel();
		setNifDeclarant(fiscalModel, text);
		setNameDeclarant(fiscalModel, text);
		setEmail(fiscalModel, text);
		setPhoneNumber(fiscalModel, text);
		setAmount(fiscalModel, text);
		return fiscalModel;
	}
}
