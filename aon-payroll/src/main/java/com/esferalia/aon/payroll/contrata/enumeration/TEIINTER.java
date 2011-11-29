package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TEIINTER table codes.
*/ 
public enum TEIINTER implements IStringEnum {

	TEIINTER_A( "A", "TRABAJADOR CON DERECHO RESERVA DE PUESTO", "19800315", "0" ),
	TEIINTER_B( "B", "TRABAJADOR POR MATERNIDAD SIN BONIFICACION DE CUOTAS", "19800315", "0" ),
	TEIINTER_C( "C", "EXCEDENCIA CUIDADO HIJO PERCEPTOR PRESTA", "19950501", "19991106" ),
	TEIINTER_D( "D", "EXCEDENCIA CUIDADO HIJO NO PERCEP.PRESTA", "19950501", "19970516" ),
	TEIINTER_E( "E", "TRABAJADOR PROCESO DE SELECCION/PROMOCION", "19800315", "0" ),
	TEIINTER_F( "F", "MATERNIDAD CON BONIFICACION DE CUOTAS", "19980906", "0" ),
	TEIINTER_G( "G", "ADOPCION", "19980906", "0" ),
	TEIINTER_H( "H", "ACOGIMIENTO", "19980906", "0" ),
	TEIINTER_I( "I", "RIESGO DURANTE EMBARAZO", "19991107", "0" ),
	TEIINTER_J( "J", "TRABAJ.EN FORMACION POR PERCEPTOR PRESTA", "20020526", "0" ),
	TEIINTER_K( "K", "MINUSVALIDOS DESEMPLEADOS POR MINUSV.INCAP.TEMP", "20021214", "0" ),
	TEIINTER_L( "L", "EXCEDENCIA CUIDADO FAMILIAR PERCEP.PREST", "19991107", "0" ),
	TEIINTER_M( "M", "SUSTITUCIÓN VÍCTIMAS VIOLENCIA DE GÉNERO", "20050128", "0" ),
	TEIINTER_N( "N", "PATERNIDAD", "20070324", "0" ),
	TEIINTER_O( "O", "RIESGO DURANTE LA LACTANCIA NATURAL", "20070324", "0" ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TEIINTER( String value, String label, String startDate, String endDate ) {
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