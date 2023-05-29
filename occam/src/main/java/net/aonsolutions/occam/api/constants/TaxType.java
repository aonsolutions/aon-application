package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum TaxType implements Serializable {

	  UNKNOWN ("---")
		{ @Override public <R,T> R visit(TaxTypeVisitor<R,T> v, T t) { return v.visitUnknown(t);} }
	, VAT("IVA")
		{ @Override public <R,T> R visit(TaxTypeVisitor<R,T> v, T t) { return v.visitVat(t);} }
	, RETENTION("IRPF")
		{ @Override public <R,T> R visit(TaxTypeVisitor<R,T> v, T t) { return v.visitRetention(t);} }
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

	public static Optional<TaxType> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<TaxType> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= TaxType.values().length) return Optional.empty();
		return Optional.of( TaxType.values()[i]);
	}
	
	public static Optional<TaxType> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
	public abstract <R,T> R visit(TaxTypeVisitor<R,T> visitor, T t);
	public static interface TaxTypeVisitor<R,T> {
		 R visitUnknown( T t );
		 R visitVat( T t );
		 R visitRetention( T t );
	}
	
	
	

}