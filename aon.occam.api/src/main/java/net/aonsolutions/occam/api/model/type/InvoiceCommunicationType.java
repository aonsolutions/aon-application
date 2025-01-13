package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public enum InvoiceCommunicationType implements Serializable{
 
	SII { @Override public <T> T visit(InvoiceCommunicationTypeVisitor<T> visitor) { return visitor.visitSII();}},
	TBAI { @Override public <T> T visit(InvoiceCommunicationTypeVisitor<T> visitor) { return visitor.visitTBAI();}},
	LROE { @Override public <T> T visit(InvoiceCommunicationTypeVisitor<T> visitor) { return visitor.visitLROE();}},
	SERES { @Override public <T> T visit(InvoiceCommunicationTypeVisitor<T> visitor) { return visitor.visitSERES();}},
	EMAIL { @Override public <T> T visit(InvoiceCommunicationTypeVisitor<T> visitor) { return visitor.visitEMAIL();}}
	;
	
	private InvoiceCommunicationType() {
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static Optional<InvoiceCommunicationType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<InvoiceCommunicationType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= InvoiceCommunicationType.values().length) return Optional.empty();
		return Optional.of(InvoiceCommunicationType.values()[i]);
	}
	
	public static Optional<InvoiceCommunicationType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
	public boolean isTbai() {
		return this == TBAI;
	}
	
	public boolean isSii() {
		return this == SII;
	}
	
	public boolean isLroe() {
		return this == LROE;
	}
	
	public boolean isSeres() {
		return this == SERES;
	}

	public abstract <T> T visit(InvoiceCommunicationTypeVisitor<T> visitor);
	public static interface InvoiceCommunicationTypeVisitor<T> {
		T visitSII();
		T visitTBAI();
		T visitLROE();
		T visitSERES();
		T visitEMAIL();
	}
}
