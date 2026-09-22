package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceCommunicationOperation implements Serializable{

	REGISTER("Alta") 					{ @Override public <T> T visit(InvoiceCommunicationOperationVisitor<T> visitor) {return visitor.visitRegister();}},	
	MODIFICATION("Modificaci\u00F3n") 	{ @Override public <T> T visit(InvoiceCommunicationOperationVisitor<T> visitor) {return visitor.visitModification();}},
	ANNULMENT("Anulaci\u00F3n") 		{ @Override public <T> T visit(InvoiceCommunicationOperationVisitor<T> visitor) {return visitor.visitAnnulment();}},
	CONSULTATION("Consulta") 			{ @Override public <T> T visit(InvoiceCommunicationOperationVisitor<T> visitor) {return visitor.visitConsultation();}}
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
	
	public abstract <T> T visit(InvoiceCommunicationOperationVisitor<T> visitor);
	public interface InvoiceCommunicationOperationVisitor<T> {
		T visitRegister();
		T visitModification();
		T visitAnnulment();
		T visitConsultation();
	}	
}
