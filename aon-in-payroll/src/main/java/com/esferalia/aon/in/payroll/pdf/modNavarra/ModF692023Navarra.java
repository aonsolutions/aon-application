package com.esferalia.aon.in.payroll.pdf.modNavarra;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModF692023Navarra {
	
	
	public String setSocialReasonName(String text) {
		String name = "";
		String nameSocialReasonRegex = " Nombre o razón social.+\\s+([^0-9])([0-9]+)([^\\n]+)";

 		Pattern pattern = Pattern.compile(nameSocialReasonRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(3).trim();
		}
		return name;
	}
	

	public String setNif(String text) {
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
		return nif;
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
		String phoneNumberRegex = "[^a-z][\\d]{9}";
		
		Pattern pattern = Pattern.compile(phoneNumberRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			phoneNumber = matcher.group().trim();
		}
		
		return phoneNumber;
	}
	
	public String setPeriodAndYear(String text) {
		String period = "";
		String year = "";
		String yearAndPeriod ="";
		String periodYearRegex ="Periodo+\\s+([0-9]{4})\\s+([A-Z]{1}[0-9]{1})";
		Pattern pattern = Pattern.compile(periodYearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			year = matcher.group(1).trim();
			period = matcher.group(2).trim();
			
			yearAndPeriod = year +" "+period;
			System.out.println("year : " +year);
			System.out.println("period : " +period);
			
		}
		return yearAndPeriod;
	}
	
	public String setAmount(String text) {
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
			System.out.println("Pago : " + amount);
		}else {
			matcher = pattern2.matcher(text);
			if (matcher.find()) {
				amount = matcher.group(1).trim();
				System.out.println("Pago : " + amount);
			}else {
				matcher = pattern3.matcher(text);
				if (matcher.find()) {
					amount = matcher.group(1).trim();
					System.out.println("Pago : " + amount);
				}
			}
		
		}
		return amount;
	}
	
	public String setHacienda(String text) {
		ParserUtils pu = new ParserUtils();
		String hacienda ="";
		if (pu.haciendaSearch(text)) {
			hacienda = "Hacienda Navarra";
		}
		System.out.println(hacienda);
		return hacienda;
	}
	
	public String setModel(String text) {
		String model = "";
		String modelRegex = "F69";
		
		Pattern pattern = Pattern.compile(modelRegex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			model = matcher.group().trim();
		}
		return model;
	}
}
