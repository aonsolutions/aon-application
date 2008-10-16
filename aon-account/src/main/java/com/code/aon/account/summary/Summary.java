package com.code.aon.account.summary;

import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;

public class Summary {

	/**
	 * Identificador de la cuenta.
	 */
	private String id;
	/**
	 * Descripción de la cuenta.
	 */
	private String description;
	/**
	 * FALSE si la cuenta dentro de la colección, es de nivel inferior al
	 * solicitado.
	 */
	private boolean lastLevel;
	/**
	 * DEBE
	 */
	private double debit;
	/**
	 * HABER
	 */
	private double credit;

	private NumberFormat formatter = DecimalFormat.getCurrencyInstance();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIndentedId() {
		return StringUtils.repeat(" ", getLevel()) + id;
	}

	public String getDescription() {
		return description;
	}

	public String getShortDescription() {
		return StringUtils.abbreviate(getDescription(), 50);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isLastLevel() {
		return lastLevel;
	}

	public void setLastLevel(boolean lastLevel) {
		this.lastLevel = lastLevel;
	}

	public double getDebit() {
		return debit;
	}

	public String getDebitFormmatted() {
		return formatter.format(getDebit());
	}

	public void setDebit(double debit) {
		this.debit = debit;
	}

	public double getCredit() {
		return credit;
	}

	public String getCreditFormmatted() {
		return formatter.format(getCredit());
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

	/**
	 * Saldo Deudor
	 */
	public double getUnpaidBalance() {
		if (getDebit() > getCredit()) {
			return round(getDebit() - getCredit());
		}
		return 0;
	}
	public String getUnpaidBalanceFormmatted() {
		return formatter.format(getUnpaidBalance());
	}

	/**
	 * Saldo Acreedor
	 */
	public double getCreditBalance() {
		if (getCredit() > getDebit()) {
			return round(getCredit() - getDebit());
		}
		return 0;
	}
	public String getCreditBalanceFormmatted() {
		return formatter.format(getCreditBalance());
	}

	private double round(double value) {
		double decimal = Math.pow(10, 2);
		return Math.round(decimal * value) / decimal;
	}

	public String toString() {
		return (StringUtils.rightPad(getId(), 12) + "\t" + isLastLevel() + "\t"
				+ StringUtils.rightPad(getShortDescription(), 50) + "\t"
				+ StringUtils.leftPad(getDebitFormmatted(), 15) + "\t"
				+ StringUtils.leftPad(getCreditFormmatted(), 15) + "\t"
				+ StringUtils.leftPad(getUnpaidBalanceFormmatted(), 15) + "\t" + StringUtils
				.leftPad(getCreditBalanceFormmatted(), 15));
	}

	public int getLevel() {
		if (id != null) {
			if (id.length() > 5) {
				return 5;
			} else if (id.length() == 5) {
				return 4;
			} else {
				return id.length();
			}
		}
		return -1;
	}

}
