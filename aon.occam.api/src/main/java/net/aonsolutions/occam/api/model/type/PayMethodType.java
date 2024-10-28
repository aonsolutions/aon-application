package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum PayMethodType implements Serializable {
	  
	CASH_BASIS("Met\u00E1lico"),
	NEGOTIABLE_DOCUMENT("Negociable"),
	DEBIT_CARD("Tarjeta D\u00E9bito"),
	CREDIT_CARD("Tarjeta Cr\u00E9dito"),
	CHEQUE("Cheque"),
	BANK_TRANSFER("Transferencia"),
	OTHER("Otros");
	
	private String description;
	
	private PayMethodType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}

	public static Optional<PayMethodType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<PayMethodType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= PayMethodType.values().length) return Optional.empty();
		return Optional.of(PayMethodType.values()[i]);
	}
	
	public static Optional<PayMethodType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
}