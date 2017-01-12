package com.esferalia.aon.occam.api.model.type;

public enum ElaborationSource {

	SALES,
	PURCHASE,
	;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }

}
