package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;

public enum BalanceType implements Serializable {
	
	 BALANCE_NORMAL		("Balance de Situaci\u00F3n (Normal)")
	,BALANCE_ABBREVIATE	("Balance de Situaci\u00F3n (Abreviado)")
	,BALANCE_PYMES		("Balance de Situaci\u00F3n (PYMES)")
	,PYG_NORMAL			("Cuenta de explotaci\u00F3n (Normal)")
	,PYG_ABBREVIATE		("Cuenta de explotaci\u00F3n (Abreviado)")
	;
	
	private String name;
	
	private BalanceType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static BalanceType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static BalanceType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= BalanceType.values().length) return null;
		return BalanceType.values()[i];
	}
	
}
