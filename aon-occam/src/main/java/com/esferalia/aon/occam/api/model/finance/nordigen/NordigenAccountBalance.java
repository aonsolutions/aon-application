package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Date;

public class NordigenAccountBalance implements Serializable {
	
	private static final long serialVersionUID = 448347838380309919L;
	
	private NordigenBalanceType balanceType;
	private NordigenAccountAmount balanceAmount;
	private Date referenceDate;
	private String originalJson;
	
	public NordigenBalanceType getBalanceType() {
		return balanceType;
	}
	public NordigenAccountBalance setBalanceType(NordigenBalanceType balanceType) {
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
	
	public String getOriginalJson() {
		return originalJson;
	}
	public NordigenAccountBalance setOriginalJson(String originalJson) {
		this.originalJson = originalJson;
		return this;
	}
	
}
