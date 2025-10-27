package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.type.Country;

public class BicSwiftValidator {

    // Valida si un código BIC/SWIFT es válido según la norma ISO 9362.
    public static boolean isValidBic(String bic) {
        if (bic == null) return false;

        bic = bic.trim().toUpperCase();

        if (bic.length() != 8 && bic.length() != 11) return false;

        // Expresión regular para BIC de 8 u 11 caracteres
        // AAAA = 4 letras (A-Z)
        // BB = 2 letras (A-Z)
        // CC = 2 alfanuméricos (A-Z0-9)
        // DDD = 3 alfanuméricos opcionales
        String regex = "^[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}([A-Z0-9]{3})?$";

        if (!bic.matches(regex)) return false;

        // Validar que el código de país sea ISO 3166-1 alpha-2
        String countryCode = bic.substring(4, 6);
        if (!isValidCountryCode(countryCode)) return false;

        return true;
    }

    private static boolean isValidCountryCode(String code) {
    	for (String country : Country.getCountriesIso2()) {
            if (country.equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }
}

