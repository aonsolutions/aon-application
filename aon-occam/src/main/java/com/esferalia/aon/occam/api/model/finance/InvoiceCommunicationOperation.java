package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceCommunicationOperation implements Serializable{

	REGISTER("Alta"),
	MODIFICATION("Modificación"),
	ANNULMENT("Anulación"),
	CONSULTATION("Consulta")
	;
	
	
	String description;
	
	private InvoiceCommunicationOperation(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static InvoiceCommunicationOperation safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InvoiceCommunicationOperation safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceCommunicationOperation.values().length) return null;
		return InvoiceCommunicationOperation.values()[i];
	}
	
	public static InvoiceCommunicationOperation safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (InvoiceCommunicationOperation rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
	public boolean isRegister() {
		return InvoiceCommunicationOperation.REGISTER.equals(this);
	}
	
	public boolean isModification() {
		return InvoiceCommunicationOperation.MODIFICATION.equals(this);
	}
	
	public boolean isAnnulment() {
		return InvoiceCommunicationOperation.ANNULMENT.equals(this);
	}
}
