package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum FinanceType implements Serializable {
	  
	COLLECTION("Cobro"),
	PAYMENT("Pago"),
	;
	
	private String description;
	
	private FinanceType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}

	public static Optional<FinanceType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<FinanceType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= FinanceType.values().length) return Optional.empty();
		return Optional.of(FinanceType.values()[i]);
	}
	
	public static Optional<FinanceType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}

	public static Byte value(FinanceType t) {
		return t == null ? null : t.value();
	}
	public static String name(FinanceType t) {
		return t == null ? null : t.name();
	}
}