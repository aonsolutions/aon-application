package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum RawdocNature implements Serializable {
	
	 INVOICE("Factura")
	 /*
	  * NOMINA, PRESUPUESTO, PEDIDO, etc ....
	  */
	;

	private String description;
	
	private RawdocNature(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static Optional<RawdocNature> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<RawdocNature> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= RawdocNature.values().length) return Optional.empty();
		return Optional.of(RawdocNature.values()[i]);
	}
	
	public static Optional<RawdocNature> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
}
