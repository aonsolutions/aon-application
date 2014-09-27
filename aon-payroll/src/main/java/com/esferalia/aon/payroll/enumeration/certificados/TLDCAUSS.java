package com.esferalia.aon.payroll.enumeration.certificados;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.enumeration.IPayrollTablesEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) TLDCAUSS table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum TLDCAUSS implements IPayrollTablesEnum {

	TLDCAUSS_01( "01", "DESPIDO DEL TRABAJADOR", null, null ),
	TLDCAUSS_02( "02", "DESPIDO POR CAUSAS OBJETIVAS,AMORTIZACION POR CAUSAS ECONÖMICAS,TÈCNICAS,ORGANIZATIVAS O DE PRODUCCIÖN", null, null ),
	TLDCAUSS_03( "03", "MUERTE DEL EMPRESARIO", null, null ),
	TLDCAUSS_04( "04", "JUBILACION DEL EMPRESARIO", null, null ),
	TLDCAUSS_05( "05", "INCAPACIDAD DEL EMPRESARIO", null, null ),
	TLDCAUSS_06( "06", "CESE POR DECLARACION DE INVALIDEZ PERMANENTE TOTAL DEL TRABAJADOR", null, null ),
	TLDCAUSS_07( "07", "CESE EN PERIODO PRUEBA A INSTANCIA DEL EMPRESARIO", null, null ),
	TLDCAUSS_08( "08", "CESE EN PERIODO PRUEBA POR ACUERDO DEL CONSEJO RECTOR EN EL SUPUESTO DE SOCIOS DE COOPERATIVAS", null, null ),
	TLDCAUSS_09( "09", "CESE EN PERIODO PRUEBA A INSTANCIA DEL TRABAJADOR", null, null ),
	TLDCAUSS_10( "10", "CESE POR VOLUNTAD DEL EMPRESARIO EN LA RELACION LABORAL DE ALTA", null, null ),
	TLDCAUSS_11( "11", "FIN DE CONTRATO TEMPORAL", null, null ),
	TLDCAUSS_12( "12", "FIN DE CONTRATO TEMPORAL A INSTANCIA DEL TRABAJADOR (RECHAZO PRORROGA)", null, null ),
	TLDCAUSS_13( "13", "FIN DE LA RELACION ADMINISTRATIVA TEMPORAL DE FUNCIONARIOS DE EMPLEO Y CONTRATADOS ADMINISTRATIVOS", null, null ),
	TLDCAUSS_14( "14", "RESOLUCION DEL TRABAJADOR POR TRASLADO", null, null ),
	TLDCAUSS_15( "15", "FIN O INTERRUPICION DE LA ACTIVIDAD DE LOS TRABAJADORES FIJOS-DISCONTINUOS", null, null ),
	TLDCAUSS_16( "16", "DESPIDO COLECTIVO O EXTINCION DEL CONTRATO POR ERE", null, null ),
	TLDCAUSS_17( "17", "SUSPENSION DEL CONTRATO O ERE", null, null ),
	TLDCAUSS_18( "18", "REDUCCION TEMPORAL DE JORNADA O ERE", null, null ),
	TLDCAUSS_19( "19", "SUSPENSIÓN VOLUNTARIA DE LA RELACION LABORAL.VICTIMAS DE VIOLENCIA DE GÉNERO", null, null ),
	TLDCAUSS_20( "20", "EXPULSION DEL SOCIO DE LA COOPERATIVA, POR ACUERDO DEL CONSEJO RECTOR", null, null ),
	TLDCAUSS_21( "21", "BAJA VOLUNTARIA DEL TRABAJADOR", null, null ),
	TLDCAUSS_22( "22", "FINALIZACION O RESOLUCION INVOLUNTARIA DEL COMPROMISO CON LAS FUERZAS(INDICAR ARMADAS.CON O SIN DERECHO A PENSION DE RETIRO)", null, null ),
	TLDCAUSS_23( "23", "FIN DE ACTUACION CON FINALIZACION DE CONTRATO, EN EL CASO DE ARTISTAS", null, null ),
	TLDCAUSS_24( "24", "FIN DE LA ACTIVIDAD FIJA DISCONTINUA POR LA REALIZACION DE TRABAJOS FIJOS Y PERIODICOS QUE SE REPITEN EN FECHAS CIERTAS", null, null ),
	TLDCAUSS_25( "25", "FINALIZACION DEL VINCULO SOCIETARIO DE DURACION DETERMINADA, FIJADO EN EL ACUERDO DE ADMISION Y EN LOS ESTATUTOS DE LA COOPERATVIA", null, null ),
	TLDCAUSS_26( "26", "EXCEDENCIA", null, null ),
	TLDCAUSS_27( "27", "CESE INVOLUNTARIO Y CON CARACTER DEFINITIVO EN CARGO PUBLICO O SINDICAL", null, null ),
	TLDCAUSS_28( "28", "PERDIDA CON CARACTER INVOLUNTARIO Y DEFINITIVO DE LA DEDICACION EXCLUSIVA O PARCIAL POR PARTE DE UN CARGO PUBLICO O SINDICAL", null, null ),
	TLDCAUSS_29( "29", "CONCLUSIÓN DEL SERVICIO O DEL TIEMPO MAXIMO COMO RESERVISTA VOLUNTARIO ACTIVADO EN LAS FUERZAS ARMADAS", null, null ),
	TLDCAUSS_30( "30", "DESPIDO POR CAUSAS OBJETIVAS.INEPTITUD,FALTA DE ADAPTACION Y ASISTENCIA AL TRABAJO", null, null ),
	TLDCAUSS_31( "31", "RESOLUCIÓN DEL TRABAJADOR POR MODIFICACIÓN SUSTANCIAL DE LAS CONDICIONES DE TRABAJO", null, null ),
	TLDCAUSS_32( "32", "EXTINCIÓN VOLUNTARIA DE LA RELACIÓN LABORAL.VICTIMAS DE LA VIOLENCIA DE GÉNERO", null, null ),
	TLDCAUSS_33( "33", "RESOLUCIÓN DEL TRABAJADOR POR  CAUSA JUSTA", null, null ),
	;
	public static final String TABLE_NAME = "TLDCAUSS";
	public static final String TABLE_DESCRIPTION = "No description found";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TLDCAUSS( String code, String description, String startDate, String endDate ) {
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
				return DateUtils.ceiling(sdf.parse(startDate), Calendar.DAY_OF_MONTH);
			}
		} catch (ParseException e) {
			// nothing to do
		}
		return null;
	}

	public Date getEndDate(){
		try {
			if(endDate!=null){
				return DateUtils.ceiling(sdf.parse(endDate), Calendar.DAY_OF_MONTH);
			}
		} catch (ParseException e) {
			// nothing to do
		}
	return null;
	}

	public boolean isActive(){
		Date now = new Date();
		now = DateUtils.ceiling(now, Calendar.DAY_OF_MONTH);
		if( (getStartDate()!=null && getStartDate().after(now)) || (getEndDate()!=null && getEndDate().before(now)) ){
			return false;
		}
		return true;
	}

	public static TLDCAUSS getEnumByValue(String expression) {
		for( TLDCAUSS o : TLDCAUSS.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}