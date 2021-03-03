package net.aonsolutions.aon.tbai._enums;

public enum IDtype {
	NIF_IVA("02"),
	PASSPORT("03"),
	IDENTIFICATION_DOCUMENT_ISSUED_BY_RESIDENCE_COUNTRY("04"),
	RESIDENCE_CERTIFICATE("05"),
	OTHER("06");

	private String code;
	IDtype(final String code) {this.code = code;}
	public String getCode() {return code;}
}
