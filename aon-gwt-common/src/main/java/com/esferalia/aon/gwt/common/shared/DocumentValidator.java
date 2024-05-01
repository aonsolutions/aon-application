package com.esferalia.aon.gwt.common.shared;

import com.esferalia.aon.occam.api.model.type.DocumentType;

public class DocumentValidator {
	
	public static DocumentType validateDocument(String document) {
        if (isValidDNI(document)) {
            return DocumentType.NIF;
        } else if (isValidNIE(document)) {
        	 return DocumentType.NIE;
        } else if (isValidCIF(document)) {
        	 return DocumentType.CIF;
        } else {
        	 return DocumentType.OTHER;
        }
    }

    private static boolean isValidDNI(String dni) {
        // DNI is a 8-digit number followed by a letter
        String pattern = "\\d{8}[A-Z]";
        return dni.matches(pattern);

    }

    private static boolean isValidNIE(String nie) {
        // NIE is a letter followed by 7 digits and a letter
        String pattern = "[A-Z]\\d{7}[A-Z]";
        return nie.matches(pattern);

    }

    private static boolean isValidCIF(String cif) {
        // CIF is a letter followed by 8 digits
        String pattern = "[A-Z]\\d{8}";
        return cif.matches(pattern);
    }
}
