package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;

public enum ReservationCheckStatus implements Serializable {

	NO_CHECK,
	CHECK_IN,
	CHECK_OUT,
	NO_SHOW,
	NO_SHOW_NO_INVOICEABLE,
	CANCEL_INVOICEABLE,
	CANCEL_NO_INVOICEABLE;
    
	public String getName() {
		return this.toString();
	}
	
	public Byte value() {
		return (byte) this.ordinal();
	}
    
}