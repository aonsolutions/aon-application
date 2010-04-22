package com.code.aon.registry;

import org.apache.commons.lang.StringUtils;

public class RegistryDocument {

	private static final char[] DNI_LETTERS = { 'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D',
			'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E' };
	private static final char[] NIF_LETTERS = { 'J', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I' };

	private String document;
	private char[] doc;
	
	public RegistryDocument() {
	}

	public RegistryDocument(String document) {
		setDocument(document);
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
		doc = (getDocument() != null)?getDocument().toCharArray():null;
	}

	public boolean isValid() {
		if (doc == null || doc.length == 0) {
			return false;
		}
		if (doc.length == 9) {
			String first = new String(doc,0,1);
			return first.matches("[0-9|K|L|M|X|Y|Z]")? isValidDNI() : isValidNIF();
		}
		return false;
	}

	public boolean isValidDNI() {
		if (doc == null || doc.length == 0) {
			return false;
		}
		doc[0] = (doc[0] == 'X' || doc[0] == 'K' || doc[0] == 'L' || doc[0] == 'M') ? '0'
				: doc[0];
		doc[0] = (doc[0] == 'Y') ? '1' : doc[0];
		doc[0] = (doc[0] == 'Z') ? '2' : doc[0];
		String numbers = new String(doc, 0, 8);
		if (!StringUtils.isNumeric(numbers)) {
			return false;
		}
		return (doc[8] == DNI_LETTERS[(Integer.parseInt(numbers) % 23)]);
	}

	public boolean isValidNIF() {
		if (doc == null || doc.length == 0) {
			return false;
		}
		int lInDC = 0;
		for (int i = 1; i < 8; ++i) {
			String strDigit = new String(doc, i, 1);
			if (!StringUtils.isNumeric(strDigit)) {
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
		if (!StringUtils.isNumeric(strDC)) {
			return false;
		}
		return (Integer.parseInt(strDC) == lInDC);
	}

}
