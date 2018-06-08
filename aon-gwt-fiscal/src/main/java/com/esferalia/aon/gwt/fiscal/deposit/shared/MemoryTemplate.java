package com.esferalia.aon.gwt.fiscal.deposit.shared;

import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.google.gwt.user.client.rpc.IsSerializable;

public class MemoryTemplate implements IsSerializable{
	Integer id;
	String name;
	D2Deposit d2Deposit;
	Map<String, String> deposit;
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
	public D2Deposit getD2Deposit() {
		return d2Deposit;
	}
	public MemoryTemplate setD2Deposit(D2Deposit d2Deposit) {
		this.d2Deposit = d2Deposit;
		return this;
	}
	
	public Map<String, String> getDeposit() {
		return deposit;
	}
	
	public MemoryTemplate setDeposit(Map<String, String> deposit) {
		this.deposit = deposit;
		return this;
	}
}
