package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TEOCOLDE table codes.
*/ 
public enum TEOCOLDE implements IStringEnum {

	TEOCOLDE_01( "01", "JOVENES DE 18 A 30 AÑOS", "19970517", "20010303" ),
	TEOCOLDE_02( "02", "MAYORES DE 45 AÑOS", "19970517", "20120211" ),
	TEOCOLDE_03( "03", "DESPEMPLE. INSCRITOS OE 12 O MAS MESES", "19970517", "20010303" ),
	TEOCOLDE_04( "04", "MINUSVALIDOS", "19970517", "20120211" ),
	TEOCOLDE_05( "05", "NO ACOGIDOS A LA LEY", "19970517", "20010622" ),
	TEOCOLDE_06( "06", "JOVENES DE 16-30 AÑOS AMBOS INCLUSIVE", "20010304", "20120211" ),
	TEOCOLDE_07( "07", "MUJERES DESEMP. OCUP < INDICE EMPLEO", "20010304", "20100918" ),
	TEOCOLDE_08( "08", "DESEMPLEADOS INSC. OE 6 O MAS MESES", "20010304", "20100617" ),
	TEOCOLDE_09( "09", "CONVERSION A INDEFINIDO ACOGIDO DESPIDO", "20010304", "20010622" ),
	TEOCOLDE_10( "10", "DESEMPLEADOS INSCRITOS OE 3 O MAS MESES", "20100618", "20100918" ),
	TEOCOLDE_11( "11", "DESEMPLEADOS SOLO CON CONTRATO TEMPORAL 2 ÚLTIMOS AÑOS", "20100618", "20120211" ),
	TEOCOLDE_12( "12", "DESEMPLEADOS CON CTO.INDEFINIDO EXTINGUIDO 2 ÚLTIMOS AÑOS", "20100618", "20120211" ),
	TEOCOLDE_13( "13", "CONVERSIÓN CON DESPIDO, HASTA 31-12-2010", "20100618", "20101231" ),
	TEOCOLDE_14( "14", "CONVERSIÓN CON DESPIDO, HASTA 31-12-2011", "20100618", "20110830" ),
	TEOCOLDE_15( "15", "DESEMPLEADOS INSCRITOS EN O.E. UNO O MÁS MESES", "20100919", "20120211" ),
	TEOCOLDE_16( "16", "MUJERES DESEMPLEADAS art.3.2a)Ley 35/2010", "20100919", "20120211" ),
	TEOCOLDE_17( "17", "CONTRATO ORIGEN CON REDUCCIÓN RDL 1/2011", "20110213", "20120212" ),
	TEOCOLDE_18( "18", "CONTRATO TEMPORAL INICIADO HASTA 28-08-2011", "20110831", "20111231" ),
	TEOCOLDE_19( "19", "CONTRATO TEMPORAL INICIADO DESDE 28-08-2011", "20110831", "20120211" ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TEOCOLDE( String value, String label, String startDate, String endDate ) {
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

	public static TEOCOLDE getEnumByValue(String expression) {
		for( TEOCOLDE o : TEOCOLDE.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}