package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum DocumentType implements Serializable {
	
	NIF("DNI")
	,CIF("CIF")
	,NIE("NIE")
	,PASSPORT("Pasp.")
	,WORK_PERMIT("P.T.")
	,COMMUNITY_CARD("T.C.")
	,OTHER("Otr.")
	,NOT_CENSUSED("No Censado")
	;

	private String description;
	
	private DocumentType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static DocumentType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static DocumentType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= DocumentType.values().length) return null;
		return DocumentType.values()[i];
	}
	
	public static DocumentType safeValueOf( String i ) {
		for (DocumentType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
