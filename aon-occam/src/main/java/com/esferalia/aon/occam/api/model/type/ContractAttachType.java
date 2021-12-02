package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum ContractAttachType implements Serializable {
	
	 TA(98)
	,IDC(99)
	,CERTIFICA2(100)
	;

	private Integer value;
	
	private ContractAttachType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	
	public byte value() {
		return (byte) value.intValue();
	}
	
	public static ContractAttachType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static ContractAttachType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ContractAttachType.values().length) return null;
		return ContractAttachType.values()[i];
	}
	
}
