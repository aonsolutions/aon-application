package com.esferalia.aon.in.payroll.img;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.img.PersonDocumentParsers.IPersonDocumentParser;
import com.esferalia.aon.occam.api.model.PersonDocument;
import com.esferalia.aon.occam.api.model.type.Country;

public class DNIBasqueParser implements IPersonDocumentParser{

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override
	public PersonDocument parse(String text) {
		PersonDocument person = new PersonDocument();
		setDocument(person, text);
		setname(person, text);
		setSurnames(person, text);
		setNationality(person, text);
		setBirthdate(person, text);
		setValidityDate(person, text);
		return person;
	}
	
//	String nifRegex = "("
//			// -------- LEGAL_PERSON_NIF PATTERN
//			// -------- (1) --> X00000000
//			+ "[A-JUV]" + "[\\s]*" + "[-_/]?" + "[\\s]*" + "[0-9]{2}" + "[-_/\\.]?" + "[0-9]{3}" + "[-_/\\.]?"
//			+ "[0-9]{3}"
//			// -------- LEGAL_PERSON_NIF PATTERN
//			// -------- (2) --> X0000000X
//			+ "|" + "[NPQRSW]" + "[\\s-_/]?" + "[0-9]{7}" + "[\\s-_/]?" + "([A-J])"
//			// -------- DNI PATTERN
//			// -------- (1) --> 00000000X
//			+ "|" + "[0-9]?" + "[0-9]" + "[\\s-_/\\.]?" + "[0-9]{3}" + "[\\s-_/\\.]?" + "[0-9]{3}" + "[\\s-_/]?"
//			+ "[A-Z]"
//			// -------- NIE PATTERN
//			// -------- (1) --> X0000000X
//			+ "|" + "[XYZ]" + "[\\s-_/]?" + "[0-9]{7}" + "[\\s-_/]?" + "[A-HJ-NP-TV-Z]" + ")";
	
	private void setDocument(PersonDocument person, String text) {
		String document = "";
		String documentDNIRegex = "DNI(\\s.*)";
		Pattern pattern = Pattern.compile(documentDNIRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			document = matcher.group(1);
			PersonDocumentParserValidation.validateGroup(document);
		}
		document = document.replaceAll("[^a-zA-Z0-9]", "");
		person.setDocument(document);
	}
	
	private void setname(PersonDocument person , String text) {
		String name = "";
		String nameRegex = "NOMBRE / IZENA(\\s[A-Z]+)";
		Pattern pattern = Pattern.compile(nameRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			name = matcher.group(1).trim();
	        PersonDocumentParserValidation.validateGroup(name);
		}
		person.setName(name);
	}

	private void setSurnames(PersonDocument person, String text) {
		String surNamesRegex = "APELLIDOS / ABIZENAK(\\s[A-Z]+)(\\s[A-Z]+)";
		String[] surNames = new String[2];
		
		Pattern pattern = Pattern.compile(surNamesRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			surNames[0] = matcher.group(1).trim();
			PersonDocumentParserValidation.validateGroup(surNames[0]);
			surNames[1] = matcher.group(2).trim();
			PersonDocumentParserValidation.validateGroup(surNames[1]);
		}
		person.setFirstSurname(surNames[0]);
		person.setSecondSurname(surNames[1]);
	}
	
	private void setNationality(PersonDocument person, String text) {
		String nationalityRegex = "\\b[A-Z][A-Z]+\\b";
		Pattern pattern = Pattern.compile(nationalityRegex);
		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			String potentialNationality = matcher.group();
			PersonDocumentParserValidation.validateGroup(potentialNationality);
			for (Country country : Country.values()) {
				if (country.getIso3().equals(potentialNationality)) {
					person.setNationality(country);
					break;
				}
			}
		}
	}
	
	private void setBirthdate(PersonDocument person, String text) {
		Pattern pattern = Pattern.compile("(\\d{2})\\s(\\d{2})\\s(\\d{4})\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        String [] birthDate = new String[3];
        if (matcher.find()) {
	    	birthDate[0] = matcher.group(1);
	    	PersonDocumentParserValidation.validateGroup(birthDate[0]);
	    	birthDate[1]= matcher.group(2);
	    	PersonDocumentParserValidation.validateGroup(birthDate[1]);
	    	birthDate[2]= matcher.group(3);	    	
	    	PersonDocumentParserValidation.validateGroup(birthDate[2]);

	    }
        String birth = birthDate[0]+" "+ birthDate[1]+" "+ birthDate[2];
	    Date date = parseDates(birth);
        person.setBirthDate(date);
	}
	
	private void setValidityDate(PersonDocument person, String text) {
	    String [] validateDate = new String[3];
	    String validityRegex = "VALIDEZ / IRAUNALDIA\\s.*\\s.*\\s+([\\d]{2})\\s([\\d]{2})\\s([\\d]{4})";
	    Pattern pattern = Pattern.compile(validityRegex , Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
	    if (matcher.find()) {
	    	validateDate[0] = matcher.group(1);
	    	PersonDocumentParserValidation.validateGroup(validateDate[0]);
	    	validateDate[1] = matcher.group(2);
	    	PersonDocumentParserValidation.validateGroup(validateDate[1]);
	    	validateDate[2] = matcher.group(3);
	    	PersonDocumentParserValidation.validateGroup(validateDate[2]);
	    }
	    String validityDate = validateDate[0]+" "+ validateDate[1]+" "+ validateDate[2];      
	    Date date = parseDates(validityDate);
        person.setValidity(date);
	}
	
	
	private Date parseDates(String dateToParse) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
		try {
			return formatter.parse(dateToParse);
		} catch (ParseException e) {
			//Needs to be empty
		}
		return null;
	}
	
	
}
