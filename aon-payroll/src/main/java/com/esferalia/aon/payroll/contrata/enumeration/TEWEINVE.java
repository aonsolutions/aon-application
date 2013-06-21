package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TEWEINVE table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TEWEINVE	TIPO DE EMPLEADOR INVESTIGACIÓN					10-01-2012			
 *  ------------------------------------------------------------------------
 */ 
public enum TEWEINVE {

	TEWEINVE_1( "1", "ORGANISMO PÚBLICO", null, null ),
	TEWEINVE_2( "2", "INSTITUCIÓN SIN ÁNIMO DE LUCRO", null, null ),
	TEWEINVE_3( "3", "UNIVERSIDAD PÚBLICA", null, null ),
	TEWEINVE_4( "4", "ORGANISMO PÚBLICO DE INVESTIGACIÓN DE LA ADMINISTRACIÓN GENERAL DEL ESTADO", null, null ),
	TEWEINVE_5( "5", "ORGANISMO DE INVESTIGACIÓN DE OTRAS ADMINISTRACIONES PÚBLICAS", null, null ),
	TEWEINVE_6( "6", "UNIVERSIDADES PRIVADAS Y UNIVERSIDADES DE LA IGLESIA CATÓLICA QUE PERCIBAN FONDOS PARA CONTRATAR PERSONAL INVESTIGADOR", null, null ),
	TEWEINVE_7( "7", "ENTIDADES PRIVADAS SIN ÁNIMO DE LUCRO QUE REALICEN ACTIVIDADES DE I+D SEGÚN D.A. 1ª LEY 14/2011", null, null ),
	TEWEINVE_8( "8", "CONSORCIOS PÚBLICOS Y FUNDACIONES DEL SECTOR PÚBLICO SEGÚN D.A. 1ª LEY 14/2011", null, null ),
	TEWEINVE_9( "9", "OTROS ORGANISMOS DE INVESTIGACIÓN DE LA AGE CUANDO REALICEN ACTIVIDAD INVESTIGADORA", null, null ),
	;
	public static final String TABLE_NAME = "TEWEINVE";
	public static final String TABLE_DESCRIPTION = " TEWEINVE	TIPO DE EMPLEADOR INVESTIGACIÓN					10-01-2012			";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TEWEINVE( String code, String description, String startDate, String endDate ) {
		this.code = code;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public Date getStartDate(){
		try {
			if(startDate!=null){
				return sdf.parse(startDate);
			}
		} catch (ParseException e) {
			// nothing to do
		}
		return null;
	}

	public Date getEndDate(){
		try {
			if(endDate!=null){
				return sdf.parse(endDate);
			}
		} catch (ParseException e) {
			// nothing to do
		}
	return null;
	}

	public static TEWEINVE getEnumByValue(String expression) {
		for( TEWEINVE o : TEWEINVE.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}