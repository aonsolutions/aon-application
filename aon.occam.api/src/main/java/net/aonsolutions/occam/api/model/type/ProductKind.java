package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum ProductKind implements Serializable {
	
	SALE_PURCHASE,
	PURCHASE,
	SALE;

	public byte value(){
		return (byte) this.ordinal();
	}
	
	public static Optional<ProductKind> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<ProductKind> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= ProductKind.values().length) return Optional.empty();
		return Optional.of(ProductKind.values()[i]);
	}
	
	public static Optional<ProductKind> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
}
