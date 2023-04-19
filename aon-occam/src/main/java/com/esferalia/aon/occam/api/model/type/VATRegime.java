package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum VATRegime implements Serializable {

	 GENERAL	("R\u00E9gimen General"		, "GENE", "R\u00E9g. Genr.")
	,SIMPLIFIED ("R\u00E9gimen Simplificado", "SIMP", "R\u00E9g. Simpl.")
	,EXEMPT 	("Exento"					, "EXEN", "Exento")
	;
	
	private String name;
	private String abbr;
	private String description;
	
	private VATRegime(String name,String abbr, String description){
		this.name = name;
		this.abbr = abbr;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAbbr() {
		return abbr;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) ordinal();
	}	
	
	public static VATRegime safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static VATRegime safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i.intValue() >= VATRegime.values().length) return null;
		return VATRegime.values()[i];
	}
	
	public static VATRegime safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return GENERAL;
		for (VATRegime rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return GENERAL;
	}
	
	public boolean isGeneral() {
		return this.equals(GENERAL);
	}
	
	public boolean isSimplified() {
		return this.equals(SIMPLIFIED);
	}
	
	public boolean isExempt() {
		return this.equals(EXEMPT);
	}

}