package com.esferalia.aon.in.payroll.pdf.modNavarra;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.modNavarra.ModelDocumentParsers.IModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.type.Period;

public class ModF692023Navarra implements IModelDocumentParser {
	
	
	public void setSocialReasonName(FiscalModel fm, String text) {
		String name = "";
		String nameSocialReasonRegex = " Nombre o razón social.+\\s+([^0-9])([0-9]+)([^\\n]+)";

 		Pattern pattern = Pattern.compile(nameSocialReasonRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		fm.setName(name);
	}
	

	public void setNif(FiscalModel fm, String text) {
		String nif = "";
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
		Pattern pattern = Pattern.compile(nifRegex , Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			nif = matcher.group().trim();
		}
			fm.setDocument(nif);
	}
	
	public void setEmail(FiscalModel fm, String text) {
		String email = "";
        String emailRegex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
		
		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			email = matcher.group().trim();
		}
		
		fm.setContactEmail(email);
	}
	
	public void setPhoneNumber(FiscalModel fm, String text) {
		String phoneNumber = "";
		String phoneNumberRegex = "[^a-z][\\d]{9}";
		
		Pattern pattern = Pattern.compile(phoneNumberRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			phoneNumber = matcher.group().trim();
		}
		
		fm.setContactPhone(phoneNumber);
	}
	
	public void setPeriodAndYear(FiscalModel fm, String text) {
		String period = "";
		String year = "";
		ParserUtils pu = new ParserUtils();
		String periodYearRegex ="Periodo+\\s+([0-9]{4})\\s+([A-Z]{1}[0-9]{1})";
		Pattern pattern = Pattern.compile(periodYearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			year = matcher.group(1).trim();
			period = matcher.group(2).trim();
			
			period= pu.parsePeriodNavarra(period);

		}
		int auxYear = Integer.parseInt(year);
		fm.setYear(auxYear);
		fm.setPeriod(Period.safeValueOf(period));
	}
	
	public void setAmount(FiscalModel fm, String text) {
		String keyWord = "Importe a ingresar";
		String keyWord2 = "Cantidad";
		String keyWord3 = "RESULTADO";

		String amount = "";
		String amountRegex = "\\b"+keyWord3+"\\s+(-?\\d{1,3}(,\\d{3})*\\.?\\d*)(,)(\\d.)";
		String amountRegex2 = ""+keyWord+"\\s.+([\\d],[\\d].)";
		String amountRegex3 = keyWord2+"\\s.*\\s.*([\\d][\\d].[,][\\d].)";
		Pattern pattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE);
		Pattern pattern2 = Pattern.compile(amountRegex2, Pattern.CASE_INSENSITIVE);
		Pattern pattern3 = Pattern.compile(amountRegex3, Pattern.CASE_INSENSITIVE);
		
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			
			String entero = matcher.group(1).trim();
			String coma = matcher.group(3).trim();
			String decimales = 	matcher.group(4).trim();
			
			amount = entero + coma + decimales;
		}else {
			matcher = pattern2.matcher(text);
			if (matcher.find()) {
				amount = matcher.group(1).trim();
			}else {
				matcher = pattern3.matcher(text);
				if (matcher.find()) {
					amount = matcher.group(1).trim();
				}
			}
		
		}
		double total = Double.parseDouble(amount.replace(",", "."));
		fm.setDeclarationResult(total);	}
	
//	public String setHacienda(String text) {
//		ParserUtils pu = new ParserUtils();
//		String hacienda ="";
//		if (pu.haciendaSearch(text)) {
//			hacienda = "Hacienda Navarra";
//		}
//		System.out.println(hacienda);
//		return hacienda;
//	}
	
	public void setModel(FiscalModel fm, String text) {
		String model = "";
		String modelRegex = "F69";
		
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
		setNif(fiscalModel, text);
		setSocialReasonName(fiscalModel, text);
		setEmail(fiscalModel, text);
		setAmount(fiscalModel, text);
		setPeriodAndYear(fiscalModel, text);
		setPhoneNumber(fiscalModel, text);
		setModel(fiscalModel, text);
		
		return fiscalModel;
	}
}
