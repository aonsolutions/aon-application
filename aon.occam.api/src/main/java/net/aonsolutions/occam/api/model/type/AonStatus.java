package net.aonsolutions.occam.api.model.type;

import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum AonStatus {

	BILLABLE("Facturable"),
	NOT_BILLABLE("No facturable"),
	;

	private String name;
	private AonStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static Optional<AonStatus> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<AonStatus> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= AonStatus.values().length) return Optional.empty();
		return Optional.of(AonStatus.values()[i]);
	}
	
	public static Optional<AonStatus> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}

	public static Byte value( AonStatus t ) {
		return t == null ? null : t.value();
	}
	public static String name( AonStatus t ) {
		return t == null ? null : t.name();
	}
}
