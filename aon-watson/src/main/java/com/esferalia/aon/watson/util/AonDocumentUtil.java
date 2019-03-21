package com.esferalia.aon.watson.util;

public class AonDocumentUtil {
	
	private static final char[] DNI_LETTERS = { 'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D',
		'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E' };
	private static final char[] NIF_LETTERS = { 'J', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I' };
	
	public static boolean isValid(Byte documentType, String country, String document) {
		// 0 --> DNI, 1 --> CIF, 2 --> NIE, > 3 --- ???
		if (documentType == null || country == null || document == null) return false;
		if (documentType < 0 || documentType > 2) return false;
		if (!"ES".equals( country )) {
			return isValidComunitaryCode(country,document);
		} else {
			char[] doc = document.toCharArray();
			if (documentType == 0) return isValidNIF(doc);
			if (documentType == 1) return isValidCIF(doc);
			if (documentType == 2) return isValidNIE(doc);
		}
		return false;
	}
	
	public static boolean isValidable(Byte documentType, String country, String document) {
		if (documentType == null || country == null || document == null) return false;
		return (isValidComunitaryCountry(country) 
			|| ("ES".equals( country ) && documentType < 3));
	}
	
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

	public static boolean isValidComunitaryCountry(String country){
		return ("AT".equals(country) || "BE".equals(country) 
			 || "BG".equals(country) || "CY".equals(country) 
			 || "CZ".equals(country) || "DE".equals(country) 
			 || "DK".equals(country) || "EE".equals(country)
			 || "EL".equals(country) || "GR".equals(country) 
			 || "FI".equals(country) || "FR".equals(country) 
			 || "GB".equals(country) || "HR".equals(country) 
			 || "HU".equals(country) || "IE".equals(country) 
			 || "IT".equals(country) || "LT".equals(country) 
			 || "LU".equals(country) || "LV".equals(country) 
			 || "MT".equals(country) || "NL".equals(country) 
			 || "PL".equals(country) || "PT".equals(country) 
			 || "RO".equals(country) || "SE".equals(country)
			 || "SI".equals(country) || "SK".equals(country));
	}

	public static boolean isValidComunitaryCode(String country , String doc){
		if (!isValidComunitaryCountry(country)) return false;
		int len = doc.length();
		if (
			// Fuente:  http://ec.europa.eu/taxation_customs/vies/faq.html
				
			//AT-Austria 	ATU99999999 	Un bloque de 9 caracteres
			    ( "AT".equals(country) && len==9 ) 
		    //BE-Bélgica 	BE0999999999 	Un bloque de 10 cifras
			||  ( "BE".equals(country) && AonStringUtils.isNumeric(doc) && len==10) 
			//BG-Bulgaria 	BG999999999 o BG9999999999 	Un bloque de 9 cifras o un bloque de 10 cifras
			||  ( "BG".equals(country) && AonStringUtils.isNumeric(doc) && (len==9 || len==10)) 
			//CY-Chipre 	CY99999999L 	Un bloque de 9 caracteres
			||  ( "CY".equals(country) && len==9 ) 
			//CZ-Chequia 	CZ99999999 o CZ999999999 o CZ9999999999 Un bloque de 8, 9 o 10 cifras
			||  ( "CZ".equals(country) && AonStringUtils.isNumeric(doc) && (len==8 || len == 9 || len == 10 ))
			//DE-Alemania 	DE999999999 	Un bloque de 9 cifras
			||	( "DE".equals(country) && AonStringUtils.isNumeric(doc) && len==9 ) 
			//DK-Dinamarca 	DK99 99 99 99 	Cuatro bloques de 2 cifras
			||  ( "DK".equals(country) && AonStringUtils.isNumeric(doc) && len==8 )
			//EE-Estonia 	EE999999999 	Un bloque de 9 cifras
			||  ( "EE".equals(country) && AonStringUtils.isNumeric(doc) && len==9 )
			// GRECIA EL y GR
			//EL-Grecia 	EL999999999 	Un bloque de 9 cifras
			||  ( "EL".equals(country) && AonStringUtils.isNumeric(doc) && len==9 ) 
			||  ( "GR".equals(country) && AonStringUtils.isNumeric(doc) && len==9 ) 
			// --------------
			//FI-Finlandia 	FI99999999 	Un bloque de 8 cifras
			||  ( "FI".equals(country) && AonStringUtils.isNumeric(doc) && len==8 ) 
			//FR-Francia 	FRXX 999999999 	Un bloque de 2 caracteres y un bloque de 9 cifras
			||  ( "FR".equals(country) && AonStringUtils.isAlphanumeric(doc) && len==11 ) 
			//GB-Reino Unido 	GB999 9999 99 o GB999 9999 99 9995 o GBGD9996 o GBHA9997 	
			// Un bloque de 3 cifras, un bloque de 4 cifras y un bloque de 2 cifras; o lo mismo 
			// seguido de un bloque de 3 cifras; o un bloque de 5 caracteres
			||  ( "GB".equals(country) && (len==5 || len == 9 || len == 12) ) 
			//HR-Croacia 	HR99999999999 	Un bloque de 11 cifras
			||  ( "HR".equals(country) && AonStringUtils.isNumeric(doc) && len==11 ) 
			//HU-Hungría 	HU99999999 	Un bloque de 8 cifras
			||  ( "HU".equals(country) && AonStringUtils.isNumeric(doc) && len==8 ) 
			//IE-Irlanda 	IE9S99999L IE9999999WI 	Un bloque de 8 caracteres o un bloque de 9 caracteres
			||  ( "IE".equals(country) && (len==8 || len==9) ) 
			//IT-Italia 	IT99999999999 	Un bloque de 11 cifras
			||  ( "IT".equals(country) && AonStringUtils.isNumeric(doc) && len==11 ) 
			//LT-Lituania 	LT999999999 o LT999999999999 	Un bloque de 9 cifras o un bloque de 12 cifras
			||  ( "LT".equals(country) && AonStringUtils.isNumeric(doc) && (len==9 || len == 12) ) 
			//LU-Luxemburgo 	LU99999999 	Un bloque de 8 cifras
			||  ( "LU".equals(country) && AonStringUtils.isNumeric(doc) && len==8 ) 
			//LV-Letonia 	LV99999999999 	Un bloque de 11 cifras
			||  ( "LV".equals(country) && AonStringUtils.isNumeric(doc) && len==11 ) 
			//MT-Malta 	MT99999999 	Un bloque de 8 cifras
			||  ( "MT".equals(country) && AonStringUtils.isNumeric(doc) && len==8 ) 
			//NL-Países Bajos 	NL999999999B998 	Un bloque de 12 caracteres
			||  ( "NL".equals(country) && len==12 ) 
			//PL-Polonia 	PL9999999999 	Un bloque de 10 cifras
			||  ( "PL".equals(country) && AonStringUtils.isNumeric(doc) && len==10 ) 
			//PT-Portugal 	PT999999999 	Un bloque de 9 cifras
			||  ( "PT".equals(country) && AonStringUtils.isNumeric(doc) && len==9 ) 
			//RO-Rumania 	RO999999999 	Un bloque de mínimo 2 cifras y máximo 10 cifras
			||  ( "RO".equals(country) && AonStringUtils.isNumeric(doc) && (len>=2 && len<=10)) 
			//SE-Suecia 	SE999999999999 	Un bloque de 12 cifras
			||  ( "SE".equals(country) && AonStringUtils.isNumeric(doc) && len==12)
			//SI-Eslovenia 	SI99999999 	Un bloque de 8 cifras
			||  ( "SI".equals(country) && AonStringUtils.isNumeric(doc) && len==8)
			//SK-Eslovaquia 	SK9999999999 	Un bloque de 10 cifras
			||  ( "SK".equals(country) && AonStringUtils.isNumeric(doc) && len == 10) 
			) return true;
		return false;
	}
	
	/*
	LINK --> https://www.agenciatributaria.es/AEAT.internet/Inicio/La_Agencia_Tributaria/Campanas/Censos__NIF_y_domicilio_fiscal/Empresas_y_profesionales__Declaracion_censal__Modelos_036_y_037/Informacion/NIF_de_personas_juridicas_y_entidades.shtml
	Claves sobre la forma jurídica de entidades españolas
	Para las entidades españolas, el número de identificación fiscal comenzará con una letra, que 
	incluirá información sobre su forma jurídica de acuerdo con las siguientes claves:

	A. Sociedades anónimas
	B. Sociedades de responsabilidad limitada
	C. Sociedades colectivas
	D. Sociedades comanditarias
	E. Comunidades de bienes, herencias yacentes y demás entidades carentes de personalidad jurídica no incluidas expresamente en otras claves
	F. Sociedades cooperativas
	G. Asociaciones
	H. Comunidades de propietarios en régimen de propiedad horizontal
	J. Sociedades civiles
	P. Corporaciones Locales
	Q. Organismos públicos
	R. Congregaciones e instituciones religiosas
	S. Órganos de la Administración del Estado y de las Comunidades Autónomas
	U. Uniones Temporales de Empresas
	V. Otros tipos no definidos en el resto de claves

	Clave de entidades extranjeras
	N. Entidades extranjeras
	
	Clave de establecimiento permanente de entidad no residente
	W. Establecimientos permanentes de entidades no residentes en territorio español.
	*/	

    public static boolean isCulturalAssociation(String doc) {
		if (doc == null || doc.length() != 9) {
			return false;
		}
    	return (doc.matches("^(G).{8}"));
    }

    public static boolean isCooperative(String doc) {
		if (doc == null || doc.length() != 9) {
			return false;
		}
    	return (doc.matches("^(F).{8}"));
    }
}



