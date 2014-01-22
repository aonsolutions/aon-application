package com.esferalia.aon.gwt.fiscal.client.widget;

import com.esferalia.aon.gwt.fiscal.client.css.AonResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

public class DocumentTextBox extends TextBox {

	private static final AonResources AON_RESOURCES = GWT.create(AonResources.class);
	
	public DocumentTextBox() {
		setVisibleLength(9);
		setMaxLength(9);
		addValueChangeHandler( new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if (isValid(event.getValue())) {
					removeStyleName(AON_RESOURCES.css().aonTextBoxError() );	
				} else {
					addStyleName(AON_RESOURCES.css().aonTextBoxError() );	
				}
			}
			
		});
	}
	
	@Override
	public void setValue(String value) {
		super.setValue(value);
		if (value == null || value.length() == 0 || isValid(getValue()) ){
			removeStyleName(AON_RESOURCES.css().aonTextBoxError() );	
		} else {
			addStyleName(AON_RESOURCES.css().aonTextBoxError() );	
		}
	}
	
	
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
		doc[0] = (doc[0] == 'K' || doc[0] == 'L' || doc[0] == 'M') ? '0' : doc[0];
		String numbers = new String(doc, 0, 8);
		if (!isNumeric(numbers)) {
			return false;
		}
		return (doc[8] == DNI_LETTERS[(Integer.parseInt(numbers) % 23)]);
	}

	public static boolean isValidCIF(char[] doc) {
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
}
