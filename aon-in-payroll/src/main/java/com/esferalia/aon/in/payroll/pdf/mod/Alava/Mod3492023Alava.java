package com.esferalia.aon.in.payroll.pdf.mod.Alava;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.mod.Alava.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod3492023Alava implements IModelDocumentParser {

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

	private void setDeclarantNif(FiscalModel fm, String text) {
		String nif = "";
		String nifRegexx = "DECLARANTE:\\s" + nifRegex;

		Pattern pattern = Pattern.compile(nifRegexx);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}

		fm.setDocument(nif);
	}

	private void setDeclarantName(FiscalModel fm, String text) {
		String declarantName = "";
		String declarantNameRegex = "DECLARANTE:\\s" + nifRegex + "(\\s.*)";

		Pattern pattern = Pattern.compile(declarantNameRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			declarantName = matcher.group(3).trim();
		}

		fm.setName(declarantName);
	}

//	private void setModel(FiscalModel fm, String text) {
//		String model = "";
//		String modelRegex = "MODELO\\s([0-9]{3})";
//
//		Pattern pattern = Pattern.compile(modelRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		if (matcher.find()) {
//			model = matcher.group(1).trim();
//		}
//		fm.setModel(FiscalModelType.safeValueOf(model));
//	}

	private void setExercise(FiscalModel fm, String text) {
		String exercise = "";
		String exerciseRegex = "F.SUSTIT.\\s*([0-9].?[0-9]{3})";

		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			exercise = matcher.group(1).trim();
		}
		String auxYear = exercise.replace(".", "");
		int year = Integer.parseInt(auxYear);
		fm.setYear(year);
	}

	private void setPeriod(FiscalModel fm, String text) {
		String period = "";
		String periodRegex = "F.SUSTIT.\\s*([0-9].?[0-9]{3})\\s.+([0-9][A-Z])";

		Pattern pattern = Pattern.compile(periodRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			period = matcher.group(2).trim();			
		}
		fm.setPeriod(Period.safeValueOf(period));
	}

//	private String setIssueDate(String text) {
//		String issueDate = "";
//		String issueDateRegex = "F.SUSTIT.\\s*([0-9].?[0-9]{3})\\s.+([0-9][A-Z])\\s+([0-9]{1,}.?[0-9]{1,}.?[0-9]{2,})";
//
//		Pattern pattern = Pattern.compile(issueDateRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//
//		if (matcher.find()) {
//			issueDate = matcher.group(3).trim();
//
//		}
//
//		return issueDate;
//	}

	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "IMPORTE:\\s+([0-9]{1,}.?[0-9]{1,})";

		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}

		double total = Double.parseDouble(amount.replace(",", "."));
		fm.setDeclarationResult(total);

	}
	
	public void setHacienda(FiscalModel fm, String text) {
		ParserUtils pu = new ParserUtils();
		String hacienda ="";
		if (pu.haciendaSearch(text)) {
			hacienda = "Araba/Alava";
			fm.setAdministration(Administration.safeValueOf(hacienda));
		}
	}

//	private List<String> setOperatorsNif(String text) {
//
//		String operatorNifRegex = "[A-Z]{2}-[\\d]+[A-Za-z]?";
//
//		Pattern pattern = Pattern.compile(operatorNifRegex, Pattern.DOTALL);
//		Matcher matcher = pattern.matcher(text);
//		List<String> list = new ArrayList<>();
//
//		while (matcher.find()) {
//			String operatorNif = matcher.group().trim();
//			list.add(operatorNif);
//
//		}
//
//		return list;
//	}

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override
	public FiscalModel parse(String text) {
		FiscalModel fiscalModel = new FiscalModel();
		setDeclarantNif(fiscalModel, text);
		setDeclarantName(fiscalModel, text);
		setExercise(fiscalModel, text);
		setPeriod(fiscalModel, text);
		setAmount(fiscalModel, text);
		setHacienda(fiscalModel, text);
		return fiscalModel;
	}
}
