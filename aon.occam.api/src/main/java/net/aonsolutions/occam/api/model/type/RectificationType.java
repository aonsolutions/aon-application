package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum RectificationType implements Serializable {

	NONE("Ninguno"),
	NORMAL_RECTIFIER("Rectificativa"),
	SPECIAL_RECTIFIER("Rectificativa especial"),
	RECTIFIED("Rectificada");
	
	private String description;
	
	private RectificationType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public byte value() {
		return (byte) this.ordinal();
	}

	public static Optional<RectificationType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<RectificationType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= RectificationType.values().length) return Optional.empty();
		return Optional.of(RectificationType.values()[i]);
	}
	
	public static Optional<RectificationType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
	public static Byte value(RectificationType t) {
		return t == null ? null : t.value();
	}
	
}