package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum Administration implements Serializable {
	
	ALAVA("Araba/Alava")
		{ @Override public <R,T> R visit(AdministrationVisitor<R,T> v, T t) { return v.visitAlava(t);} }
	,BIZKAIA("Bizkaia") 
		{ @Override public <R,T> R visit(AdministrationVisitor<R,T> v, T t) { return v.visitBizkaia(t);} }
	,GIPUZKOA("Gipuzkoa") 
		{ @Override public <R,T> R visit(AdministrationVisitor<R,T> v, T t) { return v.visitGipuzkoa(t);} }
	,NAVARRA("Navarra") 
		{ @Override public <R,T> R visit(AdministrationVisitor<R,T> v, T t) { return v.visitNavarra(t);} }
	,COMMON_TERRITORY("Territorio Com\u00FAn") 
		{ @Override public <R,T> R visit(AdministrationVisitor<R,T> v, T t) { return v.visitCommonTerritory(t);} }
	,UNKNOWN("Otro")
		{ @Override public <R,T> R visit(AdministrationVisitor<R,T> v, T t) { return v.visitUnknown(t);} }
	;

	private String description;
	
	private Administration(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public byte value() {
		return (byte) ordinal();
	}
	
	public static Optional<Administration> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<Administration> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= Administration.values().length) return Optional.empty();
		return Optional.of( Administration.values()[i]);
	}
	
	public static Optional<Administration> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
	public abstract <R,T> R visit(AdministrationVisitor<R,T> visitor, T t);
	public static interface AdministrationVisitor<R,T> {
		 R visitAlava( T t );
		 R visitBizkaia( T t );
		 R visitGipuzkoa( T t );
		 R visitNavarra( T t );
		 R visitCommonTerritory( T t );
		 R visitUnknown( T t );
	}
	
	
}
