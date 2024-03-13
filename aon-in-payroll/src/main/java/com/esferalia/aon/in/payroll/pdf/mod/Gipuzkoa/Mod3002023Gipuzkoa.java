package com.esferalia.aon.in.payroll.pdf.mod.Gipuzkoa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.mod.Gipuzkoa.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod3002023Gipuzkoa implements IModelDocumentParser{
	
	String nifRegex = "("
			//  -------- LEGAL_PERSON_NIF PATTERN  
			// -------- (1) --> X00000000
				+"[A-JUV]"
				+"[\\s]*"
				+"[-_/]?"
				+"[\\s]*"
				+"[0-9]{2}"
				+"[-_/\\.]?"
				+"[0-9]{3}"
				+"[-_/\\.]?"
				+"[0-9]{3}"
			//  -------- LEGAL_PERSON_NIF PATTERN 
			// -------- (2) --> X0000000X
			+"|"
				+"[NPQRSW]"
				+"[\\s-_/]?"
				+"[0-9]{7}"
				+"[\\s-_/]?"
				+"([A-J])"
			//  -------- DNI PATTERN 
			// -------- (1) --> 00000000X
			+"|"
				+"[0-9]?"
				+"[0-9]"
				+"[\\s-_/\\.]?"
				+"[0-9]{3}"
				+"[\\s-_/\\.]?"
				+"[0-9]{3}"
				+"[\\s-_/]?"
				+"[A-Z]"
			//  -------- NIE PATTERN 
			// -------- (1) --> X0000000X
			+"|"
				+"[XYZ]"
				+"[\\s-_/]?"
				+"[0-9]{7}"
				+"[\\s-_/]?"
				+"[A-HJ-NP-TV-Z]"
			+")"
			;
	
	
	private void setNif(FiscalModel fm, String text) {
		String nif = "";
		String nifRegexx = "Apellidos y nombre o razón social AnagramaDNI . NIF\\s"+nifRegex;
		
		
		Pattern pattern = Pattern.compile(nifRegexx);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			nif = matcher.group(1).trim();
		}
		
		fm.setDocument(nif);
	} 
	
	private void setName(FiscalModel fm, String text) {
		String name = "";
		String nameRegex = "Apellidos y nombre o razón social AnagramaDNI . NIF\\s"+nifRegex+"(\\s.*)";
		
		Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		
		fm.setName(name);
	}
	
	private void setPeriod(FiscalModel fm, String text) {
		String period = "";
		String periodRegex = "Período:(\\s[0-9]{1})";
		ParserUtils pu = new ParserUtils();

		Pattern pattern = Pattern.compile(periodRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if ( matcher.find()) {
			period = matcher.group(1).trim() + " Trimestre";
			System.out.println(period);
			period = pu.parsePeriodGipuzkoa(period);

		}
		fm.setPeriod(Period.safeValueOf(period.replace(" ", "")));

	}
	
	private void setExercise(FiscalModel fm, String text) {
		String exercise = "";
		String exerciseRegex = "Ejercicio:\\s([0-9]{2,})";
		
		Pattern pattern = Pattern.compile(exerciseRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			exercise = matcher.group(1).trim();
		}
		int year = Integer.parseInt(exercise);
		fm.setYear(year);
	}
	
//	public String setAccruedFee(String text) {
//		String accruedFee = "";
//		String accruedFeeRegex = "SORTUTAKO KUOTA, GUZTIRA / TOTAL CUOTA DEVENGADA\\s..\\s([0-9]{1,},[0-9]{2})";
//		
//		Pattern pattern = Pattern.compile(accruedFeeRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			accruedFee = matcher.group(1).trim();
//		}
//		
//		return accruedFee;
//	}
//	
//	//NOT FINISH
//	public String setDeduct(String text) {
//		String deduct ="";
//		String deductRegex = "KENDU BEHARREKOA, GUZTIRA / TOTAL A DEDUCIR\\s.*\\s.*([0-9]{1,}(.)[0-9]{2,})DIFERENTZIA / DIFERENCIA ";
//		Pattern pattern = Pattern.compile(deductRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			deduct = matcher.group(1).trim();
//		}
//		
//		return deduct;
//	}
	
	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex ="([0-9]{1,}.[0-9]{2,})EMAITZA / RESULTADO";
		
		Pattern pattern = Pattern.compile(amountRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		double total = Double.parseDouble(amount.replace(",", "."));
		fm.setDeclarationResult(total);
		
	}
	
	public void setHacienda(FiscalModel fm , String text) {
		ParserUtils pu = new ParserUtils();
		if (pu.haciendaSearch(text)) {
			fm.setAdministration(Administration.GIPUZKOA);
		}
	}
//	private String setIssueDate(String text) {
//		String issueDate = "";
//		String issueDateRegex ="Fecha de presentación:\\s([0-9]{1,}.[0-9]{1,}.[0-9]{2,})";
//		
//		Pattern pattern = Pattern.compile(issueDateRegex);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			issueDate = matcher.group(1).trim();
//		}
//		
//		return issueDate;
//	}

	@Override
	public boolean accept(String text) {
		return true;
	}

	@Override
	public FiscalModel parse(String text) {
		FiscalModel fiscalModel = new FiscalModel();
		setNif(fiscalModel, text);
		setName(fiscalModel, text);
		setExercise(fiscalModel, text);
		setAmount(fiscalModel, text);
		setPeriod(fiscalModel, text);
		setHacienda(fiscalModel, text);
		return fiscalModel;
	}
	
	
}
