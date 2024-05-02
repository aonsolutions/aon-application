package com.esferalia.aon.in.payroll.pdf.mod.Gipuzkoa;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;

public class ParserUtils {

	public boolean haciendaSearch(String text) {
		boolean result = false;
		ArrayList<String> provinces = new ArrayList<>();

		provinces.add("GIPUZKOA");
		provinces.add("gipuzkoa");

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
		models.add("Finanzas 115");
		models.add("180");
		models.add("190");
		models.add("300");
		models.add("349");
		models.add("390");
		models.add("130");
		models.add("200");

		for (int i = 0; i < models.size(); i++) {
			String modelRegex = models.get(i);
			Pattern pattern = Pattern.compile(modelRegex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);

			if (matcher.find()) {
				possibleModel = matcher.group();
				if (possibleModel.contains("115")) {
					possibleModel = "115";
					break;
				}
				for (FiscalModelType modelos : FiscalModelType.values()) {
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
	
	
	public String parsePeriodGipuzkoa(String period) {
		String truePeriod = "";
		String [] auxPeriod = null;
		String trimestre = "Trimestre";
		period = period.replace("er", "");
		if (period.contains(trimestre) && period.contains("º")) {
			auxPeriod = period.split(trimestre);
			truePeriod = auxPeriod[0].replace("º", "T");
		}else if(period.contains(trimestre)) {
			auxPeriod = period.split(trimestre);
			truePeriod = auxPeriod[0]+"T";
		}
		System.out.println(truePeriod );
		return truePeriod;
		
		
	}

}
