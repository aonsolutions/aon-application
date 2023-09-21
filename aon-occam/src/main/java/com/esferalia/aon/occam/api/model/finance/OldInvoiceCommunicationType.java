package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

@Deprecated
public enum OldInvoiceCommunicationType implements Serializable{
	
	LROE_1_1,
	LROE_1_2,
	LROE_1_3,
	LROE_2,
	LROE_2_1,
	LROE_2_2,
	LROE_3,
	LROE_3_1,
	LROE_3_2,
	LROE_3_3,
	LROE_3_4,
	LROE_4_1,
	LROE_4_2,
	LROE_5_1,
	LROE_5_2,
	LROE_6,
	LROE_6_1,
	LROE_6_2,
	LROE_6_3,
	LROE_7_1,
	LROE_7_2,
	LROE_7_3,
	LROE_7_4,
	LROE_8_1,
	LROE_8_2,
	SII,
	TBAI,
	;
	
	
	private OldInvoiceCommunicationType() {

	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static OldInvoiceCommunicationType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static OldInvoiceCommunicationType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= OldInvoiceCommunicationType.values().length) return null;
		return OldInvoiceCommunicationType.values()[i];
	}
	
	public static OldInvoiceCommunicationType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (OldInvoiceCommunicationType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
