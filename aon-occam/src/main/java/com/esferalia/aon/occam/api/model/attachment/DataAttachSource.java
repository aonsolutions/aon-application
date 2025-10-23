package com.esferalia.aon.occam.api.model.attachment;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;


public enum DataAttachSource implements Serializable {
	
	QUALITY,
	INVOICE,
	FBATCH,
	PRODUCTION,
	DELIVERY,
	SII,
	SERES,
	INGENET,
	MOD303,
	MOD111,
	MOD115,
	MOD123,
	MOD130,
	MOD131,
	MOD390,
	IMPORTATION,
	SISTEMA_RED,
	INVOICE_PRINT_CONFIGURATION,
	TBAI,
	MOD202,
	MOD190,
	LROE,
	MOD180,
	MOD193,
	MOD184,
	MOD347,
	MOD349,
	MOD200,
	MOD369,
	VERIFACTU
	;

	public byte value() {
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
    public static DataAttachSource safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static DataAttachSource safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= DataAttachSource.values().length) return null;
		return DataAttachSource.values()[i];
	}
	
	public static DataAttachSource safeValueOf(InvoiceCommunicationType ict) {
		if(InvoiceCommunicationType.SII.equals(ict)) {
			return DataAttachSource.SII;
		} else if(InvoiceCommunicationType.TBAI.equals(ict)) {
			return DataAttachSource.TBAI;
		} else if(InvoiceCommunicationType.LROE.equals(ict)) {
			return DataAttachSource.LROE;
		} else if(InvoiceCommunicationType.VERIFACTU.equals(ict)) {
			return DataAttachSource.VERIFACTU;
		} else if(InvoiceCommunicationType.SERES.equals(ict)) {
			return DataAttachSource.SERES;
		}
		return null;
	}
	
}