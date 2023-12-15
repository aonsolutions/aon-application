package com.esferalia.aon.in.payroll.pdf.modBizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod1102023Bizkaia {
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

	public String setNif(String text) {
		ParserUtils pu = new ParserUtils();
		String nif = "";
		Pattern pattern = Pattern.compile(nifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			nif = matcher.group().trim();
			if (pu.validateDocument(nif)) {
				return nif;
			}
		}
		return null;
	}

	public String setDeclarant(String text) {
		String declarant = "";
		String declarantRegex = "Declarante\\s.*\\s.*" + nifRegex + "(\\s.*)";

		Pattern pattern = Pattern.compile(declarantRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			declarant = matcher.group(3).trim();
			System.out.println("Declarante : " + declarant);
		}
		return declarant;
	}

	public String setPresenter(String text) {

		String presenter = "";
		String presenterRegex = "Presentador/a\\s.*\\s.*" + nifRegex + "(\\s.*)";

		Pattern pattern = Pattern.compile(presenterRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			presenter = matcher.group(3).trim();
			System.out.println("Presentador : " + presenter);
		}

		return presenter;
	}

	public String setPresenterNif(String text) {
		String presenterNif = "";
		String prsenterNifRegex = "Presentador/a\\s.*\\s" + nifRegex;

		Pattern pattern = Pattern.compile(prsenterNifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			presenterNif = matcher.group(1).trim();
		}
		return presenterNif;
	}

	public String setEmail(String text) {
		String email = "";
		String emailRegex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";

		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			email = matcher.group().trim();
		}

		return email;
	}

	public String setPhoneNumber(String text) {
		String phoneNumber = "";
		String phoneNumberRegex = "[^a-z][0-9]{9}";

		Pattern pattern = Pattern.compile(phoneNumberRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			phoneNumber = matcher.group().trim();
		}

		return phoneNumber;
	}

	public String setAmount(String text) {
		String amount = "";
		String amountRegex = "ingresar\\s.*(\\d)(\\.\\d{3})(,\\d+)";
		String wholeNumbers = "";
		String decimalNumbers = "";
		String auxNumber = "";
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			wholeNumbers = matcher.group(1).trim();
			decimalNumbers = matcher.group(2).trim();
			auxNumber = matcher.group(3).trim();

			amount = wholeNumbers + decimalNumbers + auxNumber;
			System.out.println("A ingresar : " + amount);
		}

		return amount;
	}

	public String setYearAndPeriod(String text) {
		String year = "";
		String period = "";
		String yearRegex = "Ejercicio\\s.*\\s.*\\s([\\d]{4})\\s(.*)";

		Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			year = matcher.group(1).trim();
			period = matcher.group(2).trim();
			System.out.println("Año : " + year + " Periodo : " + period);
		}
		return "Año : " + year + " Periodo : " + period;
	}

	public String setModel(String text) {
		String model = "";
		String modelRegex = "110";
		Pattern pattern = Pattern.compile(modelRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			model = matcher.group().trim();
		}
return model;
		}

}
