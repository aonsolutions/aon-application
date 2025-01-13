package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum ProductStatus implements Serializable {
	
	ACTIVE,
    DISCONTINUED;

	public byte value(){
		return (byte) this.ordinal();
	}
	
	public static Optional<ProductStatus> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<ProductStatus> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= ProductStatus.values().length) return Optional.empty();
		return Optional.of(ProductStatus.values()[i]);
	}
	
	public static Optional<ProductStatus> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
}
