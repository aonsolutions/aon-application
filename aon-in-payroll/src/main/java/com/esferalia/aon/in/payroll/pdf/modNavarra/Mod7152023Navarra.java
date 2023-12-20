package com.esferalia.aon.in.payroll.pdf.modNavarra;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Mod7152023Navarra {

	
	public String setSocialReasonName(String text) {
		String name = "";
		String nameSocialReasonRegex = " Nombre o razón social.+\\s+([^0-9])([0-9]+)([^\\n]+)";

 		Pattern pattern = Pattern.compile(nameSocialReasonRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			name = matcher.group(3).trim();
			System.out.println("Name : " + name);
		}
		
		return name;
		
	}
	
	
	public String setRegistryNumber(String text) {
		String registry = "";
		String registryRegex = "([0-9]{5})";
		Pattern pattern = Pattern.compile(registryRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
//		System.out.println(matcher.find());
		if (matcher.find()) {
			registry = matcher.group().trim();
			System.out.println("Registry number : " + registry);
		}
		
		return registry;
	}
	
	
	//Implementar patron para cualquier nif nie dni 
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
		Pattern pattern = Pattern.compile(nifRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			nif = matcher.group().trim();
			System.out.println("NIF : " + nif);
		}
		
		return nif;
	}
	
	public String setEmail(String text) {
		String email = "";
//		String emailRegex = ".*[A-Z]@[A-Za-z0-9.-].*[A-Z]";
        String emailRegex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
		
		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			email = matcher.group().trim();
			System.out.println("Email : " + email);
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
			System.out.println("Phone number : " + phoneNumber);
		}
		
		return phoneNumber;
	}
	
	public String setIBAN (String text) {
		String iban = "";
		//Cambiar ES por for que recorra nacionalidades
		String IBANRegex = "ES+([^A-Z]{26})";
		
		Pattern pattern = Pattern.compile(IBANRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			iban = matcher.group().trim();
			System.out.println("IBAN : " + iban);
		}
		
		return iban;
	}
	
	public String setPeriodAndYear(String text) {
		String period = "";
		String year = "";
		String periodYearRegex ="Periodo+\\s+([0-9]{4})\\s+([A-Z]{1}[0-9]{1})";
		Pattern pattern = Pattern.compile(periodYearRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			year = matcher.group(1).trim();
			period = matcher.group(2).trim();
			
			System.out.println("year : " +year);
			System.out.println("period : " +period);
			
		}
		
		return period + " " + year;
	}
	
	public String setIssueDate(String text) {
		String issueDay = "";
		String issueMonth = "";
		String issueYear = "";
		String issueDateRegex = "Fecha presentación:\\s+([0-9]{2})(/)([0-9]{2})(/)([0-9]{4})";
		Pattern pattern = Pattern.compile(issueDateRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
//		System.out.println(matcher.find());
		if (matcher.find()) {
			issueDay= matcher.group(1).trim();
			issueMonth = matcher.group(3).trim();
			issueYear = matcher.group(5).trim();
			System.out.println("Issue date : " + issueDay +"/"+ issueMonth +"/"+ issueYear);
		}
		
		return issueDay +"/"+ issueMonth +"/"+ issueYear;
		
	}
	
	public String setSign(String text) {
		String sign = "";
		String signRegex = "Firma\\s+([^\\n]+)";
		Pattern pattern = Pattern.compile(signRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			sign = matcher.group(1).trim();
			System.out.println("Signature : " +sign);
		}
		
		return sign;
	}
	
	public String setCsv (String text) {
		String csv = "";
		String csvRegex = "CSV:\\s+([^\\n]+)";
		Pattern pattern = Pattern.compile(csvRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			csv = matcher.group(1).trim();
			System.out.println("CSV : " + csv);
		}
		
		return csv;
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
	
	public void setModel(String text) {
		ParserUtils pu = new ParserUtils();
		pu.modelSearch(text); 
	}
	
	public void parser(String text) {
		System.out.println("MODELO NAVARRA");
		setSocialReasonName(text);
		setNif(text);
		setEmail(text);
		setPeriodAndYear(text);
		setPhoneNumber(text);
		setRegistryNumber(text);
		setIBAN(text);
		setHacienda(text);
		setCsv(text);
		setSign(text);
		setIssueDate(text);
		setAmount(text);
		setModel(text);
	}
	
	
}
