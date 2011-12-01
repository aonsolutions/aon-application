package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TBONVFOR table codes.
*/ 
public enum TBONVFOR implements IStringEnum {

	TBONVFOR_11( "11", "ESTUDIOS PRIMARIOS INCOMPLETOS", null, null ),
	TBONVFOR_22( "22", "PRIMERA ETAPA DE EDUCACIÓN SECUNDARIA SIN TÍTULO DE GRADUADO ESCOLAR O EQUIVALENTE", null, null ),
	TBONVFOR_23( "23", "PRIMERA ETAPA DE EDUCACIÓN SECUNDARIA CON TÍTULO DE GRADUADO ESCOLAR O EQUIVALENTE", null, null ),
	TBONVFOR_32( "32", "ENSEÑANZAS DE BACHILLERATO", null, null ),
	TBONVFOR_33( "33", "ENSEÑANZAS DE GRADO MEDIO DE FORMACIÓN PROFESIONAL ESPECÍFICA, ARTES PLÁSTICAS, DISEÑO Y DEPORTIVAS", null, null ),
	TBONVFOR_51( "51", "ENSEÑANZAS DE GRADO SUPERIOR DE FORMACIÓN PROFESIONAL ESPECÍFICA Y EQUIVALENTE, ARTES PLÁSTICAS, DISEÑO Y DEPORTIVAS", null, null ),
	TBONVFOR_54( "54", "ENSEÑANZAS UNIVERSITARIAS DE PRIMER CICLO Y EQUIVALENTES O PERSONAS QUE HAN APROBADO 3 CURSOS COMPLETOS DE UNA LICENCIATURA O CRÉDITOS EQUIVALENTES (DIPLOMADOS)", null, null ),
	TBONVFOR_55( "55", "ENSEÑANZAS UNIVERSITARIAS DE SEGUNDO CICLO Y EQUIVALENTES (LICENCIADOS)", null, null ),
	TBONVFOR_59( "59", "ENSEÑANZAS UNIVERSITARIAS DE GRADO", null, null ),
	TBONVFOR_60( "60", "ENSEÑANZAS UNIVERSITARIAS DE MÁSTER", null, null ),
	TBONVFOR_61( "61", "DOCTORADO UNIVERSITARIO", null, null ),
	TBONVFOR_80( "80", "SIN ESTUDIOS", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TBONVFOR( String value, String label, String startDate, String endDate ) {
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