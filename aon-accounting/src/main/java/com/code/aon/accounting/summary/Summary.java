package com.code.aon.accounting.summary;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.CommonUtil;

public class Summary {
	public Summary() {
	}

	public Summary(String id,String description,double debit,double credit) {
		setId(id);
		setDescription(description);
		setDebit(debit);
		setCredit(credit);
	}
	
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
	/**
	 * DEBE INICIAL
	 */
	private double initialDebit;
	/**
	 * HABER INICIALE
	 */
	private double initialCredit;
	

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
		return StringUtils.abbreviate(getDescription(), 70);
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
			return CommonUtil.round(getDebit() - getCredit());
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
			return CommonUtil.round(getCredit() - getDebit());
		}
		return 0;
	}
	public String getCreditBalanceFormmatted() {
		return formatter.format(getCreditBalance());
	}

	/**
	 * Diferencia para el balance de perdidas y Ganancias
	 */
    public double getDifference() {
        return (credit - debit);
    }

    public double getInitialDebit() {
		return initialDebit;
	}
	public void setInitialDebit(double initialDebit) {
		this.initialDebit = initialDebit;
	}
	public double getInitialCredit() {
		return initialCredit;
	}
	public void setInitialCredit(double initialCredit) {
		this.initialCredit = initialCredit;
	}

	/**
	 * Saldo Deudor Inicial
	 */
	public double getInitialUnpaidBalance() {
		if (getInitialDebit() > getInitialCredit()) {
			return CommonUtil.round(getInitialDebit() - getInitialCredit());
		}
		return 0;
	}
	public String getInitialUnpaidBalanceFormmatted() {
		return formatter.format(getInitialUnpaidBalance());
	}

	/**
	 * Saldo Acreedor Inicial
	 */
	public double getInitialCreditBalance() {
		if (getInitialCredit() > getInitialDebit()) {
			return CommonUtil.round(getInitialCredit() - getInitialDebit());
		}
		return 0;
	}
	public String getInitialCreditBalanceFormmatted() {
		return formatter.format(getInitialCreditBalance());
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
