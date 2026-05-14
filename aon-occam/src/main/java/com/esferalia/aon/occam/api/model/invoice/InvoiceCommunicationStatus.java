package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceCommunicationStatus implements Serializable{
	
	PENDING("Pendiente") {
		@Override public void accept(InvoiceCommunicationStatusVisitor visitor) {visitor.visitPending();}},
	ACCEPTED("Aceptada", "Correcto") { 
		@Override public void accept(InvoiceCommunicationStatusVisitor visitor) {visitor.visitAccepted();}},
	ACCEPTED_WITH_ERRORS("Aceptada con Errores", "AceptadoConErrores"){
		@Override public void accept(InvoiceCommunicationStatusVisitor visitor) {visitor.visitAcceptedWithErrors();}},
	WRONG("Incorrecta", "Incorrecto"){
		@Override public void accept(InvoiceCommunicationStatusVisitor visitor) { visitor.visitWrong();}},
	CANCELLED("Anulada") {
		@Override public void accept(InvoiceCommunicationStatusVisitor visitor) { visitor.visitCancelled();}},
	EXTERNALLY_COMMUNICATED("Com. Externamente") {
		@Override public void accept(InvoiceCommunicationStatusVisitor visitor) { visitor.visitExternallyCommunicated();}}
	;
	
	String[] description;
	private InvoiceCommunicationStatus(String... description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description[0];
	}
	
	public List<String> getDescriptions() {
		return Arrays.asList(description);
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static String name( InvoiceCommunicationStatus i ) {
		return (i == null) ? null : i.name(); 
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
			if(i.equalsIgnoreCase(rs.name()) || rs.getDescriptions().contains(i))
				return rs;
		}
		return null;
	}
	
	public boolean isPending() 				{return this == InvoiceCommunicationStatus.PENDING;}
	public boolean isAccepted() 			{return this == InvoiceCommunicationStatus.ACCEPTED;}
	public boolean isAcceptedWithErrors() 	{return this == InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS;}
	public boolean isWrong() 				{return this == InvoiceCommunicationStatus.WRONG;}
	public boolean isAnnulled() 			{return this == InvoiceCommunicationStatus.CANCELLED;}
	
	public abstract void accept( InvoiceCommunicationStatusVisitor visitor );
	public interface InvoiceCommunicationStatusVisitor {
		void visitPending();
		void visitAccepted();
		void visitAcceptedWithErrors();
		void visitWrong();
		void visitCancelled();
		void visitExternallyCommunicated();
	}
	
	public static boolean isPending(InvoiceCommunicationType type, InvoiceCommunicationStatus status) {
		if (type == null) return true;
		if (status == null && type == InvoiceCommunicationType.NO_VERIFACTU) return true;
		return (type != InvoiceCommunicationType.NO_VERIFACTU && status == PENDING);
	}
	
}
