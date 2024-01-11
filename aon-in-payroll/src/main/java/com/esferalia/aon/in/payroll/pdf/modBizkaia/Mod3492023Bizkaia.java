package com.esferalia.aon.in.payroll.pdf.modBizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.modBizkaia.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod3492023Bizkaia implements IModelDocumentParser{

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
		String regex = "Apellidos y nombre o razón social\\s"+nifRegex;
		
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
		
		fm.setDocument(nif);
	}
	
	public void setNameDeclarant(FiscalModel fm, String text) {
		String name = "";
		String nameRegex = "Apellidos y nombre o razón social\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		fm.setName(name);
	}
	
	public String setContactPerson(String text) {
		String name = "";
		String declarantNameRegex = "Persona de contacto\\s.*(\\s.*)";
		
		Pattern pattern = Pattern.compile(declarantNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(1).trim();
		}
		return name;
	}
	
	public void setEmail(FiscalModel fm, String text) {
		String email = "";
		String emailRegex = "([^a-z][0-9]{9})(.*[A-Z]@[A-Z0-9.-].*[A-Z])";
		
		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			email = matcher.group(2).trim();
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
	
	public String setRepresentativeNif(String text) {
		String representativeNif = "";
		String representativeNifRegex = "Representante\\s.*\\s.*"+nifRegex;
		
		Pattern pattern = Pattern.compile(representativeNifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			representativeNif = matcher.group(1).trim();
		}
		
		return representativeNif;
	}
	
	public String setRepresentativeName(String text) {
		String representativeName = "";
		String representativeNameRegex = "Representante\\s.*\\s.*"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(representativeNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			representativeName = matcher.group(3).trim();
		}
		
		return representativeName;
	}
	
	public void setExercise(FiscalModel fm, String text) {
		String exercise = "";
		String exerciseRegex = "Declaración sustitutiva([0-9]{4})";
		
		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).trim();
		}
		int auxYear = Integer.parseInt(exercise);
		fm.setYear(auxYear);
		
	}
	
	public void setPeriod(FiscalModel fm , String text) {
		String period =" ";
		String periodRegex = "Declaración sustitutiva([0-9]{4})\\s([A-Z]{1,})";
		ParserUtils pu = new ParserUtils();
		Pattern pattern = Pattern.compile(periodRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group(2).trim();
			period = pu.parsePeriodBizkaia(period);
		}
		fm.setPeriod(Period.safeValueOf(period));
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
		setExercise(fiscalModel, text);
		setPeriod(fiscalModel, text);
		return fiscalModel;
	}
}
