package com.esferalia.aon.in.payroll.img;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.img.PersonDocumentParsers.IPersonDocumentParser;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DNICommonParser implements IPersonDocumentParser {

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override 
	public Person parse(String text) {
		Person person = new Person();
 
		String dni = getDocumentDNI(text);
		String name = getDocumentName(text);
		String[] surnames = getDocumentSurnames(text);
		String surName1 = surnames[0];
		String surName2 = surnames[1];
		Country country = getNationality(text);

		person.setDocument(dni);
		person.setFirstSurname(surName1);
		person.setSecondSurname(surName2);
		person.setName(name);
		person.setNationality(country);
		return person;
	}

	private String getDocumentDNI(String text) {
		String document = "";
		String documentDNIRegex = "[\\d]?[\\d][\\s-_/\\.]?[\\d]{3}[\\s-_/\\.]?[\\d]{3}[\\s-_/]?[A-Z]";
		Pattern pattern = Pattern.compile(documentDNIRegex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			document = matcher.group();
		}
		return document;
	}

	private Country getNationality(String text) {
		String nationalityRegex = "\\b[A-Z][A-Z]+\\b";
		Pattern pattern = Pattern.compile(nationalityRegex);
		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			String potentialNationality = matcher.group();
			for (Country country : Country.values()) {
				if (country.getIso3().equals(potentialNationality)) {
					return country;
				}
			}
		}
		return null;
	}

	private String getDocumentName(String text) {
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
	    }
	    return name;
	}

	private String[] getDocumentSurnames(String text) {
		String keyWord = "APELLIDOS";
		String keyWordPattern = "";
		String[] lines = text.split("\\s+");
		  for (int i = 0; i < lines.length; i++) {
		    	if (AonStringUtils.getLevenshteinDistance(keyWord, lines[i]) <= 1) {
		    		keyWordPattern = lines[i];
				}
			}
		String[] apellidos = new String[2];
		Pattern pattern = Pattern.compile(keyWordPattern+"\\s+([^\\n]+)\\s+([^\\n]+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			apellidos[0] = matcher.group(1).trim();
			apellidos[1] = matcher.group(2).trim();
		}
		return apellidos;
	}
}
