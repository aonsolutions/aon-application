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
	public MemoryTemplate setName(String name) {
		this.name = name;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public MemoryTemplate setId(Integer id) {
		this.id = id;
		return this;
	}
	public D2DepositTreeObject getD2Deposit2014() {
		return d2Deposit2014;
	}
	public MemoryTemplate setD2Deposit2014(D2DepositTreeObject d2Deposit2014) {
		this.d2Deposit2014 = d2Deposit2014;
		return this;
	}
}
