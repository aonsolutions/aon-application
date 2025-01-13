package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceCommunicationStatus implements Serializable{

	PENDING("Pendiente"),
	ACCEPTED("Aceptada"),
	ACCEPTED_WITH_ERRORS("Aceptada con Errores"),
	WRONG("Incorrecta"),
	CANCELLED("Anulada");
	
	String description;
	
	private InvoiceCommunicationStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static Optional<InvoiceCommunicationStatus> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<InvoiceCommunicationStatus> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= InvoiceCommunicationStatus.values().length) return Optional.empty();
		return Optional.of(InvoiceCommunicationStatus.values()[i]);
	}
	
	public static Optional<InvoiceCommunicationStatus> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
	public boolean isPending() {
		return this == PENDING;
	}
	
	public boolean isAccepted() {
		return this == ACCEPTED;
	}
	
	public boolean isAcceptedWithErrors() {
		return this == ACCEPTED_WITH_ERRORS;
	}
	
	public boolean isWrong() {
		return this == WRONG;
	}
	
	public boolean isAnnulled() {
		return this == CANCELLED;
	}
}
