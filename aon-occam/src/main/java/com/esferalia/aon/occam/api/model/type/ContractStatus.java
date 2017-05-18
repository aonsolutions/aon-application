package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;


public enum ContractStatus  implements Serializable {
	
	PENDING,
	PROCESSED,
	BLOCKED, 
	BATCHED;
	
	
	public Byte getValue() {
		return (byte) ordinal();
	}
}