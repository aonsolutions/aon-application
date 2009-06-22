package com.code.aon.finance;

import java.io.Serializable;

public class BankAccount implements Serializable {

	private static final long serialVersionUID = -3424507856145743377L;
	private static final int[] DIGITS = new int[] { 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
	private String entity;
	private String office;
	private String control;
	private String account;

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getControl() {
		return control;
	}

	public void setControl(String control) {
		this.control = control;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String toString() {
		if ( (getEntity() != null) && (getOffice() != null) && (getControl() != null) && (getAccount() != null) ) {
			return getEntity() + '.' + getOffice() + '.' + getControl() + '.' + getAccount();			
		}
		return "";
	}

	public String getValue() {
		if ( (getEntity() != null) && (getOffice() != null) && (getControl() != null) && (getAccount() != null) ) {
			return getEntity()+ getOffice() + getControl() + getAccount();			
		}
		return "";
	}

	public boolean isValid() {
		String cd = calculateControlDigit();
		return cd != null && cd.equals(getControl());
	}

	public String calculateControlDigit() {
		if (getEntity() == null || getEntity().length() != 4 || getOffice() == null
				|| getOffice().length() != 4 || getAccount() == null || getAccount().length() != 10) {
			return "XX";
		}
		String entoff = getEntity() + getOffice();
		int sum = 0;
		int total = 0;
		for (int i = 0; i < entoff.length(); i++) {
			int digito = Integer.parseInt(String.valueOf(entoff.charAt(entoff.length() - 1 - i)));
			sum = digito * DIGITS[i];
			total = total + sum;
		}
		total = 11 - (total % 11);
		if (total == 10) {
			total = 1;
		}
		if (total == 11) {
			total = 0;
		}
		int number = 0;
		int control = 0;
		int c = 0;
		for (int i = 0; i < getAccount().length(); i++) {
			number = Integer.parseInt(String.valueOf(getAccount().charAt(
					getAccount().length() - 1 - i)));
			control = number * DIGITS[i];
			c = c + control;
		}
		c = 11 - (c % 11);
		if (c == 10) {
			c = 1;
		}
		if (c == 11) {
			c = 0;
		}
		return String.valueOf(total) + String.valueOf(c);
	}
}
