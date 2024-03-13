package com.esferalia.aon.in.payroll.pdf.mod.Gipuzkoa;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.mod.Gipuzkoa.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod1902023Gipuzkoa implements IModelDocumentParser{

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

	private void setNif(FiscalModel fm, String text) {
		String nif = "";
		Pattern pattern = Pattern.compile(nifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			nif = matcher.group().trim();
		}
		fm.setDocument(nif);
	}

	private void setSocialReasonName(FiscalModel fm, String text) {
		String name = "";
		String nameRegex = "Apellidos y nombre o razón social\\s+" + nifRegex + "(\\s+[A-Z]+.?[A-Z]+)";
		Pattern pattern = Pattern.compile(nameRegex);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		fm.setName(name);
	}

	private void setPhoneNumber(FiscalModel fm, String text) {
		String phoneNumber = "";
		String phoneNumberRegex = "Telefonoa / Teléfono Posta elektronikoa / Correo electrónico\\s+([0-9]{9})";
		Pattern pattern = Pattern.compile(phoneNumberRegex);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			phoneNumber = matcher.group(1).trim();
		}
		fm.setContactPhone(phoneNumber);
	}

	private void setEmail(FiscalModel fm, String text) {
		String email = "";
		String emailRegex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			email = matcher.group().trim();
		}
		fm.setContactEmail(email);
	}

	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "([0-9]{1,}.?[0-9]{1,}.?[0-9]{1,}\\s+)Importe total de las retenciones e ingresos a cuenta";
		String auxAmount = "";
		Pattern pattern = Pattern.compile(amountRegex);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			amount = matcher.group(1).trim();
			auxAmount = amount.replace("." , "");
		}

		double total = Double.parseDouble(auxAmount.replace(",", "."));
		fm.setDeclarationResult(total);
	}

//	private String setIssueDate(String text) {
//		String issueDate = "";
//		String issueDateRegex = "FECHA Y FIRMA\\s+([0-9]{1,}/[0-9]{1,}/[0-9]{1,})";
//		Pattern pattern = Pattern.compile(issueDateRegex);
//		Matcher matcher = pattern.matcher(text);
//
//		if (matcher.find()) {
//			issueDate = matcher.group(1).trim();
//		}
//
//		return issueDate;
//	}
	
	private void setPeriod(FiscalModel fm, String text) {
		String period = "";
		String periodregex = "";
		Pattern pattern = Pattern.compile(periodregex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group().trim();
		}
		
		fm.setPeriod(Period.safeValueOf(period));
	}
	
	private void setExercise(FiscalModel fm , String text) {
		String exercise = "";
		String exerciseregex = "DECLARACIÓN COMPLEMENTARIA\\s+([0-9]{1,}.?[0-9]{1,}.?[0-9]{1,}.?[0-9]{1,})";
		Pattern pattern = Pattern.compile(exerciseregex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).replaceAll("\\s+", "");
		}
		
		int year = Integer.parseInt(exercise);
		fm.setYear(year);
	}

//	public List<String> setNifs(String text) {
//		List<String> lista = new ArrayList<>();
//		Pattern pattern = Pattern.compile(nifRegex);
//		Matcher matcher = pattern.matcher(text);
//		ParserUtils pu = new ParserUtils();
//		List<String> trueList = new ArrayList<>();
//
//		while (matcher.find()) {
//			lista.add(matcher.group().trim());
//
//		}
//		for (int i = 0; i < lista.size(); i++) {
//			if (pu.validateDocument(lista.get(i))) {
//				String buenNif = lista.get(i);
//				trueList.add(buenNif);
//			}
//		}
//
//		return trueList;
//	}

//	public List<String> setNames(String text) {
//		List<String> lista = new ArrayList<>();
//		Pattern pattern = Pattern.compile(nifRegex + "(\\s+[A-Z]+.?[A-Z]+.?[A-Z].*)");
//		Matcher matcher = pattern.matcher(text);
//		ParserUtils pu = new ParserUtils();
//		List<String> trueList = new ArrayList<>();
//
//		while (matcher.find()) {
//			lista.add(matcher.group(3).trim());
//
//		}
//		for (int i = 0; i < lista.size(); i++) {
//			 
//				String buenNif = lista.get(i);
//				trueList.add(buenNif);
//		}
//
//		return trueList;
//	}

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override
	public FiscalModel parse(String text) {
		FiscalModel fiscalModel = new FiscalModel();
		setNif(fiscalModel, text);
		setSocialReasonName(fiscalModel, text);
		setEmail(fiscalModel, text);
		setPhoneNumber(fiscalModel, text);
		setEmail(fiscalModel, text);
		setPeriod(fiscalModel, text);
		setExercise(fiscalModel, text);
		setAmount(fiscalModel, text);
		return fiscalModel;
	}
}
