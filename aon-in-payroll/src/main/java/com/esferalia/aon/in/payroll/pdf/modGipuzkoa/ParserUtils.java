package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

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
	
	
	public void modelSearch(String text) {
		ArrayList<String> models = new ArrayList<>();
		models.add("115");
		models.add("115A");
		models.add("130");
		models.add("715");
		models.add("F69");
				
		for (int i = 0; i < models.size(); i++) {
			String modelRegex = models.get(i);
			Pattern pattern = Pattern.compile(modelRegex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			
			if (matcher.find()) {
				String possibleModel = matcher.group();
				for(FiscalModelType modelos : FiscalModelType.values()) {
					if (modelos.getName().equals(possibleModel)) {
						System.out.println("MODELO : " + possibleModel);
						break;
					}
				}
			}
			
		}
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
	

	
}
