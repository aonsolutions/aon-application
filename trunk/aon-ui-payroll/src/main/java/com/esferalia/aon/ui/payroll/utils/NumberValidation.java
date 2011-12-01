package com.esferalia.aon.ui.payroll.utils;


public class NumberValidation {
	
	public static boolean validCiasPattern(String value) {
		if(!validateMask(value, "##########A")){
			return false;
		} 
		return true;
	}
	
	public static boolean validCollegeNumberPattern(String value) {
		if(!validateMask(value, "########")){
			return false;
		} 
		return true;
	}
	
	public static boolean validCiasControlDigit(String cias) {
		String lChLetras = "RWAGMYFPDXBNJZSQVHLCKET";
		String lChLetraOriginal = "";
		String lChLetraResultado = "";
		cias = cias.toUpperCase();
		String lChNumeroOriginal = cogerParteValida(cias);
		String letter = lChNumeroOriginal.substring(
				lChNumeroOriginal.length() - 1, lChNumeroOriginal.length());
		Integer lInNumeroResultado;

		try {
			Integer.parseInt(letter);
			lInNumeroResultado = 10;
		} catch (NumberFormatException e) {
			// La cadena no se puede convertir a entero
			lInNumeroResultado = 11;
		}
		lChNumeroOriginal = repeat('0', (lInNumeroResultado - lChNumeroOriginal
				.length()))
				+ lChNumeroOriginal;

		if (lChNumeroOriginal.length() == 10) {
			if (checkDigits(lChNumeroOriginal)) {
				// entonces añadiremos la letra de control que le corresponda
				lInNumeroResultado = calculateResult(lChNumeroOriginal);
				if (lInNumeroResultado == 0) {
					lInNumeroResultado = 23;
				}
				lChLetraResultado = lChLetras.substring(lInNumeroResultado, 1);
				cias = lChNumeroOriginal + lChLetraResultado;
				return true;
			} else {
				return false;
			}
		} else if (lChNumeroOriginal.length() == 11) {
			lChLetraOriginal = lChNumeroOriginal.substring(10, 11);
			if (checkDigits(lChNumeroOriginal.substring(0, 10))) {
				lChNumeroOriginal = lChNumeroOriginal.substring(0, 10);
				lInNumeroResultado = calculateResult(lChNumeroOriginal);
				if (lInNumeroResultado == 0) {
					lInNumeroResultado = 23;
				}
				lChLetraResultado = lChLetras.substring(lInNumeroResultado - 1,
						lInNumeroResultado);
				if (lChLetraResultado.equals(lChLetraOriginal)) {
					cias = lChNumeroOriginal + lChLetraOriginal;
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private static String repeat(char c, int i) {
		String text = "";
		for (int j = 0; j < i; j++) {
			text = text + c;
		}
		return text;
	}

	private static String cogerParteValida(String pChDni) {
		String lChDniResultado;
		lChDniResultado = "";
		lChDniResultado = pChDni.trim();
		lChDniResultado = lChDniResultado.replace(".", "");
		return lChDniResultado;
	}

	
	/**
	 * Funcion que comprueba que los caracteres del dni (que son char)
	 * corresponden a valores numericos
	 * 
	 * @param pChNumero
	 * @return
	 */
	private static Boolean checkDigits(String pChNumero) {
		int pos = 0;
		while (pos < pChNumero.length()) {
			if (pChNumero.charAt(pos) < '0' || pChNumero.charAt(pos) > '9') {
				return false;
			}
			pos++;
		}
		return true;
	}

	private static Integer calculateResult(String pChDato) {
		Integer lDcNumero = Integer.parseInt(pChDato);
		Integer parteEntera = lDcNumero / 23;
		return (lDcNumero - (parteEntera * 23));
	}

	public static boolean validCollegeNumberControlDigit(String pChNumcol) {
		Integer lSmLong;
		lSmLong = pChNumcol.length();
		
		if(Integer.parseInt(pChNumcol.substring(0, 2))<1 || Integer.parseInt(pChNumcol.substring(0, 2))>50){
			return false;
		}
		if (lSmLong == 8) {
			if (!pChNumcol.substring(7, 8).equals(
					gFnDCColegiado(pChNumcol.substring(0, 7)))) {
				return false;
			}
		} else {
			if (lSmLong == 2) {
				pChNumcol += "99999";
			}
			pChNumcol += gFnDCColegiado(pChNumcol);
		}
		return true;
	}

	private static String gFnDCColegiado(String pInNumero) {
		Integer lDcNumero = Integer.parseInt(pInNumero);
		Integer parteEntera = lDcNumero / 9;
		lDcNumero = (lDcNumero - (parteEntera * 9));
		return lDcNumero.toString();
	}
	
	/**
	 * Valida el parametro pDato segun el tipo de mascara indicado.
	 * 
	 * @param pDato
	 * @param pMask
	 * @return
	 */
	public static final boolean validateMask(String value, String mask) {
		boolean valid = false;
		if (value.length() == mask.length()) {
			for (int i = 0; i < value.length(); i++) {
				valid = false;
				if ('#' == mask.charAt(i)) {
					if (value.charAt(i) <= '9' && value.charAt(i) >= '0') {
						valid = true;
					}
				} else if ('A' == mask.charAt(i)) {
					if ((value.charAt(i) >= 'A' && value.charAt(i) <= 'Z')
							|| (value.charAt(i) >= 'a' && value.charAt(i) <= 'z')) {
						valid = true;
					}
				} else if ('X' == mask.charAt(i)) {
					if ((value.charAt(i) >= 'A' && value.charAt(i) <= 'Z')
							|| (value.charAt(i) >= 'a' && value.charAt(i) <= 'z')
							|| (value.charAt(i) <= '9' && value.charAt(i) >= '0')) {
						valid = true;
					}
				}
				if (!valid) {
					break;
				}
			}
		}
		return valid;
	}

	public static void main(String[] args) {
		NumberValidation.validateMask("111a1F", "#####X");
	} 
}
