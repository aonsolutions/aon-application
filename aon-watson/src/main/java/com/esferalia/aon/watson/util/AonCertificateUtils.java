package com.esferalia.aon.watson.util;

public class AonCertificateUtils {
	
    /**
     * Extrae la password de la descripción del Certificado Dígital
     */
	public static String getCertificatePassword(String description) {
		try {
			return description.split("HIDE\\(")[1].substring(0, description.split("HIDE\\(")[1].length() - 1);
		} catch (Exception e) {
			return "";
		}
	}

}
