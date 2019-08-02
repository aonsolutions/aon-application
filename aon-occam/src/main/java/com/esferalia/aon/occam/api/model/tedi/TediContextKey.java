package com.esferalia.aon.occam.api.model.tedi;

import java.io.Serializable;

public enum TediContextKey implements Serializable {
	// Á --> \u00C1 á --> \u00E1 
	// É --> \u00C9 é --> \u00E9 
	// Í --> \u00CD í --> \u00ED 
	// Ó --> \u00D3 ó --> \u00F3 
	// Ú --> \u00DA ú --> \u00FA ... acento
	// Ü --> \u00DC ü --> \u00fc ... diéresis
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00BA ª --> \u00AA 
	// ¿ --> \u00BF
	
	DOMAIN("Dominio"),
	TYPE("Tipo de factura"), 
	SERIES("Serie"),
	NUMBER("N\u00FAmero"),
	REFERENCE_CODE("N\u00BA factura"),
	TRANSACTION("Tipo de transacci\u00F3n"),
	ISSUE_DATE ("Fecha de factura"),
	TAX_DATE ("Fecha de IVA"),
	SCOPE("\u00C1mbito"),
	REGISTRY ("Titular de la factura"),
	RDOCUMENT ("N\u00BA documento del titular"),
	RDOCUMENT_COUNTRY("Pa\u00CDs del documento del titular"),
	RNAME("Raz\u00F3n social del titular"),
	ADDRESS("Direcci\u00F3n del titular"),
	DETAIL_DESCRIPTION("Descripci\u00F3n de la l\u00EDnea")
	
	;

	private String description;

	private TediContextKey() {
	}

	private TediContextKey(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}