package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAdministrationVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Administration implements Serializable {
	
	ALAVA("Araba/Alava"){ @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitAlava();} },
	BIZKAIA("Bizkaia") { @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitBizkaia();} },
	GIPUZKOA("Gipuzkoa") { @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitGipuzkoa();} },
	NAVARRA("Navarra") { @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitNavarra();} },
	COMMON_TERRITORY("Territorio Com\u00FAn") { @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitCommonTerritory();} },
	CANARIAS("A.T. Canaria"){ @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitCanarias();} },
	UNKNOWN("Otro"){ @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitUnknown();} },
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
	
	@Deprecated
	public byte getValue() {
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
	
	public abstract <T> T visit(IAdministrationVisitor<T> visitor);
	

	public static Administration safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (Administration rs : values()) {
			if(i.equalsIgnoreCase(rs.name()) || i.equalsIgnoreCase(rs.getDescription()))
				return rs;
		}
		return null;
	}
	
	
	public static Administration safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static Administration safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= Administration.values().length) return null;
		return Administration.values()[i];
	}
}
