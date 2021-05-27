package com.esferalia.aon.occam.api.model.fiscal.d2_deposit;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum DepositType implements Serializable {
	 ABREVIADO("Abreviado")
	,PYMES("Pymes")
	,NORMAL("Normal")
	;
	 
	private String label;
	
	private DepositType(String label) {
		this.label = label;	
	}
	
	public static DepositType valueOfLabel(String label) {
		for (DepositType tp : DepositType.values()) {
			if (AonStringUtils.equals(label, tp.label)) {
				return tp;
			}
		}
		throw new IllegalArgumentException("No existe el tipo " + label);
	}
	
	public String getLabel() {
		return label;
	}
}
