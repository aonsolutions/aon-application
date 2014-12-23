package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
public class CompanyBank implements Serializable, IIbanContainer {
	
	private String bankAccount;
	private String bic;
	private String alias;
	
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	public String getBic() {
		return bic;
	}
	public void setBic(String bic) {
		this.bic = bic;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Override
	public String getIBan() {
		return bankAccount;
	}
	@Override
	public String getDisplay() {
		return AonStringUtils.isEmpty(alias)?"":"(" + alias +") " + bankAccount;
	}
	
}
