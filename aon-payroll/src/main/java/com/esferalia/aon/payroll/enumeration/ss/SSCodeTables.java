package com.esferalia.aon.payroll.enumeration.ss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.esferalia.aon.payroll.enumeration.IPayrollTablesEnum;

/** 
 * Enumeration for represent SOCIAL SECURITY - Sistema RED table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.SSCodeTablesWriter
 */ 
public enum SSCodeTables implements IPayrollTablesEnum {

	T_T1( "T-1", "Indicador de prueba", null ),
	T_T2( "T-2", "Régimen / Sector", null ),
	T_T3( "T-3", "Tipo de identificación de empresario", null ),
	T_T5( "T-5", "Calificador de liquidación", null ),
	T_T6( "T-6", "Clase de liquidación", null ),
	T_T7( "T-7", "Acción", null ),
	T_T8( "T-8", "Tipo alfabético de empresario", null ),
	T_T9( "T-9", "Tipo de explotación", null ),
	T_T10( "T-10", "Clave de entidad de AT y EP", null ),
	T_T11( "T-11", "Tipo de identificador de persona física", null ),
	T_T12( "T-12", "Nacionalidad / País", null ),
	T_T13( "T-13", "Indicadores trabajador", null ),
	T_T14( "T-14", "Tipo de domicilio", null ),
	T_T15( "T-15", "Tipo de vía", null ),
	T_T16_1( "T-16", "Indicador de Huelga", null ),
	T_T16_2( "T-16", "Indicador de Pluriempleo", null ),
	T_T16_3( "T-16", "Indicador de Reducción de jornada", null ),
	T_T16_4( "T-16", "Indicador de Modalidad de salario", null ),
	T_T16_5( "T-16", "Indicador de Alta sin retribución", null ),
	T_T16_6( "T-16", "Otros indicadores", null ),
	T_T16_7( "T-16", "Indicador de contratación inferior a 7 días", null ),
	T_T16_8( "T-16", "Indicador de vacaciones", null ),
	T_T16_9( "T-16", "Indicador de jubilación", null ),
	T_T17( "T-17", "Clave de días/jornadas específicas de colectivo", null ),
	T_T18( "T-18", "Grupo de cotización", null ),
	T_T19( "T-19", "Clave de contrato de trabajo", null ),
	T_T20( "T-20", "Epígrafe de AT y EP", null ),
	T_T21( "T-21", "Situación", null ),
	T_T24( "T-24", "Tipo de elemento de datos", null ),
	T_T25_Y_26( "T-25 y 26", "Clave (según tipo)", null ),
	T_T27( "T-27", "Tipo de resolución", null ),
	T_T28( "T-28", "Modalidad de pago", null ),
	T_T29( "T-29", "Tipo de expediente", null ),
	T_T30( "T-30", "Tipo de elemento de datos", null ),
	T_T31_Y_T32( "T-31 y T-32", "Clave (según tipo)", null ),
	T_T33( "T-33", "Calificador de clave", null ),
	T_T34( "T-34", "Acción (INSS)", null ),
	T_T35( "T-35", "Contingencia (INSS)", null ),
	T_T36( "T-36", "Causa del alta (INSS)", null ),
	T_T37( "T-37", "Condición de desempleado", null ),
	T_T38( "T-38", "Relación Laboral de Carácter Especial", null ),
	T_T39( "T-39", "Causa de sustitución", null ),
	T_T40( "T-40", "Lugar de nacimiento", null ),
	T_T41( "T-41", "Tipos de inactividad", null ),
	T_T42( "T-42", "Autorización para trabajar FUTURO USO", null ),
	T_T43( "T-43", "Número de colegiado convencional", null ),
	T_T44( "T-44", "Solicitud Modo de Pago", null ),
	T_T45( "T-45", "Causa Exclusión Censo Agrario BAJA", null ),
	T_T46( "T-46", "Tipo de situación adicional de afiliación", null ),
	T_T47( "T-47", "Tipo de subcontratación o cesión", null ),
	T_T49( "T-49", "Tipo de peculiaridad", null ),
	T_T50( "T-50", "Fracción-Cuota", null ),
	T_T51( "T-51", "Categoría profesional", null ),
	T_T52( "T-52", "Tipo de Relación Laboral", null ),
	T_T53( "T-53", "Indicadores de reducción de bonificación T.Parciales", null ),
	T_T54( "T-54", "Colectivo de peculiaridad de cotización", null ),
	T_T55( "T-55", "Indicadores de Discapacidad", null ),
	T_T56( "T-56", "Indicadores de Relación Laboral", null ),
	T_T57( "T-57", "Información Complementaria", null ),
	T_T58( "T-58", "Ocupación", null ),
	T_T59( "T-59", "CNAE93", null ),
	T_T60( "T-60", "Convenio Colectivo - BAJA", null ),
	T_T61( "T-61", "Colectivo de trabajador", null ),
	T_T62( "T-62", "Provincia", null ),
	T_T63( "T-63", "CNAE09", null ),
	T_T64( "T-64", "Indicadores de modalidad de cotización", null ),
	T_T65( "T-65", "Acontecimientos extraordinarios", null ),
	T_T66( "T-66", "Sector industrial incentivado", null ),
	T_T67( "T-67", "Colectivo Especial CCC", null ),
	T_T68( "T-68", "Indicativo pérdida de beneficios (trabajador)", null ),
	T_T69( "T-69", "Indicativo pérdida de beneficios (empresa)", null ),
	T_T70( "T-70", "Efectos pérdida de beneficios (empresa)", null ),
	T_T71( "T-71", "Causa de suspensión", null ),
	T_T72( "T-72", "Tipo de discapacidad", null ),
	T_T73( "T-73", "Cambio de puesto de trabajo", null ),
	T_T74( "T-74", "Excedente sector industrial incentivado", null ),
	T_T75( "T-75", "Mujer reincorporada", null ),
	T_T76( "T-76", "Tipo de prestación", null ),
	T_T77( "T-77", "Forma de pago", null ),
	T_T78( "T-78", "Motivo de la solicitud MA/PA", null ),
	T_T79( "T-79", "Convenio colectivo", null ),
	T_T80( "T-80", "Legislación", null ),
	T_T81( "T-81", "Tipo de embarcación", null ),
	T_T82( "T-82", "Colectivo incentivado", null ),
	T_T83( "T-83", "Exclusión social/víctimas", null ),
	T_T84( "T-84", "Concepto retributivo", null ),
	T_T85( "T-85", "Tipo de reducción de jornada", null ),
	T_T86( "T-86", "Proveedor de nómina", null ),
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