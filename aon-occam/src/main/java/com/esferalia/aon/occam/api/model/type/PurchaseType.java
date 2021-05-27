package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum PurchaseType implements Serializable {

	NORMAL,
	SAMPLE,
	ITEM_RETURN,
	MANUFACTURE;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
}
