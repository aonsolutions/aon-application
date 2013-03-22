package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TEWEINVE table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TEWEINVE	TIPO DE EMPLEADOR INVESTIGACIÓN					10-01-2012			
 *  ------------------------------------------------------------------------
 */ 
public enum TEWEINVE implements IStringEnum {

	TEWEINVE_1( "1", "ORGANISMO PÚBLICO", null, null ),
	TEWEINVE_2( "2", "INSTITUCIÓN SIN ÁNIMO DE LUCRO", null, null ),
	TEWEINVE_3( "3", "UNIVERSIDAD PÚBLICA", null, null ),
	TEWEINVE_4( "4", "ORGANISMO PÚBLICO DE INVESTIGACIÓN DE LA ADMINISTRACIÓN GENERAL DEL ESTADO", null, null ),
	TEWEINVE_5( "5", "ORGANISMO DE INVESTIGACIÓN DE OTRAS ADMINISTRACIONES PÚBLICAS", null, null ),
	TEWEINVE_6( "6", "UNIVERSIDADES PRIVADAS Y UNIVERSIDADES DE LA IGLESIA CATÓLICA QUE PERCIBAN FONDO...", null, null ),
	TEWEINVE_7( "7", "ENTIDADES PRIVADAS SIN ÁNIMO DE LUCRO QUE REALICEN ACTIVIDADES DE I+D SEGÚN D.A....", null, null ),
	TEWEINVE_8( "8", "CONSORCIOS PÚBLICOS Y FUNDACIONES DEL SECTOR PÚBLICO SEGÚN D.A. 1ª LEY 14/2011", null, null ),
	TEWEINVE_9( "9", "OTROS ORGANISMOS DE INVESTIGACIÓN DE LA AGE CUANDO REALICEN ACTIVIDAD INVESTIGAD...", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TEWEINVE( String value, String label, String startDate, String endDate ) {
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

	public static TEWEINVE getEnumByValue(String expression) {
		for( TEWEINVE o : TEWEINVE.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}