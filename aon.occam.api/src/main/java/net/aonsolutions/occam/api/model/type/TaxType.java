package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum TaxType implements Serializable {

	  UNKNOWN (" ---- ")
	, VAT("IVA")
	, RETENTION("IRPF")
	;

	private String name;

	private TaxType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public byte value() {
		return (byte) ordinal();
	}

	public static Optional<TaxType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<TaxType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= TaxType.values().length) return Optional.empty();
		return Optional.of(TaxType.values()[i]);
	}
	
	public static Optional<TaxType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}

	public static Byte value(TaxType t) {
		return t == null ? null : t.value();
	}

}