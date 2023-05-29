package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum RectificationType implements Serializable {

	NONE("Ninguno")
 		{ @Override public <R,T> R visit(RectificationTypeVisitor<R,T> v, T t) { return v.visitNone(t);} }
	,NORMAL_RECTIFIER("Rectificativa")
		{ @Override public <R,T> R visit(RectificationTypeVisitor<R,T> v, T t) { return v.visitNormalRectifier(t);} }
	,SPECIAL_RECTIFIER("Rectificativa especial")
		{ @Override public <R,T> R visit(RectificationTypeVisitor<R,T> v, T t) { return v.visitSpecialRectifier(t);} }
	,RECTIFIED("Rectificada")
		{ @Override public <R,T> R visit(RectificationTypeVisitor<R,T> v, T t) { return v.visitRectified(t);} }
	;
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

	public static Optional<RectificationType> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( (int) i);
	}
	
	public static Optional<RectificationType> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= RectificationType.values().length) return Optional.empty();
		return Optional.of( RectificationType.values()[i] );
	}
	
	public static Optional<RectificationType> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
	public abstract <R,T> R visit(RectificationTypeVisitor<R,T> visitor, T t);
	public static interface RectificationTypeVisitor<R,T> {
		 R visitNone( T t );
		 R visitNormalRectifier( T t );
		 R visitSpecialRectifier( T t );
		 R visitRectified( T t );
	}

}