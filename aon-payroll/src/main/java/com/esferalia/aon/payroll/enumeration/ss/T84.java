package com.esferalia.aon.payroll.enumeration.ss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.enumeration.IPayrollTablesEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent SOCIAL SECURITY T84 table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.SSCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * T84.txt
 *  ------------------------------------------------------------------------
 */ 
public enum T84 implements IPayrollTablesEnum {

	T84_0001( "0001", "RETRIBUCION NO INCLUIDA OTROS APARTADOS", null, null ),
	T84_0002( "0002", "HORAS EXTRAORDINARIAS NO ESTRUCTURALES", null, null ),
	T84_0003( "0003", "HORAS EXTR. ESTRUCTURALES O FUERZA MAYOR", null, null ),
	T84_0004( "0004", "PAGAS EXTRAORDINARIAS.PRORRATEO", null, null ),
	T84_0005( "0005", "RETR<>PAGA.EXTR.VENCIM.SUP.MES.PRORRATEO", null, null ),
	T84_0006( "0006", "VACACIONES RETRIBUIDAS NO DISFRUTADAS", null, null ),
	T84_0007( "0007", "SALARIOS DE TRAMITACIÓN", null, null ),
	T84_0008( "0008", "RETR.POR ATRASOS NO INCLUIDA OTROS APART", null, null ),
	T84_0009( "0009", "RETRIBUCIÓN POR ATRASOS.CONV.COLECTIVO", null, null ),
	T84_0010( "0010", "RETRIBUCIÓN POR ATRASOS.SENTENCIA JUD.", null, null ),
	T84_0011( "0011", "RETRIBUCIÓN POR ATRASOS.NORMATIVA", null, null ),
	T84_0012( "0012", "RETRIBUCIÓN POR ATRASOS.ACTA CONCILIAC.", null, null ),
	T84_0013( "0013", "R.ESPECIE NO INCLUIDA EN OTROS APARTADOS", null, null ),
	T84_0014( "0014", "R.ESP.VIVIENDA.PROP.PAGAD.C/VALOR.CATAST.", null, null ),
	T84_0015( "0015", "R.ESP.VIVIENDA.PROP.PAGAD.PTE.VALOR.CAT.", null, null ),
	T84_0016( "0016", "R.ESP.VIVIENDA.NO PROPIEDAD PAGADOR", null, null ),
	T84_0017( "0017", "R.ESP.VEHÍCULO.ENTREGA AL TRABAJADOR", null, null ),
	T84_0018( "0018", "R.ESP.VEHÍCULO.USO.PROPIEDAD PAGADOR", null, null ),
	T84_0019( "0019", "R.ESP.VEHÍCULO USO.NO PROPIEDAD PAGADOR", null, null ),
	T84_0020( "0020", "R.ESP.VEHÍCULO USO Y POSTERIOR ENTREGA", null, null ),
	T84_0021( "0021", "R.ESP.PRÉSTAMO.TIPO INTERÉS < LEGAL", null, null ),
	T84_0022( "0022", "R.ESP. MANUTENCIÓN Y SIMILARES", null, null ),
	T84_0023( "0023", "R.ESP. HOSPEDAJE Y SIMILARES", null, null ),
	T84_0024( "0024", "R.ESP. VIAJES Y SIMILARES", null, null ),
	T84_0025( "0025", "R.ESP.GASTOS DE ESTUDIOS Y MANUTENCIÓN", null, null ),
	T84_0026( "0026", "R.ESP.DERECHOS FUNDADORES DE SOCIEDADES", null, null ),
	T84_0027( "0027", "QUEBRANTO DE MONEDA", null, null ),
	T84_0028( "0028", "DESGASTE ÚTILES Y HERRAMIENTAS", null, null ),
	T84_0029( "0029", "ADQUISICIÓN Y MANTENIMIENTO ROPA TRABAJO", null, null ),
	T84_0030( "0030", "PERCEPCIONES POR MATRIMONIO", null, null ),
	T84_0031( "0031", "DONACIONES PROMOCIONALES", null, null ),
	T84_0032( "0032", "PLUSES DE TRANSPORTE Y DE DISTANCIA", null, null ),
	T84_0033( "0033", "PLANES PENSIONES Y SIST. ALTERNATIVOS", null, null ),
	T84_0034( "0034", "ACCIONES O PARTICIPACIONES EMPRESA", null, null ),
	T84_0035( "0035", "GASTOS ESTUDIO ACT. CAPACIT. O RECICLAJE", null, null ),
	T84_0036( "0036", "PRODUCTOS.PREC.REB.-CANTIN.COMED.ECONOM.", null, null ),
	T84_0037( "0037", "BIENES DESTINADOS A SERV. SOC. Y CULT.", null, null ),
	T84_0038( "0038", "PRIMAS SEGURO AT O RESPONS. CIVIL TRAB.", null, null ),
	T84_0039( "0039", "PRIMAS SEGURO ENFERMEDAD COMÚN TRABAJ.", null, null ),
	T84_0040( "0040", "PRIMAS SEGURO ENFERMEDAD COMÚN FAMILIAR.", null, null ),
	T84_0041( "0041", "PREST. EDUC. POR CENTR.AUT. A HIJ. TRAB.", null, null ),
	T84_0042( "0042", "GASTOS DE ESTANCIA", null, null ),
	T84_0043( "0043", "GASTOS MANUTENCIÓN PERNOCTA ESPAÑA", null, null ),
	T84_0044( "0044", "GASTOS MANUTENCIÓN PERNOCTA EXTRANJERO", null, null ),
	T84_0045( "0045", "GASTOS MANUTENCIÓN SIN PERNOCTA ESPAÑA", null, null ),
	T84_0046( "0046", "GASTOS MANUTENCIÓN SIN PERNOCTA EXTRANJERO", null, null ),
	T84_0047( "0047", "GASTOS MANUTENCIÓN PERSONAL VUELO ESPAÑA", null, null ),
	T84_0048( "0048", "GASTOS MANUTENCIÓN PERSONAL VUELO EXTR.", null, null ),
	T84_0049( "0049", "GASTOS DE LOCOMOCIÓN TRANSPORTE PÚBLICO", null, null ),
	T84_0050( "0050", "GASTOS LOCOMOCIÓN SIN JUSTIFIC. IMPORTE", null, null ),
	T84_0051( "0051", "INDEMNIZACIONES POR FALLECIMIENTO", null, null ),
	T84_0052( "0052", "INDEMNIZACIONES POR TRASLADOS", null, null ),
	T84_0053( "0053", "INDEMNIZACIONES POR SUSPENSIONES", null, null ),
	T84_0054( "0054", "INDEMNIZACIONES POR DESPIDO O CESE", null, null ),
	T84_0055( "0055", "MEJORAS PREST.SS.INCAPACIDAD TEMPORAL", null, null ),
	T84_0056( "0056", "MEJORAS PREST.SS.<>INCAPACIDAD TEMPORAL", null, null ),
	;
	public static final String TABLE_NAME = "T84";
	public static final String TABLE_DESCRIPTION = "T84.txt";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	T84( String code, String description, String startDate, String endDate ) {
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

	public static T84 getEnumByValue(String expression) {
		for( T84 o : T84.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}