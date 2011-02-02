package com.code.aon.facturae.enumeration;

import com.code.aon.config.enumeration.TaxType;

public enum TaxTypeCode {

	IVA("01", TaxType.VAT),
	IPSI("02"),
	IGIC("03", TaxType.IGIC),
	Otro("05"),
	ITPAJD("06"),
	IE("07"),
	Ra("08"),
	IGTECM("09"),
	IECDPCAC("10"),
	IIIMAB("11"),
	ICIO("12"),
	MVDN("13"),
	IMSN("14"),
	IMGSN("15"),
	IMPN("16");

	private String value;
	
	private TaxType taxType;
	
	TaxTypeCode( String value ) {
		this( value, null );
	}

	TaxTypeCode( String value, TaxType taxType ) {
		this.value = value;
		this.taxType = taxType;
	}
	
	public String getValue() {
		return value;
	}

	public TaxType getTaxType() {
		return taxType;
	}
	
	public static TaxTypeCode getTaxTypeCode( TaxType taxType ) {
		for( TaxTypeCode ttc : values() ) {
			if ( taxType == ttc.getTaxType() ) {
				return ttc;
			}
		}
		return null;
	}
	
}
