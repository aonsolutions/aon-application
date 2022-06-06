package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum ContractLeaveDetailStatus {
	
	PENDING,
	BATCHED,
	RETURNED,
	PROCESSED,
	;
	
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }

	public static ContractLeaveDetailStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ContractLeaveDetailStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ContractLeaveDetailStatus.values().length) return null;
		return ContractLeaveDetailStatus.values()[i];
	}
	
	public static ContractLeaveDetailStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return PENDING;
		for (ContractLeaveDetailStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return PENDING;
	}

}
