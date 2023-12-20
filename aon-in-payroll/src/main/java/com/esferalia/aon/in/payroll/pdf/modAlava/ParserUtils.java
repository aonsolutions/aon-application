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
	
	public void listPeriods(String period) {
		
	}
	
	public  String obtenerTrimestre(String dato) {
        // Dividir el rango de fechas en dos partes: añoInicio y mesInicio
		  if (dato.contains("-")) {
	            // Procesar rango de fechas
	            String[] partes = dato.split("-");
	            String mesInicio = partes[1].substring(4); // Obtener los últimos dos dígitos del segundo año

	            int mesNumero = Integer.parseInt(mesInicio);
	            int trimestre = (mesNumero - 1) / 3 + 1;

	            String[] nombresTrimestres = {"1º Trimestre", "2º Trimestre", "3º Trimestre", "4º Trimestre"};
	            String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

	            String nombreTrimestre = nombresTrimestres[trimestre - 1];
	            String nombreMes = nombresMeses[mesNumero - 1];

	            return  nombreTrimestre;
	        } else if (dato.startsWith("T")) {
	            // Procesar dato de trimestre (T1, T2, T3, T4)
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
	

    // Método para obtener el número del mes a partir de su nombre
    public  int obtenerMesNumero(String nombreMes) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyyMM");
        LocalDate fecha = LocalDate.parse("2023" + nombreMes, formato);
        return fecha.getMonthValue();
    }

	
}
