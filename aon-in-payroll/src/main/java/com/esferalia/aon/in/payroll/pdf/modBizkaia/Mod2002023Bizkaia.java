package com.esferalia.aon.in.payroll.pdf.modBizkaia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.modBizkaia.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;

public class Mod2002023Bizkaia implements IModelDocumentParser{

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
	
	
	private void setDeclarantNif(FiscalModel fm, String text) {
		String declarantNif="";
		String declarantNifRegex = "IFZ/NIF Izendura edo sozietatearen izena/Denominación o razón socialAitortzailea/ Declarante\\s"+nifRegex;
		
		Pattern pattern = Pattern.compile(declarantNifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			declarantNif = matcher.group(1).trim();
		}
		
		fm.setDocument(declarantNif);
	}
	
	private void setDeclarantName(FiscalModel fm, String text) {
		String declarantName="";
		String declarantNameRegex = "IFZ/NIF Izendura edo sozietatearen izena/Denominación o razón socialAitortzailea/ Declarante\\s"+nifRegex+"(\\s.+)";
		
		Pattern pattern = Pattern.compile(declarantNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			declarantName = matcher.group(3).trim();
		}
		
		fm.setName(declarantName);
	}
	
	private void setAmount(FiscalModel fm, String text) {
		String amount = "";
		String amountRegex = "Emaitza/ Itzuli beharrekoa/A devolver Sartu beharrekoa/A ingresar\\s.*([0-9].[0-9]{2,}.*[0-9])";
		
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			amount = matcher.group(1).trim();
		}
		
		double total = Double.parseDouble(amount.replace(",", "."));
		fm.setDeclarationResult(total);
				
	}
	
	private void setIban(FiscalModel fm, String text) {
		String iban = "";
		String ibanRegex = "Banku helbideraketa/Datos domiciliación bancaria\\s([A-Z]{2}[0-9]{2}).*([0-9]{4}).*([0-9]{4}).*([0-9]{4}).*([0-9]{4}).*([0-9]{4})";
		
		Pattern pattern = Pattern.compile(ibanRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			iban =  matcher.group(1).trim() +" " +
					matcher.group(2).trim() + " " +
					matcher.group(3).trim() + " " +
					matcher.group(4).trim() + " " +
					matcher.group(5).trim() + " " +
					matcher.group(6).trim();
		}
		fm.setIban(iban);
	}
	
//	private String setPrincipalActivity(String text) {
//		String principalActivity = "";
//		String principalActivityRegex = "Actividad principal:(\\s.+)([0-9])(\\s.*)";
//		
//		Pattern pattern = Pattern.compile(principalActivityRegex, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		
//		if (matcher.find()) {
//			principalActivity = matcher.group(3).trim();
//		}
//		
//		return principalActivity;
//	}
	
	private void setModel(FiscalModel fm, String text) {
		String model = "";
		String modelRegex = "200";
		
		Pattern pattern = Pattern.compile(modelRegex);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			model = matcher.group().trim();
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
		setDeclarantNif(fiscalModel, text);
		setDeclarantName(fiscalModel, text);
		setIban(fiscalModel, text);
		setAmount(fiscalModel, text);
		setModel(fiscalModel, text);
		return fiscalModel;
	}
}
