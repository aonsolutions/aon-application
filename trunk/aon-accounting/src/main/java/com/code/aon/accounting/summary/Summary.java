package com.code.aon.accounting.summary;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.CommonUtil;

public class Summary implements Comparable<Summary>{
	
	private static final double ZERO = 0.0;
	
	private Integer accountId;		//Identificador de la cuenta.
	private String code;			//Código de la cuenta.
	private String description;		//Descripción de la cuenta.
	private boolean lastLevel;		// FALSE si la cuenta es de nivel inferior al solicitado.
	private double debit;
	private double credit;
	private double initialDebit;
	private double initialCredit;

	private boolean touched;
	
	public boolean isTouched() {
		return touched;
	}
	public void setTouched(boolean touched) {
		this.touched = touched;
	}
	
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public int getLevel() {
		if (code == null) {
			return -1;	
		}
		if (code.length() > 5) {
			return 5;
		} 
		return code.length();
	}

	public String getIndentedId() {
		return StringUtils.repeat(" ", getLevel()) + getCode();
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
		return (StringUtils.rightPad(getCode(), 12) + "\t" + isLastLevel() + "\t"
				+ StringUtils.rightPad(getShortDescription(), 50) + "\t"
				+ StringUtils.leftPad(Double.toString(getDebit()), 15) + "\t"
				+ StringUtils.leftPad(Double.toString(getCredit()), 15) + "\t"
				+ StringUtils.leftPad(Double.toString(getUnpaidBalance()), 15) + "\t" + StringUtils
				.leftPad(Double.toString(getCreditBalance()), 15));
	}
	
	public void add(Summary summary) {
		setInitialDebit(CommonUtil.round(getInitialDebit() + summary.getInitialDebit()));
		setInitialCredit(CommonUtil.round(getInitialCredit() + summary.getInitialCredit()));
		setDebit(CommonUtil.round(getDebit() + summary.getDebit()));
		setCredit(CommonUtil.round(getCredit() + summary.getCredit()));
		setTouched(true);
	}
	
	public boolean isEmpty() {
		return (CommonUtil.round(debit + credit + initialCredit + initialDebit) == 0);
	}
	public boolean isBalanced() {
		return (CommonUtil.round(getCreditBalance() + getUnpaidBalance()) == 0);
	}
	
	@Override
	public int compareTo(Summary s) {
		if ( s == null) return 1;
		if (getCode() != null && s.getCode() == null) return 1;
		if (getCode() == null && s.getCode() == null) return 0;
		if (getCode() == null && s.getCode() != null) return -1;
		return getCode().compareTo(s.getCode());
	}

}
