package com.esferalia.aon.occam.api.model.payroll;

import java.io.Serializable;

public enum FamilySituation implements Serializable{
	NO_MARRIED_WITH_SONS(1),
	MARRIED(2),
	OTHER(3)
	;

	private int value;
	
	private FamilySituation(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
}
