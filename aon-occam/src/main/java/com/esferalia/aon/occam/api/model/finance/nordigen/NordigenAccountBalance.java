package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Date;

public class NordigenAccountBalance implements Serializable {
	
	private static final long serialVersionUID = 448347838380309919L;
	
	private NORDIGEN_BALANCE_TYPE balanceType;
	private NordigenAccountAmount balanceAmount;
	private Date referenceDate;
	
	public NordigenAccountBalance() {
		super();
	}
	
	public NordigenAccountBalance(NORDIGEN_BALANCE_TYPE balanceType, NordigenAccountAmount balanceAmount) {
		this.balanceType = balanceType;
		this.balanceAmount = balanceAmount;
	}

	public NORDIGEN_BALANCE_TYPE getBalanceType() {
		return balanceType;
	}

	public NordigenAccountBalance setBalanceType(NORDIGEN_BALANCE_TYPE balanceType) {
		this.balanceType = balanceType;
		return this;
	}

	public NordigenAccountAmount getBalanceAmount() {
		return balanceAmount;
	}

	public NordigenAccountBalance setBalanceAmount(NordigenAccountAmount balanceAmount) {
		this.balanceAmount = balanceAmount;
		return this;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public NordigenAccountBalance setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
		return this;
	}
	
}
