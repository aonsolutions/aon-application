package net.aonsolutions.occam.api.model.type;

import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum CertificateType {
	TGSS,
	SEPE,
	AEAT;
	
	private CertificateType() {
	
	}

	public byte value(){
		return (byte) ordinal();
	}
	
	public static Optional<CertificateType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<CertificateType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= CertificateType.values().length) return Optional.empty();
		return Optional.of(CertificateType.values()[i]);
	}
	
	public static Optional<CertificateType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
}
