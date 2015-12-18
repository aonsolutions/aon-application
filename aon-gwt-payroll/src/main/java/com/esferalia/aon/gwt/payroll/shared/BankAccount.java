package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.shared.HasId;

@SuppressWarnings("serial")
public class BankAccount implements Serializable, HasId<Integer>{

	private Integer id;
	private String bic;
	private String alias;
	private String account;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	
}
