package com.esferalia.aon.payroll.certificados.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) Terrores table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum Terrores {

	Terrores_DHG0006( "DHG0006", "Vigente", null, null ),
	Terrores_DHG0008( "DHG0008", "Vigente", null, null ),
	Terrores_DHG0010( "DHG0010", "ERROR DEL SISTEMA", "Vigente", null ),
	Terrores_DHG0023( "DHG0023", "Vigente", null, null ),
	Terrores_DHT0001( "DHT0001", "Vigente", null, null ),
	Terrores_DHT0002( "DHT0002", "Vigente", null, null ),
	Terrores_DHT0003( "DHT0003", "Beneficiario sin periodos de actividad", "OBSOLETO desde el 01/01/2007", null ),
	Terrores_DHT0004( "DHT0004", "Vigente", null, null ),
	Terrores_DHT0013( "DHT0013", "Vigente", null, null ),
	Terrores_DHT0014( "DHT0014", "Situación incompatible con indicador de reanudación múltiple", "OBSOLETO desde el 01/01/2007", null ),
	Terrores_DHT0015( "DHT0015", "Vigente", null, null ),
	Terrores_DHT0017( "DHT0017", "Error en la continuidad de los periodos de actividad", "OBSOLETO desde el 17/05/2007", null ),
	Terrores_DHT0020( "DHT0020", "Vigente", null, null ),
	Terrores_DHT0022( "DHT0022", "Vigente", null, null ),
	Terrores_DHT0024( "DHT0024", "El beneficiario no es fijo discontinuo", "OBSOLETO desde el 01/04/2007", null ),
	Terrores_DHT0026( "DHT0026", "Beneficiario con periodos de actividad superiores", "OBSOLETO desde el 01/05/2007", null ),
	Terrores_DHT0027( "DHT0027", "Vigente", null, null ),
	Terrores_DHT0028( "DHT0028", "Vigente", null, null ),
	Terrores_DHT0029( "DHT0029", "Vigente", null, null ),
	Terrores_DHT0030( "DHT0030", "Vigente                                                  ", null, null ),
	Terrores_DHT0031( "DHT0031", "Vigente                                               ", null, null ),
	Terrores_DHT0032( "DHT0032", "Vigente       ", null, null ),
	Terrores_DHT0033( "DHT0033", "Vigente                                                  ", null, null ),
	Terrores_DHT0034( "DHT0034", "Vigente                    ", null, null ),
	Terrores_DHT0035( "DHT0035", "Vigente", null, null ),
	Terrores_DHT0036( "DHT0036", "Vigente", null, null ),
	Terrores_DHT0037( "DHT0037", "Vigente", null, null ),
	Terrores_DWG0004( "DWG0004", "Vigente", null, null ),
	Terrores_DWG0005( "DWG0005", "No se puede cumplimentar número de ERE y coeficiente de actividad, son valores excluyentes", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWG0013( "DWG0013", "El CIF/NIF es erróneo", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWG0033( "DWG0033", "El coeficiente de actividad no corresponde a un ERE", "OBSOLETO desde el 15/03/2008", null ),
	Terrores_DWG0038( "DWG0038", "El coeficiente de actividad no corresponde a una comunicación de Fijos discontinuos", "Vigente", null ),
	Terrores_DWG0039( "DWG0039", "Vigente", null, null ),
	Terrores_DWG0044( "DWG0044", null, null, null ),
	Terrores_DWG0047( "DWG0047", "Vigente", null, null ),
	Terrores_DWG0048( "DWG0048", "Vigente", null, null ),
	Terrores_DWG0049( "DWG0049", "Vigente", null, null ),
	Terrores_DWG0050( "DWG0050", "Vigente", null, null ),
	Terrores_DWG0051( "DWG0051", "Vigente", null, null ),
	Terrores_DWG0052( "DWG0052", "Vigente", null, null ),
	Terrores_DWG0053( "DWG0053", null, null, null ),
	Terrores_DWT0001( "DWT0001", "Fecha de inicio del periodo de actividad no coincide con comienzo de mes", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0002( "DWT0002", "Fecha de fin del periodo de actividad no coincide con el fin de mes", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0003( "DWT0003", "Fechas de intervalos de actividad deben ser correlativas, no pueden solaparse y deben cubrir todo el periodo de actividad", "OBSOLETO desde el 15/03/2008", null ),
	Terrores_DWT0006( "DWT0006", "Fecha errónea: existe alguna fecha superior a la del sistema (año incorrecto)", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0007( "DWT0007", "Fecha errónea: existe alguna fecha superior a la del sistema (mes incorrecto)", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0008( "DWT0008", "Fecha errónea: existe alguna fecha superior a la del sistema (día incorrecto)", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0009( "DWT0009", "Fecha errónea. El formato debe ser aaaammdd (aaaa=año, mm=mes, dd=día)", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0010( "DWT0010", "Fechas de intervalos de actividad deben ser correlativas, no pueden solaparse y deben cubrir el periodo de actividad según determine la información complementaria", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0011( "DWT0011", "Fecha errónea: existe alguna fecha superior a la del sistema", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0012( "DWT0012", "El NIF/NIE es erróneo", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0014( "DWT0014", "Vigente", null, null ),
	Terrores_DWT0015( "DWT0015", "Vigente", null, null ),
	Terrores_DWT0016( "DWT0016", "Vigente", null, null ),
	Terrores_DWT0017( "DWT0017", "No se ha introducido ningún intervalo de actividad", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0018( "DWT0018", "El periodo de actividad debe coincidir con un mes natural", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0019( "DWT0019", "no se encuentra en tabla TKFCOEFI", null, null ),
	Terrores_DWT0020( "DWT0020", "no se encuentra en la tabla TCKSITEM", null, null ),
	Terrores_DWT0021( "DWT0021", "no se encuentra en la tabla TKDIASAC", null, null ),
	Terrores_DWT0030( "DWT0030", "Vigente", null, null ),
	Terrores_DWT0031( "DWT0031", "Vigente", null, null ),
	Terrores_DWT0032( "DWT0032", "Vigente", null, null ),
	Terrores_DWT0034( "DWT0034", "Fecha errónea. La fecha no existe o no sigue el formato aaaammdd (aaaa=año, mm=mes, dd=día)", "OBSOLETO desde el 15/03/2008", null ),
	Terrores_DWT0036( "DWT0036", "Vigente", null, null ),
	Terrores_DWT0037( "DWT0037", "La fecha de inicio de un periodo de actividad debe ser anterior o igual a la fecha de fin de ese periodo", "Vigente", null ),
	Terrores_DWT0040( "DWT0040", null, null, null ),
	Terrores_DWT0041( "DWT0041", null, null, null ),
	Terrores_DWT0042( "DWT0042", "Indica la obligatoriedad de consignar el código de actividad que describe la actividad desempeñada en un intervalo", "Vigente", null ),
	Terrores_DWT0043( "DWT0043", "Se valida el número máximo de intervalos de actividad permitidos dentro de una comunicación", "Vigente", null ),
	Terrores_DWT0045( "DWT0045", null, null, null ),
	Terrores_DWT0046( "DWT0046", "Es necesario que un periodo de actividad contenga, al menos, un intervalo de actividad", "Vigente", null ),
	Terrores_DWT0049( "DWT0049", "Las comunicaciones de fijos discontínuos deben informar el campo Codigo de INFORMACION_COMPLEMENTARIA_SITUACION", "OBSOLETO desde el 07/05/2012", null ),
	Terrores_DWT0050( "DWT0050", "Vigente", null, null ),
	Terrores_DWT0051( "DWT0051", "Vigente", null, null ),
	Terrores_DWT0052( "DWT0052", "Vigente", null, null ),
	Terrores_DWT0053( "DWT0053", "Vigente", null, null ),
	Terrores_DWT0054( "DWT0054", "Vigente", null, null ),
	Terrores_DWT0055( "DWT0055", "Vigente", null, null ),
	Terrores_DWT0056( "DWT0056", "Vigente", null, null ),
	Terrores_DWT0057( "DWT0057", "se debe especificar el porcentaje de dedicación", " en cuyo caso debe informase el campo dedicación completa", null ),
	Terrores_DWT0058( "DWT0058", "Vigente", null, null ),
	Terrores_DWT0059( "DWT0059", "Vigente", null, null ),
	Terrores_DWT0060( "DWT0060", "Vigente", null, null ),
	Terrores_DWT0061( "DWT0061", "Vigente", null, null ),
	Terrores_DWT0062( "DWT0062", "Vigente", null, null ),
	Terrores_DWT0063( "DWT0063", "Vigente", null, null ),
	Terrores_DWT0064( "DWT0064", "Vigente", null, null ),
	Terrores_DWT0065( "DWT0065", "debe estar informado solamente para la causa de suspensión o extinción 18", null, null ),
	Terrores_DWT0067( "DWT0067", "Vigente", null, null ),
	Terrores_DWT0068( "DWT0068", "Vigente", null, null ),
	Terrores_DWT0069( "DWT0069", "Vigente", null, null ),
	Terrores_DWT0071( "DWT0071", "Vigente", null, null ),
	Terrores_DWT0072( "DWT0072", "Vigente", null, null ),
	Terrores_DWT0073( "DWT0073", "Número de días cotizados de vacaciones anuales retribuidas y no disfrutadas no puede superar los 99", "Vigente", null ),
	Terrores_DWT0074( "DWT0074", "Vigente", null, null ),
	Terrores_DWT0075( "DWT0075", "Vigente", null, null ),
	Terrores_DWT0077( "DWT0077", "Vigente", null, null ),
	Terrores_DWT0078( "DWT0078", "Vigente", null, null ),
	Terrores_DWT0082( "DWT0082", "Vigente", null, null ),
	Terrores_DWT0084( "DWT0084", "Vigente", null, null ),
	Terrores_DWT0085( "DWT0085", "Vigente", null, null ),
	Terrores_DWT0086( "DWT0086", "Vigente", null, null ),
	Terrores_DWT0087( "DWT0087", "Vigente", null, null ),
	Terrores_DWT0088( "DWT0088", "Vigente", null, null ),
	Terrores_DWT0089( "DWT0089", "Vigente", null, null ),
	Terrores_DWT0090( "DWT0090", "Vigente", null, null ),
	Terrores_DWT0091( "DWT0091", "No se admiten trabajadores repetidos en un mismo fichero XML", "Vigente", null ),
	Terrores_DWT0092( "DWT0092", "El número ERE debe estar informado si el porcentaje de reducción de jornada es por causa ERE", "OBSOLETO desde el 07/05/2012", null ),
	Terrores_DWT0093( "DWT0093", "Información complementaria no permitida cuando se informe el campo Nº ERE", "Vigente", null ),
	Terrores_DWT0094( "DWT0094", "Es obligatorio cumplimentar 'Cargo público o sindical'", "Vigente", null ),
	Terrores_DWT0095( "DWT0095", "Cargo público o sindical debe tener el valor 2 (cargo representante) o 3 (miembro de corporación)", "Vigente", null ),
	Terrores_DWT0096( "DWT0096", "La Causa de suspensión o extinción de la relación laboral deberá tener el valor 27 o 28", "Vigente", null ),
	Terrores_DWT0097( "DWT0097", "La Causa de suspensión o extinción de la relación laboral deberá tener el valor 27", "Vigente", null ),
	Terrores_DWT0098( "DWT0098", "El Número de afiliación a la Seguridad Social (NumSS) es obligatorio", "Vigente", null ),
	Terrores_DWT0145( "DWT0145", "El número de días cotizados no puede ser superior a 30 para el mes de Febrero", "Vigente", null ),
	Terrores_DWT0146( "DWT0146", null, null, null ),
	Terrores_DWT0147( "DWT0147", "el porcenteje de reducción por ERE es obligatorio", null, null ),
	Terrores_DWT0148( "DWT0148", "Vigente", null, null ),
	Terrores_DWT0149( "DWT0149", "Para la causa de suspensión 16, 17 y 18, el número de ERE es obligatorio.", "OBSOLETO desde el 07/05/2012", null ),
	Terrores_DWT0150( "DWT0150", "Vigente", null, null ),
	Terrores_DWT0151( "DWT0151", "Vigente", null, null ),
	Terrores_DWT0152( "DWT0152", "vigente", null, null ),
	Terrores_DWT0153( "DWT0153", "Vigente", null, null ),
	Terrores_DWT0154( "DWT0154", "Vigente", null, null ),
	Terrores_DWT0155( "DWT0155", "Vigente", null, null ),
	Terrores_DWT0156( "DWT0156", "Vigente", null, null ),
	Terrores_DWT0157( "DWT0157", "Vigente", null, null ),
	Terrores_DWT0158( "DWT0158", "Vigente", null, null ),
	Terrores_DWT0159( "DWT0159", "Vigente", null, null ),
	Terrores_DWT0160( "DWT0160", null, null, null ),
	Terrores_DWT0161( "DWT0161", "Vigente", null, null ),
	Terrores_DWT0162( "DWT0162", "Obsoleto", null, null ),
	Terrores_DWT0163( "DWT0163", "Vigente", null, null ),
	Terrores_DWT0164( "DWT0164", "la base de cotización por desempleo es obligatoria y con valor mayor que cero", null, null ),
	Terrores_DWT0165( "DWT0165", "el número de días cotizados es obligatorio y con valor mayor que cero", null, null ),
	Terrores_DWT0166( "DWT0166", "Vigente", null, null ),
	Terrores_DWT0167( "DWT0167", "Vigente", null, null ),
	Terrores_DWT0168( "DWT0168", "Vigente", null, null ),
	Terrores_DWT0169( "DWT0169", "Vigente", null, null ),
	Terrores_DWT0170( "DWT0170", "Vigente", null, null ),
	Terrores_DWT0171( "DWT0171", "Vigente", null, null ),
	Terrores_DWT0172( "DWT0172", "Vigente", null, null ),
	Terrores_DWT0173( "DWT0173", "Vigente", null, null ),
	Terrores_DWT0174( "DWT0174", "Vigente", null, null ),
	Terrores_DWT0175( "DWT0175", "Vigente", null, null ),
	Terrores_DWT0176( "DWT0176", "Vigente", null, null ),
	Terrores_DWT0177( "DWT0177", "Vigente", null, null ),
	Terrores_DWT0178( "DWT0178", "Vigente", null, null ),
	Terrores_DWT0179( "DWT0179", "la causa de porcentaje parcial y el porcentaje parcial deben estar informados", null, null ),
	Terrores_DWT0180( "DWT0180", "Vigente", null, null ),
	Terrores_DWT0181( "DWT0181", "Vigente", null, null ),
	Terrores_DWT0182( "DWT0182", "Vigente", null, null ),
	Terrores_DWT0183( "DWT0183", "Vigente", null, null ),
	Terrores_DWT0184( "DWT0184", "Vigente", null, null ),
	Terrores_DWT0185( "DWT0185", "Vigente", null, null ),
	Terrores_DWT0186( "DWT0186", "Vigente", null, null ),
	Terrores_DWT0187( "DWT0187", "Vigente", null, null ),
	Terrores_DWT0188( "DWT0188", "Vigente", null, null ),
	Terrores_DWT0189( "DWT0189", "Vigente", null, null ),
	Terrores_DWT0190( "DWT0190", "Vigente", null, null ),
	Terrores_DWT0191( "DWT0191", "Vigente", null, null ),
	Terrores_DWT0192( "DWT0192", "obsoleto", null, null ),
	Terrores_DWT0193( "DWT0193", "Vigente", null, null ),
	Terrores_DWT0194( "DWT0194", "Vigente", null, null ),
	Terrores_DWT0195( "DWT0195", "Vigente", null, null ),
	Terrores_DWT0196( "DWT0196", "Vigente", null, null ),
	Terrores_DWT0197( "DWT0197", "Vigente", null, null ),
	Terrores_DWT0198( "DWT0198", "la base de cotización por desempleo es obligatoria y con valor mayor que cero para esas causas de suspensión", null, null ),
	Terrores_DWT0199( "DWT0199", "Vigente", null, null ),
	Terrores_DWT0200( "DWT0200", "no los dos campos conjuntamente.", null, null ),
	Terrores_DWT0201( "DWT0201", "es obligatoria desde el 01/01/2012.", null, null ),
	Terrores_DWT0202( "DWT0202", "no aplica para la fecha informada.", null, null ),
	Terrores_DWT0203( "DWT0203", "supera la cantidad máxima establecida para el año indicado.", null, null ),
	Terrores_DWT0204( "DWT0204", "es obligatoria desde el 01/01/2012.", null, null ),
	Terrores_DWT0205( "DWT0205", "no aplica para la fecha informada.", null, null ),
	Terrores_DWT0206( "DWT0206", "supera la cantidad máxima establecida para el año indicado.", null, null ),
	Terrores_DWT0207( "DWT0207", "Vigente", null, null ),

	;
	public static final String TABLE_NAME = "Terrores";
	public static final String TABLE_DESCRIPTION = "No description found";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	Terrores( String code, String description, String startDate, String endDate ) {
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

	public static Terrores getEnumByValue(String expression) {
		for( Terrores o : Terrores.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}