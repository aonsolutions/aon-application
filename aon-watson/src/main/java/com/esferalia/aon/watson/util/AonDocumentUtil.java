package com.esferalia.aon.watson.util;

public class AonDocumentUtil {
	
	private static final char[] DNI_LETTERS = { 'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D',
		'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E' };
	private static final char[] NIF_LETTERS = { 'J', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I' };
	
	public static boolean isValid(String value) {
		if (value == null || value.length() == 0) {
			return false;
		}
		char[] doc = value.toCharArray();
		if (doc.length == 9) {
			String first = new String(doc,0,1);
			if (first.matches("[0-9|K|L|M]")) {
				return isValidNIF(doc);
			}
			if (first.matches("[X|Y|Z]")) {
				return isValidNIE(doc);
			}
			return isValidCIF(doc);
		}
		return false;
	}
	
	public static boolean isValidNIE(char[] doc) {
		if (doc.length != 9) {
			return false;
		}
		doc[0] = (doc[0] == 'X') ? '0' : doc[0];
		doc[0] = (doc[0] == 'Y') ? '1' : doc[0];
		doc[0] = (doc[0] == 'Z') ? '2' : doc[0];
		String numbers = new String(doc, 0, 8);
		if (!isNumeric(numbers)) {
			return false;
		}
		return (doc[8] == DNI_LETTERS[(Integer.parseInt(numbers) % 23)]);
	}

	public static boolean isValidNIF(char[] doc) {
		if (doc.length != 9) {
			return false;
		}
		doc[0] = (doc[0] == 'K' || doc[0] == 'L' || doc[0] == 'M') ? '0' : doc[0];
		String numbers = new String(doc, 0, 8);
		if (!isNumeric(numbers)) {
			return false;
		}
		return (doc[8] == DNI_LETTERS[(Integer.parseInt(numbers) % 23)]);
	}

	public static boolean isValidCIF(char[] doc) {
		if (doc.length != 9) {
			return false;
		}
		int lInDC = 0;
		for (int i = 1; i < 8; ++i) {
			String strDigit = new String(doc, i, 1);
			if (!isNumeric(strDigit)) {
				return false;
			}
			int digit = Integer.parseInt(strDigit);
			if ((i % 2) != 0) {
				digit *= 2;
				if (digit >= 10) {
					digit -= 9;
				}
			}
			lInDC += digit;
		}
		// Buscamos el multiplo de diez mas cercano mayor al numero calculado.
		lInDC = (((lInDC / 10) + 1) * 10) - lInDC;
		if (lInDC == 10) {
			lInDC = 0;
		}
		String first = new String(doc,0,1);
		if (first.matches("[P|N|S|Q|R|W]")) {
			return (NIF_LETTERS[lInDC] == doc[8]);
		}
		String strDC = new String(doc, 8, 1);
		if (!isNumeric(strDC)) {
			return false;
		}
		return (Integer.parseInt(strDC) == lInDC);
	}
	
	
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEntity(String doc) {
		if (doc == null || doc.length() != 9) {
			return false;
		}
    	return (doc.matches("^(A|B|C|D|E|F|G|H|J|P|Q|R|S|U|V|N|W).{8}"));
    }

    public static boolean isCulturalAssociation(String doc) {
		if (doc == null || doc.length() != 9) {
			return false;
		}
    	return (doc.matches("^(G).{8}"));
    }
}

