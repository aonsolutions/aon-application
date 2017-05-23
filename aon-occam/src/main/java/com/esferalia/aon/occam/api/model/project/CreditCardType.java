package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;

public enum CreditCardType implements Serializable {

	AX,
	CA,
	DC,
	EC,
	IK,
	JC,
	MC,
	VI;
	
	public String getName() {
		return this.toString();
	}
	
	public Byte value() {
		return (byte) this.ordinal();
	}
    
}