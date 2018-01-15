package com.esferalia.aon.occam.api.model.type;

public enum WorkgroupStatus {
	
    ACTIVE,
    INACTIVE;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }

}
