package com.esferalia.aon.in.payroll.img;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.img.PersonDocumentParsers.IPersonDocumentParser;
import com.esferalia.aon.occam.api.model.PersonDocument;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;

class DNICommonParser implements IPersonDocumentParser {

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override 
	public PersonDocument parse(String text) {
		PersonDocument person = new PersonDocument();
		setDocument(person,text);
		setName(person,text);
		setSurnames(person,text);
		setNationality(person,text);
		setBirthdate(person, text);
		setIssueAndValidityDate(person, text);
		return person;
	}
	
	private void setDocument(PersonDocument person,String text) {
		String document = "";
		String documentDNIRegex = "[\\d]?[\\d][\\s-_/\\.]?[\\d]{3}[\\s-_/\\.]?[\\d]{3}[\\s-_/]?[A-Z]";
		Pattern pattern = Pattern.compile(documentDNIRegex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			document = matcher.group();
			PersonDocumentParserValidation.validateGroup(document);
		}
		person.setDocument(document);
	}

	private void setNationality(PersonDocument person,String text) {
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

	private void setName(PersonDocument person,String text) {
	    String name = "";
	    String keyWord = "NOMBRE";
	    String keyWordPattern = "";
	    String [] lines = text.split("\\s+");
	    for (int i = 0; i < lines.length; i++) {
	    	if (AonStringUtils.getLevenshteinDistance(keyWord, lines[i]) <= 1) {
	    		keyWordPattern = lines[i];
			}
		}
	    Pattern pattern = Pattern.compile(keyWordPattern+"\\s+([^\\n]+)", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(text);
	    if (matcher.find()) {
	        name = matcher.group(1).trim();
	        PersonDocumentParserValidation.validateGroup(name);
	    }
	    person.setName(name);
	}

	private void setSurnames(PersonDocument person, String text) {
		String keyWord = "APELLIDOS";
		String keyWordPattern = "";
		String[] lines = text.split("\\s+");
		  for (int i = 0; i < lines.length; i++) {
		    	if (AonStringUtils.getLevenshteinDistance(keyWord, lines[i]) <= 1) {
		    		keyWordPattern = lines[i];
				}
			}
		String[] surnames = new String[2];
		Pattern pattern = Pattern.compile(keyWordPattern+"\\s+([^\\n]+)\\s+([^\\n]+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			surnames[0] = matcher.group(1).trim();
			PersonDocumentParserValidation.validateGroup(surnames[0]);
			surnames[1] = matcher.group(2).trim();
			PersonDocumentParserValidation.validateGroup(surnames[1]);
		}
		
		person.setFirstSurname(surnames[0]);
		person.setSecondSurname(surnames[1]);
		
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
	
	private void setIssueAndValidityDate(PersonDocument person, String text) {
	    String [] validateDate = new String[6];
	    Pattern pattern = Pattern.compile("[^a-z](\\d{2})\\s(\\d{2})\\s(\\d{4})\\s(\\d{2})\\s(\\d{2})\\s(\\d{4})");
        Matcher matcher = pattern.matcher(text);
	    while (matcher.find()) {
	    	
	    	validateDate[0] = matcher.group(1);
	    	PersonDocumentParserValidation.validateGroup(validateDate[0]);
	    	validateDate[1] = matcher.group(2);
	    	PersonDocumentParserValidation.validateGroup(validateDate[1]);
	    	validateDate[2] = matcher.group(3);
	    	PersonDocumentParserValidation.validateGroup(validateDate[2]);
	    	
	    	validateDate[3] = matcher.group(4);
	    	PersonDocumentParserValidation.validateGroup(validateDate[3]);
	    	validateDate[4] = matcher.group(5);
	    	PersonDocumentParserValidation.validateGroup(validateDate[4]);
	    	validateDate[5] = matcher.group(6);
	    	PersonDocumentParserValidation.validateGroup(validateDate[5]);

	    }
	    String issueDate = validateDate[0]+" "+ validateDate[1]+" "+ validateDate[2];      
	    String validityDate = validateDate[3]+" "+ validateDate[4]+" "+ validateDate[5];      
	    Date date = parseDates(issueDate);
	    person.setIssueDate(date);
	    date = parseDates(validityDate);
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
