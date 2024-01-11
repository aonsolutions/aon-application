package com.esferalia.aon.in.payroll.pdf.modAlava;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;

public class ParserUtils {

	
	
	public boolean haciendaSearch(String text) {
		boolean result = false;
		ArrayList<String> provinces = new ArrayList<>();
		provinces.add("NAVARRA");
		provinces.add("ALAVA");
		provinces.add(" Sello electrónico de la Hacienda Foral");
		provinces.add("GIPUZKOA");
//		provinces.add("AEAT");
		
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
		models.add("303");
		models.add("115");
		models.add("115A");
		models.add("130");
		models.add("715");
		models.add("F69");
		models.add("349");
	
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
	        result = true;
	    } else if (document.matches(nieRegex)) {
	        result = true;
	    } else if (document.matches(cifRegex)) {
	        result = true;
	    } 
		return result;

	}

    public  int obtenerMesNumero(String nombreMes) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyyMM");
        LocalDate fecha = LocalDate.parse("2023" + nombreMes, formato);
        return fecha.getMonthValue();
    }
    
    public String parsePeriodAlava(String period) {
		String truePeriod = "";
		String trueTruePeriod = "";
	
		String [] auxPeriod = null;
		String [] startMonth = null;
		String [] finishMonth = null;
		if (period.contains("-") && period.contains("2023")) {
			auxPeriod = period.split("-");
			startMonth = auxPeriod[0].split("2023");
			finishMonth = auxPeriod[1].split("2023");
			
	
			truePeriod = startMonth [1] +" - "+ finishMonth [1]; 
			if (truePeriod.equals("01 - 03")) {
				trueTruePeriod ="1T";
			}else if(truePeriod.equals("04 - 06")) {
				trueTruePeriod ="2T";
			}else if(truePeriod.equals("07 - 09")) {
				trueTruePeriod ="3T";
			}else if(truePeriod.equals("10 - 12")) {
				trueTruePeriod ="4T";
			}

		}else if(period.contains("-") && period.contains("2022")) {
			auxPeriod = period.split("-");
			startMonth = auxPeriod[0].split("2022");
			finishMonth = auxPeriod[1].split("2022");
			

			truePeriod = startMonth [1] +" - "+ finishMonth [1]; 
			if (truePeriod.equals("01 - 03")) {
				trueTruePeriod ="1T";
			}else if(truePeriod.equals("04 - 06")) {
				trueTruePeriod ="2T";
			}else if(truePeriod.equals("07 - 09")) {
				trueTruePeriod ="3T";
			}else if(truePeriod.equals("10 - 12")) {
				trueTruePeriod ="4T";
			}
		}
		return trueTruePeriod;
	}
    
    

	
}
