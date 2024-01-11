package com.esferalia.aon.in.payroll.pdf.modAeat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.modAeat.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod1152023AEAT implements IModelDocumentParser {
	ParserUtils pu = new ParserUtils();

	public void setExercise(FiscalModel fm, String text) {
		String exercise = "";
		String exerciseRegex = "Ejercicio\\s.*([0-9]{4})";
		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			exercise = matcher.group(1).trim();
		}
		int year = Integer.parseInt(exercise);
		fm.setYear(year);

	}

	public void setPeriod(FiscalModel fm, String text) {
		String period = "";
		String periodRegex = "Per\u00EDodo\\s.*([\\d][A-Z])";

		Pattern pattern = Pattern.compile(periodRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			period = matcher.group(1).trim();
		}
		fm.setPeriod(Period.safeValueOf(period));
	}

	public void setNif(FiscalModel fm, String text) {

		String nif = "";
		String nifRegex = "NIF Presentador:(\\s.*)";
		Pattern pattern = Pattern.compile(nifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			nif = matcher.group(1).trim();

		}
		fm.setDocument(nif);


	}

	public void setSocialReason(FiscalModel fm, String text) {
		String name = "";
		String nameRegex = "Raz\u00F3n social:(\\s.*)";
		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			name = matcher.group(1).trim();
		}

		fm.setName(name);
	}

//	public void setPerceivers(String text) {
//		String perceivers = "";
//		String perceiversRegex = "Nº de perceptores\\s.+([0-9])";
//
//		Pattern pattern = Pattern.compile(perceiversRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//
//		if (matcher.find()) {
//			perceivers = matcher.group(1).trim();
//		}
//	}
//
//	public void setWithHoldingsAndPayments(String text) {
//		String withHoldingsAndPayment = "";
//		String withHoldingsAndPaymentRegex = "Base de las retenciones e ingresos a cuenta\\s.+([0-9][.][0-9].+[,][0-9].)";
//
//		Pattern pattern = Pattern.compile(withHoldingsAndPaymentRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		if (matcher.find()) {
//			withHoldingsAndPayment = matcher.group(1).trim();
//		}
//	}
//
//	public void setWithHoldings(String text) {
//		String withHoldings = "";
//		String withHoldingsRegex = "Retenciones e ingresos a cuenta\\s.*\\s.*([0-9][.][0-9].+[,][0-9].)";
//
//		Pattern pattern = Pattern.compile(withHoldingsRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//
//		if (matcher.find()) {
//			withHoldings = matcher.group(1).trim();
//		}
//	}

	public void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "Importe:\\s.*([^A-Z][0-9].+[,][0-9].)";

		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		double total = Double.parseDouble(amount.replace(",", "."));
		fm.setDeclarationResult(total);
		
	}

	public void setStreet(FiscalModel fm , String text) {
		String street = "";
		String streetRegex = "C./Plaza/Avda.";

		Pattern pattern = Pattern.compile(streetRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			street = matcher.group().trim();
		}
		fm.setStreetName(street);
	}

	public void setModel(FiscalModel fm, String text) {
		String model = "";
		String modelRegex ="Modelo\\s([0-9]{3})";
		
		Pattern pattern = Pattern.compile(modelRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			model = matcher.group(1).trim();
			
		}
			fm.setModel(FiscalModelType.safeValueOf(model));
	}

	public String setHacienda(String text) {
		String hacienda = "";

		if (!pu.haciendaSearch(text)) {
			hacienda = "AEAT";
		}
		return hacienda;
	}

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override
	public FiscalModel parse(String text) {
		FiscalModel fiscalModel = new FiscalModel();
		setExercise(fiscalModel, text);
		setNif(fiscalModel, text);
		setAmount(fiscalModel, text);
		setPeriod(fiscalModel, text);
		setSocialReason(fiscalModel, text);
		setModel(fiscalModel, text);
		return fiscalModel;
	}

}
