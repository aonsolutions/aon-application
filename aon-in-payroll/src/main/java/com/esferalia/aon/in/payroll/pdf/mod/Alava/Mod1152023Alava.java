package com.esferalia.aon.in.payroll.pdf.mod.Alava;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.mod.Alava.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod1152023Alava implements IModelDocumentParser {
	
	private void setNif(FiscalModel fm, String text) {
		String nif = "";
		String nifRegex = "([A-Z]{1})([0-9]{7}).([0-9]{1})";
		Pattern pattern = Pattern.compile(nifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		//while captura ambos
		if (matcher.find()) {
			nif = matcher.group().trim();
		}
		fm.setDocument(nif);
		
	}
	
	private void setPeriod(FiscalModel fm, String text) {
		String period = "";
		String periodRegex = "PERIODO:\\s+([0-9]{6}-[0-9]{6})";
		ParserUtils pu = new ParserUtils();

		Pattern pattern = Pattern.compile(periodRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()){
			period = matcher.group(1).trim();
			period = pu.parsePeriodAlava(period);
			}
			fm.setPeriod(Period.safeValueOf(period));
	}
	
	private void setName (FiscalModel fm, String text) {
		String name = "";
		String nameRegex = "\\s.*SL";
		
		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			name = matcher.group().trim();
		}
		fm.setName(name);
	}
	
	private void setExercise(FiscalModel fm, String text) {
		String exercise = "";
		String exerciseRegex = "EJERCICIO:\\s+([0-9]{4})";
		
		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).trim();
		}
		int year = Integer.parseInt(exercise);
		fm.setYear(year);
		
	}
	
	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "IMPORTE:\\s+([^A-Z][^\\n].[,][0-9].)";

		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		double total = Double.parseDouble(amount.replace(",", "."));
		fm.setDeclarationResult(total);
		
	}
	
//	public String setLeases(String text) {
//		String lease ="";
//		String leaseRegex = "ARRENDAMIENTOS\\s+([^A-Z][^\\n]+)";
//		
//		Pattern pattern = Pattern.compile(leaseRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			lease = matcher.group(1).trim();
//		}
//		
//		return lease;
//	}
//	
//	public String setWithHoldings(String text) {
//		String withHoldings ="";
//		String leaseRegex = "RETENCIONES\\s+([^A-Z][^\\n]+)";
//		
//		Pattern pattern = Pattern.compile(leaseRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			withHoldings = matcher.group(1).trim();
//			System.out.println("Retenciones : " + withHoldings);
//		}
//		
//		return withHoldings;
//	}
//	
//	public String setLessors(String text) {
//		String lessors ="";
//		
//		String lessorsRegex = "ARRENDADORES\\s+([^A-Z][^\\n]+)";
//		
//		Pattern pattern = Pattern.compile(lessorsRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			lessors = matcher.group(1).trim();
//		}
//		return lessors;
//	}
//	
	public void setHacienda(FiscalModel fm, String text) {
		ParserUtils pu = new ParserUtils();
		String hacienda ="";
		if (pu.haciendaSearch(text)) {
			hacienda = "Araba/Alava";
			fm.setAdministration(Administration.safeValueOf(hacienda));
		}
	}
	
	private void setModel(FiscalModel fm, String text) {
		String model = "";
		String modelRegex = "MODELO:\\s+([0-9]{3}[A-Z]{1})";
		Pattern pattern = Pattern.compile(modelRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			model = matcher.group(1).trim();
		} 
		
		fm.setModel(FiscalModelType.safeValueOf(model));
	}

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override
	public FiscalModel parse(String text) {
		FiscalModel fiscalModel = new FiscalModel();
		setNif(fiscalModel, text);
		setName(fiscalModel, text);
		setAmount(fiscalModel, text);
		setExercise(fiscalModel, text);
		setModel(fiscalModel, text);
		setPeriod(fiscalModel, text);
		setHacienda(fiscalModel, text);
		return fiscalModel;
	}
	

}
