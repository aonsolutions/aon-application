package com.esferalia.aon.gwt.common.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CompanyBank implements Serializable, IsSerializable, IIbanContainer {
	
	private static final long serialVersionUID = 1439647684878998653L;
	
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
		return AonUtil.isEmpty(alias)?"":"(" + alias +") " + bankAccount;
	}
	
}
