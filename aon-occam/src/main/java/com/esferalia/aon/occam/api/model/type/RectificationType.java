package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

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

	public static RectificationType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static RectificationType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= RectificationType.values().length) return null;
		return RectificationType.values()[i];
	}
	
	public static String safeValueOf(RectificationType t) {
		return (t == null) ? null : t.name();
	}

	public static RectificationType safeValueOf(String value) {
		if (AonStringUtils.isBlank(value)) return null;
		return AonCollectionUtils.stream(values())
			.filter( rt -> rt.name().equalsIgnoreCase(value) 
						|| rt.getDescription().equalsIgnoreCase(value))
			.findFirst()
			.orElse(null);
	}
	
}