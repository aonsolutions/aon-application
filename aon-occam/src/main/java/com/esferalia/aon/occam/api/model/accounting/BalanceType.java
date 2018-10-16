package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;

public enum BalanceType implements Serializable {
	
	 BALANCE_NORMAL		("Balance de Situación (Normal)")
	,BALANCE_ABBREVIATE	("Balance de Situación (Abreviado)")
	,BALANCE_PYMES		("Balance de Situación (PYMES)")
	;
	
	private String name;
	
	private BalanceType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
