package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TEVACTCL table codes.
*/ 
public enum TEVACTCL implements IStringEnum {

	TEVACTCL_A( "A", "ACTUACIONES ORDINARIAS", null, null ),
	TEVACTCL_B( "B", "ACTUACIONES EXTRAORDINARIAS, PIEC", null, null ),
	TEVACTCL_C( "C", "COMPLEMENTOS RENTAS (AEPSA)", null, null ),
	TEVACTCL_D( "D", "AEPSA (GENERADORES EMPLEO, ZRD, ARAGON)", null, null ),
	TEVACTCL_E( "E", "ACTUAC. ESPEC (SERV.INTEGR. EMPLEO)", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TEVACTCL( String value, String label, String startDate, String endDate ) {
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

	public static TEVACTCL getEnumByValue(String expression) {
		for( TEVACTCL o : TEVACTCL.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}