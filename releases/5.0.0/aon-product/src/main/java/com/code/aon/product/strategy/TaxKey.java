package com.code.aon.product.strategy;

import com.code.aon.account.Account;
import com.code.aon.config.enumeration.TaxType;

public class TaxKey {

	private TaxType type;

	private double percent;

	private Account account;

	public TaxType getType() {
		return type;
	}

	public void setType(TaxType type) {
		this.type = type;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Override
	public boolean equals(Object obj) {
		TaxKey key = (TaxKey)obj;
		if (getAccount() == null || key.getAccount() == null) {
			return (getType().equals(key.getType()) && getPercent() == key.getPercent());
		} 
		return (getType().equals(key.getType()) && getPercent() == key.getPercent() && getAccount().equals(key.getAccount()));
	}

	@Override
	public int hashCode() {
		return getType().hashCode() + new Double(getPercent()).hashCode();
	}

}