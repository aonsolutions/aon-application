package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum MailProcessType  implements Serializable {
	
	 /**
     * AGENCY_NO_SHOW 
     */
	AGENCY_NO_SHOW,

	/**
     * GUEST_RESERVATION 
     */
	GUEST_RESERVATION,
	
	/**
     * INVOICE - FACTURA 
     */
	INVOICE;   
	
	public Byte getValue() {
		return (byte) ordinal();
	}
	
}