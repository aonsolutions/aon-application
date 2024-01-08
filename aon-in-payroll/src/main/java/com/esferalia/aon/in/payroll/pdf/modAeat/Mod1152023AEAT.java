package com.esferalia.aon.in.payroll.pdf.modAeat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod1152023AEAT {
	ParserUtils pu = new ParserUtils();

	public String setExercise(String text) {
		String exercise = "";
		String exerciseRegex = "Ejercicio\\s.*([0-9]{4})";

		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			exercise = matcher.group(1).trim();
		}
		return exercise;
	}

	public String setPeriod(String text) {
		String period = "";
		String periodRegex = "Per\u00EDodo\\s.*([\\d][A-Z])";

		Pattern pattern = Pattern.compile(periodRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			period = matcher.group(1).trim();
		}
		
		return period;
	}

	public String setNif(String text) {

		String nif = "";
		String nifRegex = "NIF Presentador:(\\s.*)";

		Pattern pattern = Pattern.compile(nifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			nif = matcher.group(1).trim();

		}

		return nif;
	}

	public String setSocialReason(String text) {
		String name = "";
		String nameRegex = "Raz\u00F3n social:(\\s.*)";

		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			name = matcher.group(1).trim();
		}

		return name;
	}

	public void setPerceivers(String text) {
		String perceivers = "";
		String perceiversRegex = "Nº de perceptores\\s.+([0-9])";

		Pattern pattern = Pattern.compile(perceiversRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			perceivers = matcher.group(1).trim();
			System.out.println("Perceptores : " + perceivers);
		}
	}

	public void setWithHoldingsAndPayments(String text) {
		String withHoldingsAndPayment = "";
		String withHoldingsAndPaymentRegex = "Base de las retenciones e ingresos a cuenta\\s.+([0-9][.][0-9].+[,][0-9].)";

		Pattern pattern = Pattern.compile(withHoldingsAndPaymentRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			withHoldingsAndPayment = matcher.group(1).trim();
			System.out.println("Base de las retenciones e ingresos a cuenta : " + withHoldingsAndPayment);
		}
	}

	public void setWithHoldings(String text) {
		String withHoldings = "";
		String withHoldingsRegex = "Retenciones e ingresos a cuenta\\s.*\\s.*([0-9][.][0-9].+[,][0-9].)";

		Pattern pattern = Pattern.compile(withHoldingsRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			withHoldings = matcher.group(1).trim();
			System.out.println("Retenciones e ingresos a cuenta : " + withHoldings);
		}
	}

	public String setAmount(String text) {
		String amount = "";
		String amountRegex = "Importe:\\s.*([^A-Z][0-9].+[,][0-9].)";

		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		return amount;
	}

	// maybe useful maybe trash
	public void setStreet(String text) {
		String street = "";
		String streetRegex = "C./Plaza/Avda.";

		Pattern pattern = Pattern.compile(streetRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			street = matcher.group().trim();
			System.out.println("Calle / plaza / Avenida : " + street);
		}
	}

	public String setModel(String text) {
		String model = "";
		String modelRegex ="Modelo\\s([0-9]{3})";
		
		Pattern pattern = Pattern.compile(modelRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			model = matcher.group(1).trim();
		}
		
		return model;
	}

	public String setHacienda(String text) {
		String hacienda = "";

		if (!pu.haciendaSearch(text)) {
			hacienda = "AEAT";
		}
		return hacienda;
	}

	public void parser(String text) {
		setNif(text);
//		setModel(text);
		setHacienda(text);
		setExercise(text);
		setPeriod(text);
		setSocialReason(text);
		setAmount(text);
		setWithHoldings(text);
		setPerceivers(text);
	}

}
