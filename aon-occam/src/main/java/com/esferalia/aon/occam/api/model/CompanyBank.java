package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
public class CompanyBank implements Serializable, IIbanContainer {
	
	private Integer id;
	private String bankAccount;
	private String bic;
	private String alias;

	
	public Integer getId() {
		return id;
	}
	public CompanyBank setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public String getBankAccount() {
		return bankAccount;
	}
	public CompanyBank setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
		return this;
	}
	
	@Override
	public String getBic() {
		return bic;
	}
	public CompanyBank setBic(String bic) {
		this.bic = bic;
		return this;
	}
	@Override
	public String getAlias() {
		return alias;
	}
	public CompanyBank setAlias(String alias) {
		this.alias = alias;
		return this;
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
