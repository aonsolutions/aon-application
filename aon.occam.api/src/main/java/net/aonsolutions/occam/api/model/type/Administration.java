package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Administration implements Serializable {
	
	ALAVA("Araba/Alava")
		{ @Override public <T> T visit(AdministrationVisitor<T> v){ return v.visitAlava();} },
	BIZKAIA("Bizkaia") 
		{ @Override public <T> T visit(AdministrationVisitor<T> v){ return v.visitBizkaia();} },
	GIPUZKOA("Gipuzkoa") 
		{ @Override public <T> T visit(AdministrationVisitor<T> v){ return v.visitGipuzkoa();} },
	NAVARRA("Navarra") 
		{ @Override public <T> T visit(AdministrationVisitor<T> v){ return v.visitNavarra();} },
	COMMON_TERRITORY("Territorio Com\u00FAn") 
		{ @Override public <T> T visit(AdministrationVisitor<T> v){ return v.visitCommonTerritory();} },
	UNKNOWN("Otro")
		{ @Override public <T> T visit(AdministrationVisitor<T> v){ return v.visitUnknown();} },
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
	
	public boolean isAraba() {
		return (this == Administration.ALAVA);
	}
	public boolean isBizkaia() {
		return (this == Administration.BIZKAIA);
	}
	public boolean isGipuzkoa() {
		return (this == Administration.GIPUZKOA);
	}
	public boolean isNavarra() {
		return (this == Administration.NAVARRA);
	}
	public boolean isAEAT() {
		return (this == Administration.COMMON_TERRITORY);
	}
	
	public static Optional<Administration> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<Administration> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= Administration.values().length) return Optional.empty();
		return Optional.of(Administration.values()[i]);
	}
	
	public static Optional<Administration> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}

	public abstract <T> T visit(AdministrationVisitor<T> visitor);
	public static interface AdministrationVisitor<T> {
		T visitAlava();
		T visitBizkaia();
		T visitGipuzkoa();
		T visitNavarra();
		T visitCommonTerritory();
		T visitUnknown();
	}
}
