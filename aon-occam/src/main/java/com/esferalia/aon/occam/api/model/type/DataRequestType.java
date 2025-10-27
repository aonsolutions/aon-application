package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;

public enum DataRequestType implements Serializable{

	SII,
	TBAI,
	LROE,
	VERIFACTU,
	SERES;

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
	
	public static DataRequestType safeValueOf(Byte i) {
		if (i == null)
			return null;
		return safeValueOf(i.intValue());
	}

	public static DataRequestType safeValueOf(Integer i) {
		if (i == null)
			return null;
		if (i < 0 || i >= DataRequestType.values().length)
			return null;
		return DataRequestType.values()[i];
	}
	
	public static DataRequestType safeValueOf(InvoiceCommunicationType ict) {
		if(InvoiceCommunicationType.SII.equals(ict)) {
			return DataRequestType.SII;
		} else if(InvoiceCommunicationType.TBAI.equals(ict)) {
			return DataRequestType.TBAI;
		} else if(InvoiceCommunicationType.LROE.equals(ict)) {
			return DataRequestType.LROE;
		} else if(InvoiceCommunicationType.VERIFACTU.equals(ict)) {
			return DataRequestType.VERIFACTU;
		} else if(InvoiceCommunicationType.SERES.equals(ict)) {
			return DataRequestType.SERES;
		}
		return null;
	}
}