package com.esferalia.aon.payroll.enumeration.ss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;
/** 
 * Enumeration for represent SOCIAL SECURITY table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.SSCodeTablesWriter
 */ 
public enum SSCodeTables implements ISSEnum {

	T_T01( "T01", "Indicador de prueba",null),
	T_T05( "T05", "Calificados de liquidación",null),
	T_T06( "T06", "Clase de liquidación",null),
	T_T07( "T07", "Acción",null),
	T_T10( "T10", "Clave de entidad de AT y EP",null),
	T_T18( "T18", "Grupo de cotización",null),
	T_T21( "T21", "Situación",null),
	T_T37( "T37", "Condición de desempleado",null),
	T_T41( "T41", "Tipos de inactividad",null),
	T_T49( "T49", "Tipo de peculiaridad de cotización",null),
	T_T50( "T50", "Fracción-Cuota",null),
	T_T54( "T54", "Colectivo de peculiaridad de cotización",null),
	T_T58( "T58", "Ocupación",null),
	T_T61( "T61", "Colectivo de trabajador",null),
	T_T68( "T68", "Indicativo pérdida de beneficios (trabajador)",null),
	T_T83( "T83", "Exclusión social/Víctimas",null),
	T_T84( "T84", "Concepto retributivo",null),
	;
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	private String code;
	private String description;
	private String lastUpdateDate;

	SSCodeTables( String code, String description, String lastUpdateDate) {
		this.code = code;
		this.description = description;
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public Date getLastUpdateDate(){
		try {
			if(lastUpdateDate!=null){
				return sdf.parse(lastUpdateDate);
			}
		} catch (ParseException e) {
			// nothing to do
		}
	return null;
	}

	public boolean isActive(){
		return true;
	}

	public static SSCodeTables getEnumByValue(String expression) {
		for( SSCodeTables o : SSCodeTables.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}