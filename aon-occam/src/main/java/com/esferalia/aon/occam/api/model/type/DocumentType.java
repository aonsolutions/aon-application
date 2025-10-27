package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

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
		if(AonStringUtils.isBlank(i)) return null;
		for (DocumentType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}

	public static Byte value(DocumentType dt) {
		return dt==null?null:dt.value();
	}
	public static String name(DocumentType dt) {
		return dt==null?null:dt.name();
	}
	
	public static DocumentType identify(String input) {
        if (input == null) return DocumentType.NOT_CENSUSED;

        String doc = input.trim().toUpperCase();

        // --- NIF: 8 dígitos + letra final ---
        if (doc.matches("^[0-9]{8}[A-Z]$")) {
            return DocumentType.NIF;
        }

        // --- NIE: empieza con X, Y o Z + 7 dígitos + letra final ---
        if (doc.matches("^[XYZ][0-9]{7}[A-Z]$")) {
            return DocumentType.NIE;
        }

        // --- CIF: letra inicial + 7 dígitos + letra o número ---
        if (doc.matches("^[ABCDEFGHJKLMNPQRSUVW][0-9]{7}[0-9A-J]$")) {
            return DocumentType.CIF;
        }

        // --- Pasaporte: alfanumérico, 6/9 caracteres ---
        if (doc.matches("^[A-Z0-9]{6,9}$")) {
            return DocumentType.PASSPORT;
        }

        // --- Permiso de trabajo: comienza por PT ---
        if (doc.startsWith("PT")) {
            return DocumentType.WORK_PERMIT;
        }

        // --- Tarjeta comunitaria: comienza por TC ---
        if (doc.startsWith("TC")) {
            return DocumentType.COMMUNITY_CARD;
        }

        // --- Documento vacío o desconocido ---
        if (doc.isEmpty() || doc.equals("NO CENSADO")) {
            return DocumentType.NOT_CENSUSED;
        }

        // --- Otros casos ---
        return DocumentType.OTHER;
    }
	
}
