package com.esferalia.aon.in.payroll.pdf.mod.Navarra;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;

public class ParserUtils {

	
	
	public boolean haciendaSearch(String text) {
		boolean result = false;
		ArrayList<String> provinces = new ArrayList<>();
		provinces.add("NAVARRA");
		provinces.add("navarra");

		
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
		models.add("130");
		models.add("715");
		models.add("F69");
				
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
	
	public  String obtenerTrimestre(String dato) {
		  if (dato.contains("-")) {
	            String[] partes = dato.split("-");
	            String mesInicio = partes[1].substring(4); 

	            int mesNumero = Integer.parseInt(mesInicio);
	            int trimestre = (mesNumero - 1) / 3 + 1;

	            String[] nombresTrimestres = {"1º Trimestre", "2º Trimestre", "3º Trimestre", "4º Trimestre"};
	            String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

	            String nombreTrimestre = nombresTrimestres[trimestre - 1];
	            String nombreMes = nombresMeses[mesNumero - 1];

	            return nombreMes + "\n" + nombreTrimestre;
	        } else if (dato.startsWith("T")) {

	        	int trimestre;
	            try {
	                trimestre = Integer.parseInt(dato.substring(1));
	            } catch (NumberFormatException e) {
	                return "Formato de trimestre inválido";
	            }

	            if (trimestre < 1 || trimestre > 4) {
	                return "Trimestre fuera de rango (debe ser T1, T2, T3 o T4)";
	            }

	            String[] nombresTrimestres = {"1º Trimestre", "2º Trimestre", "3º Trimestre", "4º Trimestre"};
	            String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

	            int mesInicio = (trimestre - 1) * 3;
	            int mesFin = mesInicio + 2;

	            String nombreTrimestre = nombresTrimestres[trimestre - 1];
	            StringBuilder resultado = new StringBuilder();

	            for (int i = mesInicio; i <= mesFin; i++) {
	                resultado.append(nombresMeses[i]);
	                if (i < mesFin) {
	                    resultado.append("\n");
	                }
	            }
	            return resultado.toString() + "\n" + nombreTrimestre;
	        } else {
	            return "Formato no reconocido";
	        }
	    }
	
	
	public String parsePeriodNavarra(String period) {
		String truePeriod = "";
		
		if(period.equals("T1")) {
			truePeriod = "1T";
		}else if(period.equals("T2")) {
			truePeriod = "2T";
		}else if(period.equals("T3")) {
			truePeriod = "3T";
		}else if(period.equals("T4")) {
			truePeriod = "4T";
		}
		
		return truePeriod;
	}
	

	
}
