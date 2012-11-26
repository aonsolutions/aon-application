package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TAUCOMAU table codes.
*/ 
public enum TAUCOMAU implements IStringEnum {

	TAUCOMAU_01( "01", "ANDALUCIA", null, null ),
	TAUCOMAU_02( "02", "ARAGON", null, null ),
	TAUCOMAU_03( "03", "ASTURIAS", null, null ),
	TAUCOMAU_04( "04", "BALEARES", null, null ),
	TAUCOMAU_05( "05", "CANARIAS", null, null ),
	TAUCOMAU_06( "06", "CANTABRIA", null, null ),
	TAUCOMAU_07( "07", "CASTILLA LA MANCHA", null, null ),
	TAUCOMAU_08( "08", "CASTILLA LEON", null, null ),
	TAUCOMAU_09( "09", "CATALUÑA", null, null ),
	TAUCOMAU_10( "10", "COMUNIDAD VALENCIANA", null, null ),
	TAUCOMAU_11( "11", "EXTREMADURA", null, null ),
	TAUCOMAU_12( "12", "GALICIA", null, null ),
	TAUCOMAU_13( "13", "MADRID", null, null ),
	TAUCOMAU_14( "14", "MURCIA", null, null ),
	TAUCOMAU_15( "15", "NAVARRA", null, null ),
	TAUCOMAU_16( "16", "PAIS VASCO", null, null ),
	TAUCOMAU_17( "17", "LA RIOJA", null, null ),
	TAUCOMAU_18( "18", "CEUTA", null, null ),
	TAUCOMAU_19( "19", "MELILLA", null, null ),
	TAUCOMAU_99( "99", "ESTADO ESPAÑOL", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TAUCOMAU( String value, String label, String startDate, String endDate ) {
		this.value = value;
		this.label = label;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}
}