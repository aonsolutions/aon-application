package com.esferalia.aon.in.payroll.pdf.mod.Bizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.mod.Bizkaia.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod1102023Bizkaia implements IModelDocumentParser {
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
		ParserUtils pu = new ParserUtils();
		String nif = "";
		Pattern pattern = Pattern.compile(nifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			nif = matcher.group().trim();
			if (pu.validateDocument(nif)) {
				fm.setDocument(nif);
				break;
			}
		}
	}

	private void setDeclarant(FiscalModel fm, String text) {
		String declarant = "";
		String declarantRegex = "Declarante\\s.*\\s.*" + nifRegex + "(\\s.*)";

		Pattern pattern = Pattern.compile(declarantRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			declarant = matcher.group(3).trim();
		}
		fm.setName(declarant);
	}

	private void setPresenter(FiscalModel fm , String text) {

		String presenter = "";
		String presenterRegex = "Presentador/a\\s.*\\s.*" + nifRegex + "(\\s.*)";

		Pattern pattern = Pattern.compile(presenterRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			presenter = matcher.group(3).trim();
			fm.setContactPerson(presenter);
		}
	}

//	private String setPresenterNif(String text) {
//		String presenterNif = "";
//		String prsenterNifRegex = "Presentador/a\\s.*\\s" + nifRegex;
//
//		Pattern pattern = Pattern.compile(prsenterNifRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//
//		if (matcher.find()) {
//			presenterNif = matcher.group(1).trim();
//		}
//		return presenterNif;
//	}

	private void setEmail(FiscalModel fm, String text) {
		String email = "";
		String emailRegex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";

		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			email = matcher.group().trim();
		}

		fm.setContactEmail(email);
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

	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "ingresar\\s.*(\\d)(\\.\\d{3})(,\\d+)";
		String wholeNumbers = "";
		String decimalNumbers = "";
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			wholeNumbers = matcher.group(1).trim();
			decimalNumbers = matcher.group(2).trim();

			amount = wholeNumbers + decimalNumbers;
		}

		double total = Double.parseDouble(amount.replace(",", "."));
		fm.setDeclarationResult(total);
	}

	private void setYearAndPeriod(FiscalModel fm, String text) {
		String year = "";
		String period = "";
		String yearRegex = "Ejercicio\\s.*\\s.*\\s([\\d]{4})\\s(.*)";
		ParserUtils pu = new ParserUtils();
		Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			year = matcher.group(1).trim();
			period = matcher.group(2).trim();
			period = pu.parsePeriodBizkaia(period);
		}
		int auxYear = Integer.parseInt(year);
		fm.setYear(auxYear);
		fm.setPeriod(Period.safeValueOf(period));
	}

	private void setModel(FiscalModel fm, String text) {
		String model = "";
		String modelRegex = "110";
		Pattern pattern = Pattern.compile(modelRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			model = matcher.group().trim();
		}
		fm.setModel(FiscalModelType.safeValueOf(model));
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
		setDeclarant(fiscalModel, text);
		setEmail(fiscalModel, text);
		setPhoneNumber(fiscalModel, text);
		setModel(fiscalModel, text);
		setYearAndPeriod(fiscalModel, text);
		setAmount(fiscalModel, text);
		setHacienda(fiscalModel, text);
		setPresenter(fiscalModel, text);
		return fiscalModel;
	}

}
