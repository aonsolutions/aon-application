package com.esferalia.aon.in.payroll.pdf.mod.Bizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.mod.Bizkaia.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod1152023Bizkaia implements IModelDocumentParser {

	private void setEmail(FiscalModel fm, String text) {
		String email = "";
		String emailRegex = ".*[A-Z]@[A-Z0-9.-].*[A-Z]";

		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			email = matcher.group().trim();
		}

		fm.setContactEmail(email);
	}

	private void setNif(FiscalModel fm, String text) {
		String nif = "";
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
		Pattern pattern = Pattern.compile(nifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			nif = matcher.group().trim();
			ParserUtils pu = new ParserUtils();
			pu.validateDocument(nif);
			if (pu.validateDocument(nif)) {
				break;
			}

		}
		fm.setDocument(nif);
	}

	private void setYear(FiscalModel fm, String text) {
		String year = "";

		String yearRegex = "Ejercicio\\s.*\\s.*\\s([\\d]{4})\\s(.*)";

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
		String yearRegex = "Ejercicio\\s.*\\s.*\\s([\\d]{4})\\s(.*)";
		ParserUtils pu = new ParserUtils();
		Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			period = matcher.group(2).trim();
			period = pu.parsePeriodBizkaia(period);

		}
		fm.setPeriod(Period.safeValueOf(period));
	}

	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "ingresar\\s.*\\s([\\d]+)(,[\\d]+)";
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

	private void setDeclarant(FiscalModel fm, String text) {
		String declarant = "";
		String declarantRegex = "(Declarante\\s.*)(\\s.*)([A-Z]{1,}[0-9]{7,}[A-Z].)([A-Za-z].+)";

		Pattern pattern = Pattern.compile(declarantRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			declarant = matcher.group(4).trim();
		}

		fm.setName(declarant);
	}

	private void setPresenter(FiscalModel fm, String text) {
		String presenter = "";
		String presenterRegex = "(Presentador/a\\s.*)(\\s.*)([0-9]{8,}[A-Z]{1,})(\\s.*)";

		Pattern pattern = Pattern.compile(presenterRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			presenter = matcher.group(4).trim();
	
		}
		fm.setContactPerson(presenter);
	}

	public void setHacienda(FiscalModel fm , String text) {
		ParserUtils pu = new ParserUtils();
		if (pu.haciendaSearch(text)) {
			fm.setAdministration(Administration.BIZKAIA);
		}
	}

//	public void setModel(String text) {
//		ParserUtils pu = new ParserUtils();
//		String model = "";
//		if (pu.modelSearch(text)) {
//			model = "115";	
//		}
//		System.out.println("Modelo : " + model);
//	}

	@Override
	public boolean accept(String text) {
		
		return true;
	}

	@Override
	public FiscalModel parse(String text) {
		FiscalModel fiscalModel = new FiscalModel();
		setDeclarant(fiscalModel, text);
		setNif(fiscalModel, text);
		setEmail(fiscalModel, text);
		setAmount(fiscalModel, text);
		setPeriod(fiscalModel, text);
		setYear(fiscalModel, text);
		setHacienda(fiscalModel, text);
		setPresenter(fiscalModel, text);
		return fiscalModel;
	}

}
