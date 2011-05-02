package com.code.aon.accounting.summary;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.CommonUtil;

public class Summary {
	private static final double ZERO = 0.0;
	
	private String id;				//Identificador de la cuenta.
	private String description;		//Descripción de la cuenta.
	private boolean lastLevel;		// FALSE si la cuenta es de nivel inferior al solicitado.
	private double debit;
	private double credit;
	private double initialDebit;
	private double initialCredit;
	private double openingDebit;
	private double openingCredit;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public int getLevel() {
		if (id == null) {
			return -1;	
		}
		if (id.length() > 5) {
			return 5;
		} else if (id.length() == 5) {
			return 4;
		} 
		return id.length();
	}

	public String getIndentedId() {
		return StringUtils.repeat(" ", getLevel()) + id;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getShortDescription() {
		return StringUtils.abbreviate(getDescription(), 70);
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
	public void setDebit(double debit) {
		this.debit = debit;
	}

	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
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

	public double getOpeningDebit() {
		return openingDebit;
	}
	public void setOpeningDebit(double openingDebit) {
		this.openingDebit = openingDebit;
	}
	public double getOpeningCredit() {
		return openingCredit;
	}
	public void setOpeningCredit(double openingCredit) {
		this.openingCredit = openingCredit;
	}
	public double getTotalDebit() {
		return CommonUtil.round(getInitialDebit() + getDebit());
	}
	public double getTotalCredit() {
		return CommonUtil.round(getInitialCredit() + getCredit());
	}

	public double getInitialUnpaidBalance() {
		if (getInitialDebit() > getInitialCredit()) {
			return CommonUtil.round(getInitialDebit() - getInitialCredit());
		}
		return ZERO;
	}
	public double getInitialCreditBalance() {
		if (getInitialCredit() > getInitialDebit()) {
			return CommonUtil.round(getInitialCredit() - getInitialDebit());
		}
		return ZERO;
	}

	public double getUnpaidBalance() {
		if (getTotalDebit() > getTotalCredit()) {
			return CommonUtil.round(getTotalDebit() - getTotalCredit());
		}
		return ZERO;
	}
	public double getCreditBalance() {
		if (getTotalCredit() > getTotalDebit()) {
			return CommonUtil.round(getTotalCredit() - getTotalDebit());
		}
		return ZERO;
	}

	public double getPeriodUnpaidBalance() {
		if (getDebit() > getCredit()) {
			return CommonUtil.round(getDebit() - getCredit());
		}
		return ZERO;
	}
	public double getPeriodCreditBalance() {
		if (getCredit() > getDebit()) {
			return CommonUtil.round(getCredit() - getDebit());
		}
		return ZERO;
	}

	public double getDifference() {
        return CommonUtil.round(getTotalCredit() - getTotalDebit());
    }

	public String toString() {
		return (StringUtils.rightPad(getId(), 12) + "\t" + isLastLevel() + "\t"
				+ StringUtils.rightPad(getShortDescription(), 50) + "\t"
				+ StringUtils.leftPad(Double.toString(getDebit()), 15) + "\t"
				+ StringUtils.leftPad(Double.toString(getCredit()), 15) + "\t"
				+ StringUtils.leftPad(Double.toString(getUnpaidBalance()), 15) + "\t" + StringUtils
				.leftPad(Double.toString(getCreditBalance()), 15));
	}

}
