package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TETPGMEM table codes.
*/ 
public enum TETPGMEM implements IStringEnum {

	TETPGMEM_01( "01", "FOMENTO EMPLEO AGRARIO", null, null ),
	TETPGMEM_02( "02", "INSERCIÓN CORPORACIÓN LOCAL", null, null ),
	TETPGMEM_03( "03", "INSERCIÓN (ÓRGANOS ADMINISTRAC. ESTADO)", null, null ),
	TETPGMEM_04( "04", "INSERCIÓN (COMUNIDAD AUTÓNOMA)", null, null ),
	TETPGMEM_05( "05", "INSERCIÓN (ENTIDAD SIN ANIMO DE LUCRO)", null, null ),
	TETPGMEM_06( "06", "INSERCIÓN (UNIVERSIDAD)", null, null ),
	TETPGMEM_07( "07", "SUBSIDIO AGRARIO(ORGANISMOS INVERSORES)", null, null ),
	TETPGMEM_08( "08", "AGENTES DE EMPLEO Y DESARROLLO LOCAL", null, null ),
	TETPGMEM_09( "09", "ESTUDIOS Y CAMPAÑAS", null, null ),
	TETPGMEM_10( "10", "PROGRAMA DE EMPLEO I+E", null, null ),
	TETPGMEM_12( "12", "INTERES SOCIAL (CORPORACION LOCAL)", null, null ),
	TETPGMEM_13( "13", "INTERES SOCIAL (ORGANOS AD.ESTADO O CCAA)", null, null ),
	TETPGMEM_14( "14", "INTERES SOCIAL (COMUNIDAD AUTONOMA)", null, null ),
	TETPGMEM_15( "15", "INTERES SOCIAL (ENTIDAD SIN ANIMO DE LUCRO)", null, null ),
	TETPGMEM_16( "16", "INTERES SOCIAL (UNIVERSIDAD)", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TETPGMEM( String value, String label, String startDate, String endDate ) {
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