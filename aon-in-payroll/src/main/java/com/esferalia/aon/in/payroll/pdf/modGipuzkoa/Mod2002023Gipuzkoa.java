package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;

public class Mod2002023Gipuzkoa implements IModelDocumentParser{

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
		String nif = " ";
		String nifRegexx = "NIF Razón social\\s" + nifRegex;

		Pattern pattern = Pattern.compile(nifRegexx);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
		fm.setDocument(nif);
	}

	private void setDeclarantName(FiscalModel fm, String text) {
		String name = "";
		String nameRegex = "NIF Razón social\\s" + nifRegex + "(\\s.*)";

		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}

		fm.setName(name);
	}

//	public String setRelationPersonNif(String text) {
//		String nif = " ";
//		String nifRegexx = "Persona con quién relacionarse\\s.*\\s" + nifRegex;
//
//		Pattern pattern = Pattern.compile(nifRegexx);
//		Matcher matcher = pattern.matcher(text);
//
//		if (matcher.find()) {
//			nif = matcher.group(1).trim();
//		}
//		return nif;
//	}
//
//	public String setRelationPersonName(String text) {
//		String name = "";
//		String nameRegex = "Persona con quién relacionarse\\s.*\\s" + nifRegex + "\\s(.*[A-Z])";
//
//		Pattern pattern = Pattern.compile(nameRegex);
//		Matcher matcher = pattern.matcher(text);
//
//		if (matcher.find()) {
//			name = matcher.group(3).trim();
//		}
//
//		return name;
//	}

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

	private void setPhoneNumber(FiscalModel fm, String text) {
		String phoneNumber = "";
		String phoneNumberRegex = "[^a-z][\\d]{9}";

		Pattern pattern = Pattern.compile(phoneNumberRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			phoneNumber = matcher.group().trim();
		}

		fm.setContactPhone(phoneNumber);
	}

//	public String setLegalRepresntators(String text) {
//		String legalRepresentator = "";
//		String textoObtenido = "";
//		List<String> lista = new ArrayList<>();
//		int i = 0;
//
//		String searchLegalRepresentatorRegex = "Declaración de los y las representantes legales de la entidad\\s.*\\s.*\\s.*\\s.*\\s.*\\s.*\\s.*\\s.*\\sImporte";
//		String legalRepresentatorRegex = "(.*([A-Z]+.?\\s))";
//
//		Pattern pattern = Pattern.compile(searchLegalRepresentatorRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		if (matcher.find()) {
//			textoObtenido = matcher.group().trim();
//			System.out.println(textoObtenido);
//			pattern = Pattern.compile(legalRepresentatorRegex);
//			matcher = pattern.matcher(textoObtenido);
//			while (matcher.find()) {
//				
//				legalRepresentator = matcher.group(1).trim();
//				if (i % 2 != 0) {
//					legalRepresentator ="NOTARIA : " + legalRepresentator ;
//				}
//				lista.add(legalRepresentator);
//				i++;
//				
//			}
//
//		}
//		return legalRepresentator;
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
		setEmail(fiscalModel, text);
		setPhoneNumber(fiscalModel, text);
		return fiscalModel;
	}

}
