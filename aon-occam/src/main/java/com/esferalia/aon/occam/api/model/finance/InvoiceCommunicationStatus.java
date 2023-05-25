package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceCommunicationStatus implements Serializable{

	PENDING,
	ACCEPTED,
	ACCEPTED_WITH_ERRORS,
	WRONG,
	CANCELLED;
	
	
	private InvoiceCommunicationStatus() {

	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static InvoiceCommunicationStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InvoiceCommunicationStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceCommunicationStatus.values().length) return null;
		return InvoiceCommunicationStatus.values()[i];
	}
	
	public static InvoiceCommunicationStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (InvoiceCommunicationStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
	public boolean isPending() {
		return InvoiceCommunicationStatus.PENDING.equals(this);
	}
	
	public boolean isAccepted() {
		return InvoiceCommunicationStatus.ACCEPTED.equals(this);
	}
	
	public boolean isAcceptedWithErrors() {
		return InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS.equals(this);
	}
	
	public boolean isWrong() {
		return InvoiceCommunicationStatus.WRONG.equals(this);
	}
	
	public boolean isAnnulled() {
		return InvoiceCommunicationStatus.CANCELLED.equals(this);
	}
}
