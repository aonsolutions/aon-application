package com.esferalia.aon.in.payroll.pdf.mod.Bizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.mod.Bizkaia.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.type.Administration;

public class Mod1802023Bizkaia implements IModelDocumentParser {

	String nifRegex = "("
			// -------- LEGAL_PERSON_NIF PATTERN
			// -------- (1) --> X00000000
			+ "[A-JUV]" + "[\\s]*" + "[-_/]?" + "[\\s]*" + "[0-9]{2}" + "[-_/\\.]?" + "[0-9]{3}" + "[-_/\\.]?"
			+ "[0-9]{3}"
			// -------- LEGAL_PERSON_NIF PATTERN
			// -------- (2) --> X0000000X
			+ "|" + "[NPQRSW]" + "[\\s-_/]?" + "[0-9]{7}" + "[\\s-_/]?" + "([A-J])"
			// -------- DNI PATTERN
			// -------- (1) --> 00000000X
			+ "|" + "[0-9]?" + "[0-9]" + "[\\s-_/\\.]?" + "[0-9]{3}" + "[\\s-_/\\.]?" + "[0-9]{3}" + "[\\s-_/]?"
			+ "[A-Z]"
			// -------- NIE PATTERN
			// -------- (1) --> X0000000X
			+ "|" + "[XYZ]" + "[\\s-_/]?" + "[0-9]{7}" + "[\\s-_/]?" + "[A-HJ-NP-TV-Z]" + ")";

	private void setNifDeclarant(FiscalModel fm, String text) {
		String nif = "";
		String regex = "Declarante\\s.*\\s" + (nifRegex);

		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}

		fm.setDocument(nif);
	}

//	private String setIssueDate(String text) {
//		String issueDate = "";
//		String issueDateRegex = "Fecha y número de envío\\s([0-9]{2})\\s([A-Z]{1,})\\s([A-Z]{1,})\\s([A-Z]{1,})\\s([0-9]{4})";
//
//		Pattern pattern = Pattern.compile(issueDateRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//
//		if (matcher.find()) {
//			issueDate = matcher.group(1).trim() + " " + matcher.group(2).trim() + " " + matcher.group(3).trim() + " "
//					+ matcher.group(4).trim() + " " + matcher.group(5).trim();
//		}
//
//		return issueDate;
//	}

	private void setNameDeclarant(FiscalModel fm, String text) {
		String declarant = "";
		String declarantNameRegex = "Declarante\\s.*\\s.*" + nifRegex + "(\\s.*)";

		Pattern pattern = Pattern.compile(declarantNameRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			declarant = matcher.group(3).trim();
		}

		fm.setName(declarant);
	}

	private void setContactPerson(FiscalModel fm, String text) {
		String contactPerson = "";
		String contactPersonRegex = "Persona de contacto(\\s.+)(\\s.+[A-Z])";

		Pattern pattern = Pattern.compile(contactPersonRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			contactPerson = matcher.group(2).trim();
		}
		fm.setContactPerson(contactPerson);


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

	private void setExercise(FiscalModel fm, String text) {
		String exercise = "";
		String exerciseRegex = "Ekitaldia / Ejercicio(\\s.*)([0-9]{4})";

		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			exercise = matcher.group(2).trim();
		}

		int year = Integer.parseInt(exercise);
		fm.setYear(year);
	}

	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String auxAmount = "";
		String amountRegex = "(\\s.*)\\sGuztira / Total";

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
		String modelRegex = "180";
		Pattern pattern = Pattern.compile(modelRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			model = matcher.group().trim();
		}
		fm.setModel(FiscalModelType.safeValueOf(model));

	}
	
	public void setHacienda(FiscalModel fm , String text) {
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
		setNifDeclarant(fiscalModel, text);
		setNameDeclarant(fiscalModel, text);
		setModel(fiscalModel, text);
		setAmount(fiscalModel, text);
		setExercise(fiscalModel, text);
		setPhoneNumber(fiscalModel, text);
		setHacienda(fiscalModel, text);
		setContactPerson(fiscalModel, text);
		return fiscalModel;
	}

}
