package com.esferalia.aon.in.payroll.pdf.modAlava;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.modAlava.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod3002023Alava implements IModelDocumentParser {

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

	
	
	public void setDeclarantNif(FiscalModel fm, String text) {
		String nif = "";
		String nifRegexx = "eta Aurrekontu Saila Finanzas y Presupuestos\\s.*\\s.*([A-Z]{1}[0-9]{1,})";
		Pattern pattern = Pattern.compile(nifRegexx);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
		fm.setDocument(nif);
	}
	
	public void setDeclarantName(FiscalModel fm, String text) {
		String declarantName = "";
		String declarantNameRegex = "eta Aurrekontu Saila Finanzas y Presupuestos\\s.*(\\s([A-Z]+[\\D].?)*)";
		Pattern pattern = Pattern.compile(declarantNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			declarantName = matcher.group(1).trim();
		}
		fm.setName(declarantName);
	}
	
	public String setPresentatorName(String text) {
		String presentatorName = "";
		String presentatorNameRegex ="eta Aurrekontu Saila Finanzas y Presupuestos\\s.*\\s.*(\\s([A-Z]+[\\D].?)*)";
		
		Pattern pattern = Pattern.compile(presentatorNameRegex);
		Matcher matcher = pattern.matcher(text);
		
		
		if (matcher.find()) {
			presentatorName = matcher.group(1).trim();
		}
		
		return presentatorName;
	}
	
	public String setPresentatorNif(String text) {
		String presentatorNif = "";
		String presentatorNifRegex = "eta Aurrekontu Saila Finanzas y Presupuestos\\s.*\\s.*\\s.*([A-Z]{1}[0-9]{1,})";
		
		Pattern pattern = Pattern.compile(presentatorNifRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			presentatorNif = matcher.group(1).trim();
		}
		
		return presentatorNif;
	}
	
	public void setModel(FiscalModel fm, String text) {
		String model = "";
		String modelRegex = "MODELO:\\s([0-9]{3})";
		
		Pattern pattern = Pattern.compile(modelRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			model = matcher.group(1).trim();
		}
		
		fm.setModel(FiscalModelType.safeValueOf(model));
	}
	
	public void setPeriod(FiscalModel fm, String text) {
		String period = "";
		String periodRegex = "PERIODO:\\s+([0-9]{6}-[0-9]{6})";
		ParserUtils pu = new ParserUtils();
		Pattern pattern = Pattern.compile(periodRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			period = matcher.group(1).trim();
			period = pu.parsePeriodAlava(period);
		}
		fm.setPeriod(Period.safeValueOf(period));
	}
	
	public void setExercise(FiscalModel fm, String text) {
		String exercise = "";
		String exerciseRegex = "EJERCICIO:\\s([0-9]{2,})";
		
		Pattern pattern = Pattern.compile(exerciseRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).trim();
		}
		int year = Integer.parseInt(exercise);
		fm.setYear(year);
	}
	
	public void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "Importe:\\s([0-9]{1,}.?[0-9]{1,}.?[0-9]{1,})";
		String[] amountSplit = null;
		Pattern pattern = Pattern.compile(amountRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
			amountSplit = amount.split(",");
		}
		double total = Double.parseDouble(amountSplit[0]);
		fm.setDeclarationResult(total);		
	}
	
//	public String setTypeOfPayment(String text) {
//		String typeOfPayment = "";
//		String typeOfPaymentRegex = "Opción de pago seleccionada:(\\s.*)";
//		
//		Pattern pattern = Pattern.compile(typeOfPaymentRegex);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			typeOfPayment = matcher.group(1).trim();
//		}
//		
//		return typeOfPayment;
//	}
//	
//	public void setIssueDate(String text) {
//		String issueDate = "";
//		String issueDateRegex = "Fecha de cargo:\\s([0-9]{1,}.?[0-9]{1,}.?[0-9]{1,})";
//		
//		Pattern pattern = Pattern.compile(issueDateRegex);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			issueDate = matcher.group(1).trim();
//		}
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
		setAmount(fiscalModel, text);
		setModel(fiscalModel, text);
		setExercise(fiscalModel, text);
		setPeriod(fiscalModel, text);
		return fiscalModel;
	}
	
	
	
}
