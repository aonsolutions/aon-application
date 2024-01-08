package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mod1902023Gipuzkoa {

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
		String nif = "";
		Pattern pattern = Pattern.compile(nifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			nif = matcher.group().trim();
		}
		return nif;
	}

	public String setSocialReasonName(String text) {
		String name = "";
		String nameRegex = "Apellidos y nombre o razón social\\s+" + nifRegex + "(\\s+[A-Z]+.?[A-Z]+)";
		Pattern pattern = Pattern.compile(nameRegex);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		return name;
	}

	public String setPhoneNumber(String text) {
		String phoneNumber = "";
		String phoneNumberRegex = "Telefonoa / Teléfono Posta elektronikoa / Correo electrónico\\s+([0-9]{9})";
		Pattern pattern = Pattern.compile(phoneNumberRegex);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			phoneNumber = matcher.group(1).trim();
		}
		return phoneNumber;
	}

	public String setEmail(String text) {
		String email = "";
		String emailRegex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			email = matcher.group().trim();
		}
		return email;
	}

	public String setAmount(String text) {
		String amount = "";
		String amountRegex = "([0-9]{1,}.?[0-9]{1,}.?[0-9]{1,}\\s+)Importe total de las retenciones e ingresos a cuenta";
		Pattern pattern = Pattern.compile(amountRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}

		return amount;
	}

	public String setIssueDate(String text) {
		String issueDate = "";
		String issueDateRegex = "FECHA Y FIRMA\\s+([0-9]{1,}/[0-9]{1,}/[0-9]{1,})";
		Pattern pattern = Pattern.compile(issueDateRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			issueDate = matcher.group(1).trim();
		}

		return issueDate;
	}
	
	public String setPeriod(String text) {
		String period = "";
		String periodregex = "";
		Pattern pattern = Pattern.compile(periodregex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group().trim();
		}
		
		return period;
	}
	
	public String setExercise(String text) {
		String exercise = "";
		String exerciseregex = "DECLARACIÓN COMPLEMENTARIA\\s+([0-9]{1,}.?[0-9]{1,}.?[0-9]{1,}.?[0-9]{1,})";
		Pattern pattern = Pattern.compile(exerciseregex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).replaceAll("\\s+", "");
		}
		
		return exercise;
	}

	public List<String> setNifs(String text) {
		List<String> lista = new ArrayList<>();
		Pattern pattern = Pattern.compile(nifRegex);
		Matcher matcher = pattern.matcher(text);
		ParserUtils pu = new ParserUtils();
		List<String> trueList = new ArrayList<>();

		while (matcher.find()) {
			lista.add(matcher.group().trim());

		}
		for (int i = 0; i < lista.size(); i++) {
			if (pu.validateDocument(lista.get(i))) {
				String buenNif = lista.get(i);
				trueList.add(buenNif);
			}
		}

		return trueList;
	}

	public List<String> setNames(String text) {
		List<String> lista = new ArrayList<>();
		Pattern pattern = Pattern.compile(nifRegex + "(\\s+[A-Z]+.?[A-Z]+.?[A-Z].*)");
		Matcher matcher = pattern.matcher(text);
		ParserUtils pu = new ParserUtils();
		List<String> trueList = new ArrayList<>();

		while (matcher.find()) {
			lista.add(matcher.group(3).trim());

		}
		for (int i = 0; i < lista.size(); i++) {
			 
				String buenNif = lista.get(i);
				trueList.add(buenNif);
		}

		return trueList;
	}
}
