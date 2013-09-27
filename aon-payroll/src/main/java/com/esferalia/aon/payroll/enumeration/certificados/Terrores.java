package com.esferalia.aon.payroll.enumeration.certificados;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SEPECodeTablesWriter.ISepeEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) Terrores table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum Terrores implements ISepeEnum {

	Terrores_DHG0006( "DHG0006", "Empleador inexistente en la base de datos", "Vigente", null ),
	Terrores_DHG0008( "DHG0008", "La actividad económica no figura en tabla", "Vigente", null ),
	Terrores_DHG0010( "DHG0010", "ERROR DEL SISTEMA", "Vigente", null ),
	Terrores_DHG0023( "DHG0023", "El CCC no se corresponde con el NIF de la empresa", "Vigente", null ),
	Terrores_DHT0001( "DHT0001", "NIF/NIE inexistente en la base de datos", "Vigente", null ),
	Terrores_DHT0002( "DHT0002", "No se ha podido realizar la operación. Preséntese a su oficina de empleo", "Vigente", null ),
	Terrores_DHT0003( "DHT0003", "Beneficiario sin periodos de actividad", "OBSOLETO desde el 01/01/2007", null ),
	Terrores_DHT0004( "DHT0004", "NIF/NIE no tiene prestación", "Vigente", null ),
	Terrores_DHT0013( "DHT0013", "Apunte procesado existente en ese periodo", "Vigente", null ),
	Terrores_DHT0014( "DHT0014", "Situación incompatible con indicador de reanudación múltiple", "OBSOLETO desde el 01/01/2007", null ),
	Terrores_DHT0015( "DHT0015", "Periodo de actividad solapado con uno ya existente", "Vigente", null ),
	Terrores_DHT0017( "DHT0017", "Error en la continuidad de los periodos de actividad", "OBSOLETO desde el 17/05/2007", null ),
	Terrores_DHT0020( "DHT0020", "Reanudación no permitida por causa de baja", "Vigente", null ),
	Terrores_DHT0022( "DHT0022", "Periodo de acitvidad anterior con error", "Vigente", null ),
	Terrores_DHT0024( "DHT0024", "El beneficiario no es fijo discontinuo", "OBSOLETO desde el 01/04/2007", null ),
	Terrores_DHT0026( "DHT0026", "Beneficiario con periodos de actividad superiores", "OBSOLETO desde el 01/05/2007", null ),
	Terrores_DHT0027( "DHT0027", "Beneficiario con periodos superiores procesados o en error", "Vigente", null ),
	Terrores_DHT0028( "DHT0028", "El coeficiente de actividad no corresponde a una comunicación de Fijos discontinuos", "Vigente", null ),
	Terrores_DHT0029( "DHT0029", "El coeficiente de actividad no corresponde a un ERE", "Vigente", null ),
	Terrores_DHT0030( "DHT0030", "La oficina de empleo no existe", "Vigente", null ),
	Terrores_DHT0031( "DHT0031", "La entidad bancaria no existe", "Vigente", null ),
	Terrores_DHT0032( "DHT0032", "La entidad bancaria elegida no tiene suscrito convenio con el SPEE", "Vigente", null ),
	Terrores_DHT0033( "DHT0033", "La sucursal bancaria no existe", "Vigente", null ),
	Terrores_DHT0034( "DHT0034", "La sucursal bancaria elegida no está integrada en el circuito de pago", "Vigente", null ),
	Terrores_DHT0035( "DHT0035", "El número de afiliación a la Seguridad Social es erróneo", "Vigente", null ),
	Terrores_DHT0036( "DHT0036", "Fecha de inicio prestación menor que la fecha de témino de prestación anterior", "Vigente", null ),
	Terrores_DHT0037( "DHT0037", "Apunte con visto bueno existente en ese periodo.", "Vigente", null ),
	Terrores_DWG0004( "DWG0004", "Año del número de ERE incorrecto: no puede ser mayor que el año actual ni anterior en más de cinco años.", "Vigente", null ),
	Terrores_DWG0005( "DWG0005", "No se puede cumplimentar número de ERE y coeficiente de actividad, son valores excluyentes", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWG0013( "DWG0013", "El CIF/NIF es erróneo", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWG0033( "DWG0033", "El coeficiente de actividad no corresponde a un ERE", "OBSOLETO desde el 15/03/2008", null ),
	Terrores_DWG0038( "DWG0038", "El coeficiente de actividad no corresponde a una comunicación de Fijos discontinuos", "Vigente", null ),
	Terrores_DWG0039( "DWG0039", "El formato se basa en un coeficiente (", ") y un año (", ")" ),
	Terrores_DWG0044( "DWG0044", "Se valida que el campo Datos_Empresa --> Numero_ERE venga informado", null, null ),
	Terrores_DWG0047( "DWG0047", "El CIF_NIF del representante es erróneo", "Vigente", null ),
	Terrores_DWG0048( "DWG0048", "El CIF_NIF de la empresa es erróneo", "Vigente", null ),
	Terrores_DWG0049( "DWG0049", "No se admiten Códigos de Cuenta de Cotización repetidos en un mismo fichero XML", "Vigente", null ),
	Terrores_DWG0050( "DWG0050", "La Cuenta de Cotización es errónea o inexistente en las bases de datos de Certific@2", "Vigente", null ),
	Terrores_DWG0051( "DWG0051", "El año del número de ERE no puede ser posterior al actual", "Vigente", null ),
	Terrores_DWG0052( "DWG0052", "El formato del número de ERE debe ser NNNNNAAAA", "Vigente", null ),
	Terrores_DWG0053( "DWG0053", "Colectivo agrario temporalmente fuera de alcance", null, null ),
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
	Terrores_DWT0014( "DWT0014", "No se han introducido periodos de actividad", "Vigente", null ),
	Terrores_DWT0015( "DWT0015", "La fecha de inicio de periodo de actividad esta vacía", "Vigente", null ),
	Terrores_DWT0016( "DWT0016", "La fecha de fin de periodo de actividad esta vacía", "Vigente", null ),
	Terrores_DWT0017( "DWT0017", "No se ha introducido ningún intervalo de actividad", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0018( "DWT0018", "El periodo de actividad debe coincidir con un mes natural", "OBSOLETO desde el 14/02/2007", null ),
	Terrores_DWT0019( "DWT0019", "Coeficiente de actividad incorrecto, no se encuentra en tabla TKFCOEFI", "Vigente", null ),
	Terrores_DWT0020( "DWT0020", "Código de información complementaria erróneo, no se encuentra en la tabla TCKSITEM", "Vigente", null ),
	Terrores_DWT0021( "DWT0021", "Código de actividad erróneo, no se encuentra en la tabla TKDIASAC", "Vigente", null ),
	Terrores_DWT0030( "DWT0030", "Fecha de inicio del periodo de actividad no puede superar el fin de mes actual", "Vigente", null ),
	Terrores_DWT0031( "DWT0031", "Fecha de fin del periodo de actividad no puede superar el fin del mes actual", "Vigente", null ),
	Terrores_DWT0032( "DWT0032", "Fechas de intervalos de actividad no pueden solaparse y deben pertenecer al periodo declarado", "Vigente", null ),
	Terrores_DWT0034( "DWT0034", "Fecha errónea. La fecha no existe o no sigue el formato aaaammdd (aaaa=año, mm=mes, dd=día)", "OBSOLETO desde el 15/03/2008", null ),
	Terrores_DWT0036( "DWT0036", "El NIF/NIE del trabajador es erróneo", "Vigente", null ),
	Terrores_DWT0037( "DWT0037", "La fecha de inicio de un periodo de actividad debe ser anterior o igual a la fecha de fin de ese periodo", "Vigente", null ),
	Terrores_DWT0040( "DWT0040", "Indica la obligatoriedad de rellenar el campo de información complementaria del fichero de comunicación", null, null ),
	Terrores_DWT0041( "DWT0041", "Se valida que el campo Datos_Trabajador --> Datos_Actividad --> Coeficiente_Actividad venga informado en el fichero de comunicación", null, null ),
	Terrores_DWT0042( "DWT0042", "Indica la obligatoriedad de consignar el código de actividad que describe la actividad desempeñada en un intervalo", "Vigente", null ),
	Terrores_DWT0043( "DWT0043", "Se valida el número máximo de intervalos de actividad permitidos dentro de una comunicación", "Vigente", null ),
	Terrores_DWT0045( "DWT0045", "La fecha de inicio/fin de campaña debe coincidir con la fecha de inicio/fin respectivamente del periodo de actividad asociado", null, null ),
	Terrores_DWT0046( "DWT0046", "Es necesario que un periodo de actividad contenga, al menos, un intervalo de actividad", "Vigente", null ),
	Terrores_DWT0049( "DWT0049", "Las comunicaciones de fijos discontínuos deben informar el campo Codigo de INFORMACION_COMPLEMENTARIA_SITUACION", "OBSOLETO desde el 07/05/2012", null ),
	Terrores_DWT0050( "DWT0050", "El formato del número de la Seguridad Social no es correcto", "Vigente", null ),
	Terrores_DWT0051( "DWT0051", "El grupo de cotización no figura en tabla", "Vigente", null ),
	Terrores_DWT0052( "DWT0052", "El tipo de contrato no figura en tabla", "Vigente", null ),
	Terrores_DWT0053( "DWT0053", "El tipo de distribución de jornadas no figura en tabla", "Vigente", null ),
	Terrores_DWT0054( "DWT0054", "El número de días trabajados por semana en la distribución regular de jornadas excede el tope máximo permitido (7)", "Vigente", null ),
	Terrores_DWT0055( "DWT0055", "La profesión no figura en tabla", "Vigente", null ),
	Terrores_DWT0056( "DWT0056", "El cargo público sindical no figura en tabla", "Vigente", null ),
	Terrores_DWT0057( "DWT0057", "Si se informa el cargo público sindical, se debe especificar el porcentaje de dedicación, excepto si éste representa el 100%, en cuyo caso debe informase el campo dedicación completa", "Vigente", null ),
	Terrores_DWT0058( "DWT0058", "La fecha de alta en la empresa no existe", "Vigente", null ),
	Terrores_DWT0059( "DWT0059", "La causa de suspensión no figura en tabla", "Vigente", null ),
	Terrores_DWT0060( "DWT0060", "La fecha de suspensión o extinción no existe", "Vigente", null ),
	Terrores_DWT0061( "DWT0061", "La fecha de fin de suspensión no existe", "Vigente", null ),
	Terrores_DWT0062( "DWT0062", "La fecha de fin de suspensión debe informarse si el código causa de suspensión corresponde a los valores 17 ó 18", "Vigente", null ),
	Terrores_DWT0063( "DWT0063", "La fecha de fin de suspensión no puede ser mayor que la fecha de suspensión o extinción", "Vigente", null ),
	Terrores_DWT0064( "DWT0064", "El formato del número de ERE debe ser NNNNNAAAA", "Vigente", null ),
	Terrores_DWT0065( "DWT0065", "El porcentaje de reducción por ERE, debe estar informado solamente para la causa de suspensión o extinción 18", "Vigente", null ),
	Terrores_DWT0067( "DWT0067", "Año y mes de cotización no pueden anteceder a la fecha de alta de la empresa", "Vigente", null ),
	Terrores_DWT0068( "DWT0068", "El año y mes de cotización no pueden ser posteriores a la fecha de suspensión", "Vigente", null ),
	Terrores_DWT0069( "DWT0069", "Se ha superado el límite de registros permitidos (72) en los datos de cotización", "Vigente", null ),
	Terrores_DWT0071( "DWT0071", "La fecha de inicio de periodo laboral de la distribución de jornadas no puede ser anterior a la fecha de alta de la empresa", "Vigente", null ),
	Terrores_DWT0072( "DWT0072", "La fecha de fin de periodo laboral de la distribución de jornadas no puede superar la fecha de suspensión o extinción", "Vigente", null ),
	Terrores_DWT0073( "DWT0073", "Número de días cotizados de vacaciones anuales retribuidas y no disfrutadas no puede superar los 99", "Vigente", null ),
	Terrores_DWT0074( "DWT0074", "La fecha de inicio de periodo de salarios de tramitación no existe", "Vigente", null ),
	Terrores_DWT0075( "DWT0075", "La fecha de fin de período de salarios de tramitación no existe", "Vigente", null ),
	Terrores_DWT0077( "DWT0077", "La fecha de inicio de periodo de salarios de tramitación debe ser anterior a la fecha de fin de dicho periodo", "Vigente", null ),
	Terrores_DWT0078( "DWT0078", "La fecha de fin de periodo de salarios de tramitación debe ser anterior a la fecha de suspensión o extinción", "Vigente", null ),
	Terrores_DWT0082( "DWT0082", "La profesión (categoría profesional para el Régimen Especial de Minería del Carbón) no figura en tabla", "Vigente", null ),
	Terrores_DWT0084( "DWT0084", "El número de dias trabajados en la distribución irregular de jornadas excede el número de dias existente en el periodo declarado", "Vigente", null ),
	Terrores_DWT0085( "DWT0085", "Se ha superado el límite de registros permitidos (191) en los datos de Distribución de Jornadas", "Vigente", null ),
	Terrores_DWT0086( "DWT0086", "El número de días de salario de tramitación no se corresponde con el periodo especificado", "Vigente", null ),
	Terrores_DWT0087( "DWT0087", "La causa de suspensión y ERE son incompatibles", "Vigente", null ),
	Terrores_DWT0088( "DWT0088", "El código de causa de porcentaje de reducción no figura en tabla", "Vigente", null ),
	Terrores_DWT0089( "DWT0089", "La unidad de medida de la duración del contrato no figura en tabla", "Vigente", null ),
	Terrores_DWT0090( "DWT0090", "La Base Contingencias Comunes solo debe informarse si el código causa de suspensión corresponde a los valores 17 ó 18", "Vigente", null ),
	Terrores_DWT0091( "DWT0091", "No se admiten trabajadores repetidos en un mismo fichero XML", "Vigente", null ),
	Terrores_DWT0092( "DWT0092", "El número ERE debe estar informado si el porcentaje de reducción de jornada es por causa ERE", "OBSOLETO desde el 07/05/2012", null ),
	Terrores_DWT0093( "DWT0093", "Información complementaria no permitida cuando se informe el campo Nº ERE", "Vigente", null ),
	Terrores_DWT0094( "DWT0094", "Es obligatorio cumplimentar 'Cargo público o sindical'", "Vigente", null ),
	Terrores_DWT0095( "DWT0095", "Cargo público o sindical debe tener el valor 2 (cargo representante) o 3 (miembro de corporación)", "Vigente", null ),
	Terrores_DWT0096( "DWT0096", "La Causa de suspensión o extinción de la relación laboral deberá tener el valor 27 o 28", "Vigente", null ),
	Terrores_DWT0097( "DWT0097", "La Causa de suspensión o extinción de la relación laboral deberá tener el valor 27", "Vigente", null ),
	Terrores_DWT0098( "DWT0098", "El Número de afiliación a la Seguridad Social (NumSS) es obligatorio", "Vigente", null ),
	Terrores_DWT0145( "DWT0145", "El número de días cotizados no puede ser superior a 30 para el mes de Febrero", "Vigente", null ),
	Terrores_DWT0146( "DWT0146", "No se admiten trabajadores repetidos en un mismo Código de Cuenta de Cotización", null, null ),
	Terrores_DWT0147( "DWT0147", "Para la causa de suspensión o extinción 18, el porcenteje de reducción por ERE es obligatorio", "Vigente", null ),
	Terrores_DWT0148( "DWT0148", "El código de causa de porcentaje de reducción de jornada es incompatible con el/los porcentaje/s informado/s", "Vigente", null ),
	Terrores_DWT0149( "DWT0149", "Para la causa de suspensión 16, 17 y 18, el número de ERE es obligatorio.", "OBSOLETO desde el 07/05/2012", null ),
	Terrores_DWT0150( "DWT0150", "Al menos una distribución de jornadas en contratos a tiempo parcial es obligatoria.", "Vigente", null ),
	Terrores_DWT0151( "DWT0151", "Para el tipo de contrato indicado no se permite introducir distribucion de jornadas.", "Vigente", null ),
	Terrores_DWT0152( "DWT0152", "La Base Contingencias Comunes y/o Desempleo supera la cantidad base máxima establecida para el año indicado", "vigente", null ),
	Terrores_DWT0153( "DWT0153", "La Base Contingencias Comunes y/o Desempleo correspondiente a las vacaciones anuales retribuidas y no disfrutadas supera la cantidad base máxima establecida para el año de cese", "Vigente", null ),
	Terrores_DWT0154( "DWT0154", "El grupo de cotización debe estar informado si la cuenta de cotización es del Régimen General", "Vigente", null ),
	Terrores_DWT0155( "DWT0155", "El grupo de cotización no debe estar informado si la cuenta de cotización es del Régimen Especial Agrario", "Vigente", null ),
	Terrores_DWT0156( "DWT0156", "Las etiquetas del grupo de datos de cotización no se corresponden con el régimen informado en la cuenta de cotización", "Vigente", null ),
	Terrores_DWT0157( "DWT0157", "Las etiquetas del grupo de datos de cotización correspondiente a las vacaciones anuales retribuidas y no disfrutadas no se corresponden con el régimen informado en la cuenta de cotización", "Vigente", null ),
	Terrores_DWT0158( "DWT0158", "Alguno/s de los grupo/s de cotización no figura en tabla", "Vigente", null ),
	Terrores_DWT0159( "DWT0159", "El grupo de cotización correspondiente a las vacaciones anuales retribuidas y no disfrutadas no figura en tabla", "Vigente", null ),
	Terrores_DWT0160( "DWT0160", "El número de días y/o jornadas cotizadas no puede  superar el numero de dias del mes", null, null ),
	Terrores_DWT0161( "DWT0161", "El número de días cotizados y/o jornadas cotizadas debe estar informado", "Vigente", null ),
	Terrores_DWT0162( "DWT0162", "El número de días cotizados y/o jornadas cotizadas correspondientes a las vacaciones anuales retribuidas y no disfrutadas debe estar informado", "Obsoleto", null ),
	Terrores_DWT0163( "DWT0163", "La base de cotización por desempleo es obligatoria", "Vigente", null ),
	Terrores_DWT0164( "DWT0164", "En las vacaciones anuales retribuidas y no disfrutadas, la base de cotización por desempleo es obligatoria y con valor mayor que cero", "Vigente", null ),
	Terrores_DWT0165( "DWT0165", "En las vacaciones anuales retribuidas y no disfrutadas, el número de días cotizados es obligatorio y con valor mayor que cero", "Vigente", null ),
	Terrores_DWT0166( "DWT0166", "El código de causa de porcentaje parcial no figura en tabla", "Vigente", null ),
	Terrores_DWT0167( "DWT0167", "Causa de porcentaje parcial y porcentaje parcial deben estar ambos informados", "Vigente", null ),
	Terrores_DWT0168( "DWT0168", "El código de situación de la empresa de cese no figura en tabla", "Vigente", null ),
	Terrores_DWT0169( "DWT0169", "La fecha de inicio de la prestación debe de ser posterior a la fecha tope", "Vigente", null ),
	Terrores_DWT0170( "DWT0170", "La fecha de finalización de la prestación debe de ser posterior a la fecha de inico de la prestación", "Vigente", null ),
	Terrores_DWT0171( "DWT0171", "La fecha de solicitud de la prestación debe ser anterior a la fecha actual", "Vigente", null ),
	Terrores_DWT0172( "DWT0172", "El formato de la fecha de solicitud de la prestación debe ser AAAAMMDD", "Vigente", null ),
	Terrores_DWT0173( "DWT0173", "El formato de la fecha tope debe ser AAAAMMDD", "Vigente", null ),
	Terrores_DWT0174( "DWT0174", "El formato de la fecha de inicio de la prestación debe ser AAAAMMDD", "Vigente", null ),
	Terrores_DWT0175( "DWT0175", "El formato de la fecha de finalización de la prestación debe ser AAAAMMDD", "Vigente", null ),
	Terrores_DWT0176( "DWT0176", "El código de la oficina de empleo debe estar informado con un valor válido", "Vigente", null ),
	Terrores_DWT0177( "DWT0177", "La cuenta corriente debe estar informada con un valor válido", "Vigente", null ),
	Terrores_DWT0178( "DWT0178", "El código de deducción o devengo no figura en tabla", "Vigente", null ),
	Terrores_DWT0179( "DWT0179", "Si la causa de cese es 07, la causa de porcentaje parcial y el porcentaje parcial deben estar informados", "Vigente", null ),
	Terrores_DWT0180( "DWT0180", "Fecha de finalización de la prestación: debe estar informada cuando la causa de cese no sea 06.", "Vigente", null ),
	Terrores_DWT0181( "DWT0181", "El año del ERE no puede ser posterior al año actual.", "Vigente", null ),
	Terrores_DWT0182( "DWT0182", "El tipo de pago de nómina debe tener valor 1 si la causa de cese es 6 y valor 2 si la causa de cese es 5 ó 7.", "Vigente", null ),
	Terrores_DWT0183( "DWT0183", "La fecha de suspensión o extinción no puede ser posterior a hoy", "Vigente", null ),
	Terrores_DWT0184( "DWT0184", "La fecha de suspensión o extinción debe ser posterior a la fecha de alta en la empresa", "Vigente", null ),
	Terrores_DWT0185( "DWT0185", "La fecha de suspensión o extinción debe tener como máximo 1 año de antiguedad", "Vigente", null ),
	Terrores_DWT0186( "DWT0186", "La fecha de alta de la empresa debe ser anterior a la fecha de proceso de los datos del certificado", "Vigente", null ),
	Terrores_DWT0187( "DWT0187", "La fecha de inicio de salarios de tramitación debe ser anterior a la fecha de proceso de los datos del certificado", "Vigente", null ),
	Terrores_DWT0188( "DWT0188", "El año de la fecha de inicio de salarios de tramitación debe ser el anterior o el actual", "Vigente", null ),
	Terrores_DWT0189( "DWT0189", "La fecha de finalización de salarios de tramitación debe ser anterior a la fecha de proceso de los datos del certificado", "Vigente", null ),
	Terrores_DWT0190( "DWT0190", "El año de la fecha de finalización de salarios de tramitación debe ser el actual o el anterior", "Vigente", null ),
	Terrores_DWT0191( "DWT0191", "Hay periodos solapados en la distribución de jornadas trabajadas", "Vigente", null ),
	Terrores_DWT0192( "DWT0192", "La fecha de inicio de periodo en la distribución de jornadas trabajadas debe estar comprendido en los últimos seis años anteriores a la suspensión", "obsoleto", null ),
	Terrores_DWT0193( "DWT0193", "La fecha de inicio de periodo en la distribución de jornadas trabajadas debe ser anterior a la fecha de datos del certificado", "Vigente", null ),
	Terrores_DWT0194( "DWT0194", "La fecha de inicio de periodo en la distribucion e jornadas trabajadas debe ser anterior a la fecha de suspensión o extinción", "Vigente", null ),
	Terrores_DWT0195( "DWT0195", "La fecha de finalización de periodo en la distribución de jornadas trabajadas d ebe ser anterior a la fecha de proceso de los datos del certificado", "Vigente", null ),
	Terrores_DWT0196( "DWT0196", "El año de cotización debe ser el actual o el anterior", "Vigente", null ),
	Terrores_DWT0197( "DWT0197", "No se admiten periodos de cotización repetidos", "Vigente", null ),
	Terrores_DWT0198( "DWT0198", "En las vacaciones anuales retribuidas y no disfrutadas, la base de cotización por desempleo es obligatoria y con valor mayor que cero para esas causas de suspensión", "Vigente", null ),
	Terrores_DWT0199( "DWT0199", "La causa de suspensión 29 no es aplicable si la cuenta de cotización es del Régimen Especial Agrario.", "Vigente", null ),
	Terrores_DWT0200( "DWT0200", "A partir del 01/01/2012 ha de informar o Cotización por meses o Cotización por jornadas reales, no los dos campos conjuntamente.", "Vigente", null ),
	Terrores_DWT0201( "DWT0201", "La base de cotización al desempleo, es obligatoria desde el 01/01/2012.", "Vigente", null ),
	Terrores_DWT0202( "DWT0202", "La Base de cotización al desempleo, no aplica para la fecha informada.", "Vigente", null ),
	Terrores_DWT0203( "DWT0203", "La Base de cotización al desempleo, supera la cantidad máxima establecida para el año indicado.", "Vigente", null ),
	Terrores_DWT0204( "DWT0204", "La Base de cotización al desempleo para vacaciones, es obligatoria desde el 01/01/2012.", "Vigente", null ),
	Terrores_DWT0205( "DWT0205", "La Base de cotización al desempleo para vacaciones, no aplica para la fecha informada.", "Vigente", null ),
	Terrores_DWT0206( "DWT0206", "La Base de cotización al desempleo correspondiente a las vacaciones anuales retribuidas y no disfrutadas, supera la cantidad máxima establecida para el año indicado.", "Vigente", null ),
	Terrores_DWT0207( "DWT0207", "Valida que la fecha de inicio de periodo en la distribución de jornadas trabajadas deber ser anterior a la fecha de fin de periodo.", "Vigente", null ),

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

	public static Terrores getEnumByValue(String expression) {
		for( Terrores o : Terrores.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}