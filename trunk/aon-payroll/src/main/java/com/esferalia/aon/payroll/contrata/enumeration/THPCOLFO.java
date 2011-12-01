package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) THPCOLFO table codes.
*/ 
public enum THPCOLFO implements IStringEnum {

	THPCOLFO_01( "01", "DESEMPLEADO MINUSVALIDO", "20010304", "0" ),
	THPCOLFO_02( "02", "EXTRANJERO 2 PROS.AÑOS.PERMISO TRABAJO", "20010304", "20060614" ),
	THPCOLFO_03( "03", "DESEMPLEADO + 3 AÑOS SIN ACTIV. LABORAL", "20010304", "20060614" ),
	THPCOLFO_04( "04", "DESEMPLEADO EN SITUACION DE EXCL.SOCIAL", "20010304", "20060614" ),
	THPCOLFO_05( "05", "CONTRATOS ESC.TALLER, C.OFICIO,T.EMPLEO", "20010304", "20060614" ),
	THPCOLFO_06( "06", "CONTRATOS ESCUELA TALLER Y CASAS OFICIO", "20060701", "0" ),
	THPCOLFO_07( "07", "CONTRATOS TALLERES DE EMPLEO", "20060701", "0" ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	THPCOLFO( String value, String label, String startDate, String endDate ) {
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