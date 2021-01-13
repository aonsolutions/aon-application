package net.aonsolutions.aon.sii;

import com.esferalia.aon.occam.api.model.type.DocumentType;

public enum IDType {
	
	/**
	 * NIF-IVA
	 */
	NIF_IVA("02"),
	
	/**
	 * Pasaporte
	 */
	PASAPORTE("03"),
	
	/**
	 * Documento oficial de identificación expedido por el país o territorio de residencia
	 */
	DOCUMENTO_OFICIAL_PAIS("04"),
	
	/**
	 * Certificado de residencia
	 */
	CERTIFICADO_RESIDENCIA("05"),
	
	/**
	 * Otro documento probatorio
	 */
	
	OTRO("06"),
	
	/**
	 * NIF no censado
	 */
	NO_CENSADO("07")
	;
	
	String name;
	private IDType(String name) {
		this.name = name;				
	}
	
	public String getName() {
		return name;
	}
	
	public static IDType valueOf(DocumentType documentType) {
		if(DocumentType.PASSPORT.equals(documentType)) return PASAPORTE;
	
		return OTRO;
	}
	
	public static IDType safeValueOf(String str) {
		for (IDType t : IDType.values()) {
			if(t.getName().equalsIgnoreCase(str)) 
				return t;
		}
		return OTRO;
	}
	
}
