package net.aonsolutions.aon.verifactu;

import com.esferalia.aon.occam.api.model.type.DocumentType;

public enum VerifactuIDType {
	
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
	private VerifactuIDType(String name) {
		this.name = name;				
	}
	
	public String getName() {
		return name;
	}
	
	public static VerifactuIDType valueOf(DocumentType documentType) {
		if(DocumentType.PASSPORT.equals(documentType)) return PASAPORTE;
		if(DocumentType.NOT_CENSUSED.equals(documentType)) return NO_CENSADO;
		return OTRO;
	}
	
	public static VerifactuIDType safeValueOf(String str) {
		for (VerifactuIDType t : VerifactuIDType.values()) {
			if(t.getName().equalsIgnoreCase(str)) 
				return t;
		}
		return OTRO;
	}
	
}
