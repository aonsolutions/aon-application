package com.esferalia.aon.in.payroll.pdf.mod.Bizkaia;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;

public class ParserUtils {

	
	
	public boolean haciendaSearch(String text) {
		boolean result = false;
		ArrayList<String> provinces = new ArrayList<>();
		provinces.add("Sello electrónico de la Hacienda Foral");
		provinces.add("Foru Ogasunaren zigilu elektronikoa / Sello electrónico de la Hacienda Foral");

		for (int i = 0; i < provinces.size(); i++) {
			String haciendaRegex = provinces.get(i);
			Pattern pattern = Pattern.compile(haciendaRegex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				result = true;
			}
		}
		
		return result;
	}
	
	
	public String modelSearch(String text) {
		ArrayList<String> models = new ArrayList<>();
		String possibleModel = "";
		models.add("110");
		models.add("115");
		models.add("180");
		models.add("190");
		models.add("200");
		models.add("303");
		models.add("349");
		models.add("390");
				
		for (int i = 0; i < models.size(); i++) {
			String modelRegex = models.get(i);
			Pattern pattern = Pattern.compile(modelRegex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			
			if (matcher.find()) {
				possibleModel = matcher.group();
				for(FiscalModelType modelos : FiscalModelType.values()) {
					if (modelos.getName().equals(possibleModel)) {
						break;
					}
				}
			}	
		}
		return possibleModel;
	}
	
	public boolean validateDocument(String document) {
		boolean result = false;
	    String nifRegex = "[\\d]{8}[A-Z]";
	    String nieRegex = "[A-Z][\\d]{7}[TRWAGMYFPDXBNJZSQVHLCKE]";
	    String cifRegex = "[A-Z][\\d]{7,}.?[\\d]?[A-Z]?";

	    if (document.matches(nifRegex)) {
	        System.out.println("es NIF");
	        result = true;
	    } else if (document.matches(nieRegex)) {
	        System.out.println("es NIE");
	        result = true;
	    } else if (document.matches(cifRegex)) {
	        System.out.println("es CIF");
	        result = true;
	    } else {
	        System.out.println("no es ninguno");
	    }
		return result;

	}
	
	public String parsePeriodBizkaia(String period) {
		String truePeriod = "";
	
		if (period.contains("TRIM") && period.contains("1")) {
			truePeriod = "1T";
		}else if(period.contains("TRIM") && period.contains("2")) {
			truePeriod = "2T";
		}else if(period.contains("TRIM") && period.contains("3")) {
			truePeriod = "3T";
		}else if(period.contains("TRIM") && period.contains("4")) {
			truePeriod = "4T";
		}else if(period.contains("Anual") || period.contains("ANUAL")  ) {
			truePeriod = "An";
		}else if(period.contains("EN")) {
			truePeriod = "01";
		}else if(period.contains("FEB")) {
			truePeriod = "02";
		}else if(period.contains("MAR")) {
			truePeriod = "03";
		}else if(period.contains("AB")) {
			truePeriod = "04";
		}else if(period.contains("MAY")) {
			truePeriod = "05";
		}else if(period.contains("JUN")) {
			truePeriod = "06";
		}else if(period.contains("JUL")) {
			truePeriod = "07";
		}else if(period.contains("AG")) {
			truePeriod = "08";
		}else if(period.contains("SEP")) {
			truePeriod = "09";
		}else if(period.contains("OCT")) {
			truePeriod = "10";
		}else if(period.contains("NOV")) {
			truePeriod = "11";
		}else if(period.contains("DIC")) {
			truePeriod = "12";
		}
		return truePeriod;
	}
	

	
}
