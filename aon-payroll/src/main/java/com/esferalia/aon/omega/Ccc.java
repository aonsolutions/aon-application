package com.esferalia.aon.omega;

public class Ccc {

	Integer ccc;		// CCC Id (se rellena en el proceso)
	
	String cccCode;		// Valor ccc longuitug 11
	byte cccType;		// Tipo ccc 
	String cccGeozone;	// Zona geografica ej: 01 -> ALAVA
	
	protected Ccc() {
		super();
	}

	public Integer getCcc() {
		return ccc;
	}

	public Ccc setCcc(Integer ccc) {
		this.ccc = ccc;
		return this;
	}

	public String getCccCode() {
		return cccCode;
	}

	public Ccc setCccCode(String cccCode) {
		this.cccCode = cccCode;
		return this;
	}

	public byte getCccType() {
		return cccType;
	}

	public Ccc setCccType(byte cccType) {
		this.cccType = cccType;
		return this;
	}

	public String getCccGeozone() {
		return cccGeozone;
	}

	public Ccc setCccGeozone(String cccGeozone) {
		this.cccGeozone = cccGeozone;
		return this;
	}
	
}
