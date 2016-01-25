package com.esferalia.aon.gwt.fiscal.deposit.shared;

import com.esferalia.aon.gwt.fiscal.deposit.client.D2DepositTreeObject;
import com.google.gwt.user.client.rpc.IsSerializable;

public class MemoryTemplate implements IsSerializable{
	Integer id;
	String name;
	D2DepositTreeObject d2Deposit2014;
	String cif;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public D2DepositTreeObject getD2Deposit2014() {
		return d2Deposit2014;
	}
	public void setD2Deposit2014(D2DepositTreeObject d2Deposit2014) {
		this.d2Deposit2014 = d2Deposit2014;
	}
}
