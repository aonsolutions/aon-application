package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum ContractAttachType implements Serializable {
	
	 TA(98)
	,TABAJA(99)
	,COPYCONTRACT(101)
	,COPYBASIC(102)
	,CERTIFICA2(103)
	,IDC(104)
	,IDCPLNSS(105)
	,OTHER(106)
	,COPYCONTRACTTRANSFORM(108)
	,MODIFCONTRACT(107)
	,TRANSFORMDRAFT(109)
	,EXTENSIONDRAFT(110)
	,RELOCATIONDRAFT(111)
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
	
	public static ContractAttachType getContractAttachType ( Integer type ) {
		if (type == null) return null;
		for(int i=0; i<ContractAttachType.values().length; i++) {
			ContractAttachType contractAttachType = ContractAttachType.values()[i];
			if(contractAttachType.getValue().equals(type))
				return contractAttachType;
		}
		return null;
	}
	
}
