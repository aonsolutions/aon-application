
package com.esferalia.aon.payroll.enumeration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.IResourceable;

public enum ContextVariable implements IResourceable {

	START("INICIO", VariableType.DATE), END("FIN", VariableType.DATE), 
	SALARY_START("INICIO_NOMINA", VariableType.DATE),
	SALARY_END("FIN_NOMINA", VariableType.DATE), 
	CONTRACT_START("INICIO_CONTRATO", VariableType.DATE),
	CONTRACT_END("FIN_CONTRATO", VariableType.DATE), 
	SENIORITY_START("INICIO_ANTIGUEDAD", VariableType.DATE, false),
	DIRECT_PAY_START("INICIO_PAGO_DIRECTO", VariableType.DATE, false),

	// Datos de la persona
	AGE("EDAD", VariableType.INTEGER, false), GENDER("SEXO", VariableType.UNKNOWN, false),
	MALE("HOMBRE", VariableType.UNKNOWN, false), FEMALE("MUJER", VariableType.UNKNOWN, false),

	// Dias
	YEAR_DAYS("DIAS_AÑO", VariableType.INTEGER, false), MONTH_DAYS("DIAS_MES", VariableType.INTEGER, false),
	NATURAL_MONTH_DAYS("DIAS_NATURALES_MES", VariableType.INTEGER, false),
	HOLIDAYS("DIAS_VACACIONES", VariableType.INTEGER, false),
	NO_HOLIDAYS("DIAS_VACACIONES_NO_DISFRUTADOS", VariableType.INTEGER, false),
	WORKED_DAYS("DIAS_TRABAJADOS", VariableType.INTEGER, false), 
	NON_WORKED_DAYS("DIAS_NO_TRABAJADOS", VariableType.INTEGER, false), 
	WORK_DAYS("DIAS_TRABAJANDO", VariableType.INTEGER, false),
	WEEK_DAYS("DIAS_SEMANA", VariableType.INTEGER, false),
	CONTRACT_DAYS("DIAS_CONTRATO", VariableType.INTEGER, false),
	SALARY_DAYS("DIAS_NOMINA", VariableType.INTEGER, false), PAY_DAYS("DIAS_PAGA", VariableType.INTEGER, false),
	BONUS_DAYS("DIAS_BONIFICACION", VariableType.INTEGER, false),
	QUOTE_DAYS("DIAS_COTIZADOS", VariableType.INTEGER, false),
	ACTUAL_DAYS("DIAS_EFECTIVOS", VariableType.INTEGER, false), LEAVE_DAYS("DIAS_IT", VariableType.INTEGER, false),
	SPECIAL_DAYS("DIAS_ESPECIALES", VariableType.INTEGER, false),
	PATERNITY_DAYS("DIAS_PATERNIDAD", VariableType.INTEGER, false),
	MATERNITY_DAYS("DIAS_MATERNIDAD", VariableType.INTEGER, false),
	COMMON_DISEASE_DAYS("DIAS_ENFERMEDAD_COMUN", VariableType.INTEGER, false),
	COMMON_DISEASE_LACK_DAYS("DIAS_ENFERMEDAD_COMUN_CARENCIA", VariableType.INTEGER, false),
	COMMON_DISEASE_DAYS_1_3("DIAS_ENFERMEDAD_COMUN_1_3", VariableType.INTEGER, false),
	COMMON_DISEASE_DAYS_4_15("DIAS_ENFERMEDAD_COMUN_4_15", VariableType.INTEGER, false),
	COMMON_DISEASE_DAYS_16_20("DIAS_ENFERMEDAD_COMUN_16_20", VariableType.INTEGER, false),
	COMMON_DISEASE_DAYS_21("DIAS_ENFERMEDAD_COMUN_21", VariableType.INTEGER, false),
	COMMON_DISEASE_DAYS_366("DIAS_ENFERMEDAD_COMUN_366", VariableType.INTEGER, false),
	OCCUPATIONAL_DISEASE_DAYS("DIAS_ENFERMEDAD_PROFESIONAL", VariableType.INTEGER, false),
	OCCUPATIONAL_DISEASE_DAYS_366("DIAS_ENFERMEDAD_PROFESIONAL_366", VariableType.INTEGER, false),
	MENSTRUATION_DAYS("DIAS_MENSTRUACION", VariableType.INTEGER, false),
	PREGNANCY_STOP_DAYS("DIAS_INTERRUPCION_EMBARAZO", VariableType.INTEGER, false),
	PREGNANCY_39_WEEK_DAYS("DIAS_SEMANA_39_EMBARAZO", VariableType.INTEGER, false),
	PAYMENTS("NUM_PAGAS", VariableType.INTEGER, false), 
	REAL_DAYS("DIAS_REALES", VariableType.INTEGER, false),
	STRIKE_DAYS("DIAS_HUELGA", VariableType.INTEGER, false), 
	ERE_DAYS("DIAS_ERE", VariableType.INTEGER, false),
	ERE_DAYS_FORCE("DIAS_ERE_FZA", VariableType.INTEGER, false),
	ERE_DAYS_FORCE_OFF("DIAS_ERE_FZA_EXONERADO", VariableType.INTEGER, false),
	GUARANTEED_DAYS("DIAS_GARANTIZADOS", VariableType.INTEGER, false),
	ACTIVE_DAYS("DIAS_ALTA", VariableType.INTEGER, false), 
	MONDAY_DAYS("DIAS_LUNES", VariableType.DOUBLE, false),
	TUESDAY_DAYS("DIAS_MARTES", VariableType.DOUBLE, false),
	WEDNESDAY_DAYS("DIAS_MIERCOLES", VariableType.DOUBLE, false),
	THURSDAY_DAYS("DIAS_JUEVES", VariableType.DOUBLE, false), 
	FRIDAY_DAYS("DIAS_VIERNES", VariableType.DOUBLE, false),
	SATURDAY_DAYS("DIAS_SABADO", VariableType.DOUBLE, false), 
	SUNDAY_DAYS("DIAS_DOMINGO", VariableType.DOUBLE, false),
	PARTY_DAYS("DIAS_FESTIVOS", VariableType.DOUBLE, false), 
	OFF_DAYS("DIAS_INACTIVIDAD", VariableType.DOUBLE, false),
	DROP_DAYS("DIAS_AUSENCIA", VariableType.DOUBLE, false),	
	QUOTE_FACTOR("COEFICIENTE_COTIZADO", VariableType.DOUBLE, false),
	WORKING_DAYS("DIAS_LABORABLES", VariableType.INTEGER, false),
	
	OFF_CAUSE("CAUSA_INACTIVIDAD", VariableType.DOUBLE, false),
	NOT_PAID_PERMISSION("PERMISO_NO_RETRIBUIDO", VariableType.DOUBLE, false),
	SUSPEND_JOB_AND_SALARY("SUSPENSION_EMPLEO_SUELDO", VariableType.DOUBLE, false),
	
	DROP_CAUSE("CAUSA_AUSENCIA", VariableType.DOUBLE, false),
	DROP_NOT_JUSTIFIED("AUSENCIA_NO_JUSTIFICADA", VariableType.DOUBLE, false),

	// ADVANCE_NOTICE_DATE("FECHA_PREAVISO", VariableType.DATE, false),
	// ADVANCE_NOTICE_DAYS("DIAS_PREAVISO", VariableType.INTEGER, false),

	COMPENSATION_CAUSE("CAUSA_INDEMNIZACION", VariableType.INTEGER, false),
	WORKED_YEARS("AÑOS_TRABAJADOS", VariableType.INTEGER, false),

	SALARY_MONTHS("MESES_NOMINA", VariableType.INTEGER, false), PAY_MONTHS("MESES_PAGA", VariableType.INTEGER, false),
	WORKED_WEEKS("SEMANAS_TRABAJADAS", VariableType.INTEGER, false),
	SALARY_WEEKS("SEMANAS_NOMINA", VariableType.INTEGER, false), PAY_WEEKS("SEMANAS_PAGA", VariableType.INTEGER, false),

	// Horas ( contratos a tiempo parcial )
	WEEK_HOURS("HORAS_SEMANA", VariableType.DOUBLE, false), SALARY_HOURS("HORAS_NOMINA", VariableType.DOUBLE, false),
	MONDAY_HOURS("HORAS_LUNES", VariableType.DOUBLE, false), TUESDAY_HOURS("HORAS_MARTES", VariableType.DOUBLE, false),
	WEDNESDAY_HOURS("HORAS_MIERCOLES", VariableType.DOUBLE, false),
	THURSDAY_HOURS("HORAS_JUEVES", VariableType.DOUBLE, false),
	FRIDAY_HOURS("HORAS_VIERNES", VariableType.DOUBLE, false),
	SATURDAY_HOURS("HORAS_SABADO", VariableType.DOUBLE, false),
	SUNDAY_HOURS("HORAS_DOMINGO", VariableType.DOUBLE, false),
	AGREEMENT_HOURS("HORAS_CONVENIO", VariableType.DOUBLE, false),
	WORKED_HOURS("HORAS_TRABAJADAS", VariableType.DOUBLE, false),
	EXTRA_HOURS("HORAS_EXTRAS", VariableType.DOUBLE, false),
	ADDITIONAL_HOURS("HORAS_COMPLEMENTARIAS", VariableType.DOUBLE, false),

	NIGHT("NOCTURNO", VariableType.BOOLEAN, false),
	// NIGHT_HOURS("HORAS_NOCHE", VariableType.INTEGER, false),

	HOLIDAY_AMOUNT("IMPORTE_DIA_VACACIONES", VariableType.DOUBLE, false),
	COMPENSATION_AMOUNT("IMPORTE_INDEMNIZACION", VariableType.DOUBLE, false),

	// Bases
	CGC_BASE("BASE_CGC", VariableType.DOUBLE, false), 
	CGP_BASE("BASE_CGP", VariableType.DOUBLE, false),
	IRPF_BASE("BASE_IRPF", VariableType.DOUBLE), 
	IPREM_BASE("BASE_IPREM", VariableType.DOUBLE),
	IPREM_BASE_SHORT("BIPREM", VariableType.DOUBLE), 
	CGC_BASE_MIN("BASE_CGC_MIN", VariableType.DOUBLE),
	CGC_BASE_MAX("BASE_CGC_MAX", VariableType.DOUBLE),
	CGC_BASE_MIN_HOUR("BASE_CGC_MIN_HORA", VariableType.DOUBLE),
	MONEY_IRPF_BASE("BASE_IRPF_DINERO", VariableType.DOUBLE), 
	INKIND_IRPF_BASE("BASE_IRPF_ESPECIE", VariableType.DOUBLE), 

	CGP_BASE_MIN("BASE_CGP_MIN", VariableType.DOUBLE), CGP_BASE_MAX("BASE_CGP_MAX", VariableType.DOUBLE),
	SENIOR_BASE("BASE_ANTIGUEDAD", VariableType.DOUBLE), STRUCTURAL_OVERTIME_BASE("BASE_ESTR", VariableType.DOUBLE),
	NON_STRUCTURAL_OVERTIME_BASE("BASE_NESTR", VariableType.DOUBLE), MATERNITY_BASE("BASE_MTNAD", VariableType.DOUBLE),
	ERE_BASE("BASE_ERE", VariableType.DOUBLE), 
	ERE_BASE_FORCE("BASE_ERE_FZA", VariableType.DOUBLE), 
	ERE_BASE_FORCE_OFF("BASE_ERE_FZA_EXONERADO", VariableType.DOUBLE), 
	DIRECT_BASE("BASE_PAGO_DIRECTO", VariableType.DOUBLE),
	LACK_BASE("BASE_PERIODO_CARENCIA", VariableType.DOUBLE),
	ADDITIONAL_BASE("BASE_HORAS_COMPL", VariableType.DOUBLE),
	EXCESS_BASE("BASE_EXCESO", VariableType.DOUBLE, false), 
	UNPAID_BASE("BASE_UNPAID", VariableType.DOUBLE),
	SOLIDARITY_BASE_FIRST("BASE_SOLIDARIDAD_I", VariableType.DOUBLE), 
	SOLIDARITY_BASE_SECOND("BASE_SOLIDARIDAD_II", VariableType.DOUBLE), 
	SOLIDARITY_BASE_THIRD("BASE_SOLIDARIDAD_III", VariableType.DOUBLE), 
	CGP_UNPAID_BASE("BASE_CGP_UNPAID", VariableType.DOUBLE),


	CGC_BASE_RAW("BASE_CGC_BRUTA", VariableType.DOUBLE), CGP_BASE_RAW("BASE_CGP_BRUTA", VariableType.DOUBLE),
	CGC_BASE_REAL("BASE_CGC_REAL", VariableType.DOUBLE), CGP_BASE_REAL("BASE_CGP_REAL", VariableType.DOUBLE),
	CGC_BASE_ENTERPRISE("BASE_CGC_E", VariableType.DOUBLE, false), CGP_BASE_ENTERPRISE("BASE_CGP_E", VariableType.DOUBLE, false),

	REGULATORY_BASE("BASE_REGULADORA", VariableType.DOUBLE, false),

	TOTAL_CGC_BASE("TOTAL_BASE_CGC", VariableType.DOUBLE, false), 
	TOTAL_CGP_BASE("TOTAL_BASE_CGP", VariableType.DOUBLE, false),
	TOTAL_CGC_BASE_ENTERPRISE("TOTAL_BASE_CGC_E", VariableType.DOUBLE, false), 
	TOTAL_CGP_BASE_ENTERPRISE("TOTAL_BASE_CGP_E", VariableType.DOUBLE, false),

	// Cuotas
	CGC_EMPLOYEE("CGC", VariableType.DOUBLE), CGC_ENTERPRISE("CGC_E", VariableType.DOUBLE),
	MEI_EMPLOYEE("MEI", VariableType.DOUBLE), MEI_ENTERPRISE("MEI_E", VariableType.DOUBLE),
	IT_ENTERPRISE("IT_E", VariableType.DOUBLE), IMS_ENTERPRISE("IMS_E", VariableType.DOUBLE),
	FP_EMPLOYEE("FP", VariableType.DOUBLE), FP_ENTERPRISE("FP_E", VariableType.DOUBLE),
	UNEMPLOY_EMPLOYEE("DESMPL", VariableType.DOUBLE), UNEMPLOY_ENTERPRISE("DESMPL_E", VariableType.DOUBLE),
	FOGASA_ENTERPRISE("FOGASA_E", VariableType.DOUBLE), ENTERPRISE_QUOTA("CUOTA_EMPRESARIAL", VariableType.DOUBLE),
	REDUCTION_EMPLOYEE("RED_SS", VariableType.DOUBLE),REDUCTION_ENTERPRISE("RED_SS_E", VariableType.DOUBLE),
	EMPLOYEE_QUOTA("CUOTA_TRABAJADOR", VariableType.DOUBLE), SEA_ENTERPRISE("SEA_E", VariableType.DOUBLE),
	STRUCTURAL_OVERTIME_EMPLOYEE("ESTR", VariableType.DOUBLE), STRUCTURAL_OVERTIME_ENTERPRISE("ESTR_E", VariableType.DOUBLE),
	NON_STRUCTURAL_OVERTIME_EMPLOYEE("NESTR", VariableType.DOUBLE), NON_STRUCTURAL_OVERTIME_ENTERPRISE("NESTR_E", VariableType.DOUBLE),
	SOLIDARITY_EMPLOYEE("SOLIDARIDAD", VariableType.DOUBLE ),SOLIDARITY_ENTERPRISE("SOLIDARIDAD_E", VariableType.DOUBLE ),
	SOLIDARITY_FIRST_EMPLOYEE("SOLIDARIDAD_I", VariableType.DOUBLE),SOLIDARITY_FIRST_ENTERPRISE("SOLIDARIDAD_I_E", VariableType.DOUBLE), 
	SOLIDARITY_SECOND_EMPLOYEE("SOLIDARIDAD_II", VariableType.DOUBLE),SOLIDARITY_SECOND_ENTERPRISE("SOLIDARIDAD_II_E", VariableType.DOUBLE), 
	SOLIDARITY_THIRD_EMPLOYEE("SOLIDARIDAD_III", VariableType.DOUBLE),SOLIDARITY_THIRD_ENTERPRISE("SOLIDARIDAD_III_E", VariableType.DOUBLE),
	CGC_ENTERPRISE_TEMP("CGC_E_TEMP", VariableType.DOUBLE),
	
	

	// Datos 'temporales' del contrato
	
	TC2("TC2", VariableType.TC2_DROP, false), 
	CNO("CNO", VariableType.CNO_LOOKUP, false),
	TRL("TRL", VariableType.STRING, false),
	SMI("SMI", VariableType.DOUBLE, false), 
	IPREM("IPREM", VariableType.DOUBLE, false), 
	CATEGORY("CATEGORIA", VariableType.STRING, false),
	INDEFINITE("INDEFINIDO", VariableType.BOOLEAN, false), OCCUPATION("OCUPACION", VariableType.OCCUPATION_DROP, false),
//	GUARANTEED("GARANTIZADO", VariableType.DOUBLE, false),
	IRREGULAR("IRREGULAR", VariableType.BOOLEAN, false), 
	FULL_TIME("TIEMPO_COMPLETO", VariableType.BOOLEAN, false),
	IRPF_PERCENT("PORCENTAJE_IRPF", VariableType.DOUBLE, false),
	QUOTE_GROUP("GRUPO_COTIZACION", VariableType.QUOTE_GROUP_DROP, false),
	FREE_IPREM("EXENTO_IPREM", VariableType.DOUBLE, false), FREE_IPREM_SHORT("XIPREM", VariableType.DOUBLE, false),
	IT_RATE("TARIFA_IT", VariableType.DOUBLE, false), IMS_RATE("TARIFA_IMS", VariableType.DOUBLE, false),
	IT_PERCENT("PORCENTAJE_IT", VariableType.DOUBLE, false), IMS_PERCENT("PORCENTAJE_IMS", VariableType.DOUBLE, false),
	SHORT_CONTRACT("CONTRATO_CORTA_DURACION", VariableType.BOOLEAN, false),
	SENIORITY("AÑOS_ANTIGUEDAD", VariableType.DOUBLE, false),
	QUOTE_PECULIARITY_COLLECTIVE("COLECT_PECULIAR_COTIZACION", VariableType.INTEGER, false),
	CONTRACT_END_CODE("COD_FIN_CONTRATO", VariableType.INTEGER, false),
	CONTRACT_END_DESC("DESC_FIN_CONTRATO", VariableType.STRING, false),
	PARTIAL_FACTOR("COEFICIENTE_PARCIALIDAD", VariableType.STRING, false),
	ERE_FACTOR("COEFICIENTE_ERE", VariableType.INTEGER, false),
	ERE_FACTOR_FORCE("COEFICIENTE_ERE_FZA", VariableType.INTEGER, false),
	ERE_FACTOR_FORCE_OFF("COEFICIENTE_ERE_FZA_EXONERADO", VariableType.INTEGER, false),
	FULL_ERE("ERE_TOTAL", VariableType.BOOLEAN, false ),
	ERE_BACK("REINCORPORADO_ERE", VariableType.BOOLEAN, false ),
	STRIKE_FACTOR("COEFICIENTE_HUELGA", VariableType.INTEGER, false),
	PATERNITY_FACTOR("COEFICIENTE_PATERNIDAD", VariableType.DOUBLE, false),
	MATERNITY_FACTOR("COEFICIENTE_MATERNIDAD", VariableType.DOUBLE, false),
	WORKED_FACTOR("COEFICIENTE_TRABAJADO", VariableType.STRING, false),
	LEAVE_FACTOR("COEFICIENTE_IT", VariableType.STRING, false),
	DROP_FACTOR("COEFICIENTE_AUSENCIA", VariableType.DOUBLE, false),
	//HOLIDAYS_FACTOR("COEFICIENTE_VACACIONES", VariableType.DOUBLE, false),

	// Bajas, Incapacidad Temporal
	ERE("ERE", VariableType.BOOLEAN), 
	ERE_FORCE("ERE_FZA", VariableType.BOOLEAN), 
	ERE_FORCE_OFF("ERE_FZA_EXONERADO", VariableType.BOOLEAN), 
	MATERNITY("MTNAD", VariableType.BOOLEAN),
	QUOTE_IT("COTIZACION_IT", VariableType.QUOTE_IT_DROP),
	UNPAID("UNPAID", VariableType.BOOLEAN),

	// Quote Regime
	MORE_THAN_65("MAYOR_65", VariableType.BOOLEAN, false),
	ASSIMILATED("ASIMILADO_REGIMEN_GRAL", VariableType.BOOLEAN, false),
	ENTRY_BY_COMPANY_ACCOUNT("INGRESO_AC_EMPRESA", VariableType.BOOLEAN, false),

	// Embargos
	EMBARGO_LEFT("PENDIENTE", VariableType.DOUBLE, false), 
	EMBARGO_PAID("EMBARGADO", VariableType.DOUBLE, false),
	EMBARGO_LIMIT("EMBARGABLE", VariableType.DOUBLE, false), 
	EMBARGO_MAX("MAX_EMBARGABLE", VariableType.DOUBLE, false),
	TOTAL_EMBARGO("TOTAL_EMBARGADO", VariableType.DOUBLE, false),


	// Salary Type
	SALARY("NOMINA", VariableType.BOOLEAN), 
	DELAY("ATRASOS", VariableType.BOOLEAN),
	SETTLE("FINIQUITO", VariableType.BOOLEAN), 
	EXTRA_PAY("EXTRA", VariableType.BOOLEAN),

	// Results
	TOTAL_LIQUID("TOTAL_LIQUIDO", VariableType.DOUBLE), 
	TOTAL_PAYMENT("TOTAL_DEVENGADO", VariableType.DOUBLE),

	//
	CURRENT("ACTUAL", VariableType.UNKNOWN),

	// Datos de las bonificaciones
	BONUS_AGE("DURACION", VariableType.DATE, false), SUBSIDIZED("BONIFICADO", VariableType.BOOLEAN),
	BONUS_START("INICIO_BONIFICACION", VariableType.DATE, false),

	// Excel
	OR("O", VariableType.BOOLEAN), 
	AND("Y", VariableType.BOOLEAN), 
	IF("SI", VariableType.BOOLEAN),
	NOT("NO", VariableType.BOOLEAN), 
	FALSE("FALSO", VariableType.BOOLEAN), 
	TRUE("VERDADERO", VariableType.BOOLEAN),
	ABS("ABS", VariableType.DOUBLE), 
	POW("POTENCIA", VariableType.DOUBLE), 
	SQRT("RAIZ", VariableType.DOUBLE),
	INTEGER("ENTERO", VariableType.INTEGER), 
	QUOTIENT("COCIENTE", VariableType.INTEGER),
	DAYS("DIAS", VariableType.INTEGER), 
	DATE("FECHA", VariableType.DATE), 
	MONTH("MES", VariableType.INTEGER),
	YEAR("AÑO", VariableType.INTEGER), 
	DAY("DIA", VariableType.INTEGER), 
	ROUND("ROUND", VariableType.DOUBLE),
	FLOOR("FLOOR", VariableType.INTEGER),
	

	

	// AON's
	MIN("MIN", VariableType.UNKNOWN), 
	MAX("MAX", VariableType.UNKNOWN), 
	ONE("ANUAL", VariableType.UNKNOWN),
	TWO("BIENIO", VariableType.UNKNOWN), 
	THREE("TRIENIO", VariableType.UNKNOWN, true),
	FOUR("CUATRIENIO", VariableType.UNKNOWN, true), 
	FIVE("QUINQUENIO", VariableType.UNKNOWN, true),
	SIX("SEXENIO", VariableType.UNKNOWN, true), 
	SEVEN("SEPTENIO", VariableType.UNKNOWN, true),
	UNDEFINED("UNDEFINED", VariableType.UNKNOWN), 
	TRACE("TRACE", VariableType.UNKNOWN),
	MONTH_START("INICIO_MES", VariableType.DATE),
	YEAR_START("INICIO_AÑO", VariableType.DATE),
	MONTH_END("FIN_MES", VariableType.DATE),
	YEAR_END("FIN_AÑO", VariableType.DATE),
	TODAY("TODAY", VariableType.DATE),
	FORMAT("FORMAT", VariableType.INTEGER), 
	DATES("FECHAS", VariableType.INTEGER), 
	EVAL_TEMPLATE("EVAL_TEMPLATE", VariableType.UNKNOWN),
	CGPJ_COMPENSATIONS("CGPJ_INDEMNIZACIONES", VariableType.UNKNOWN),
	CALC_COMPENSATIONS("CALCULO_INDEMNIZACIONES", VariableType.UNKNOWN),

	OLD("ANTIGÜEDAD", VariableType.DOUBLE), 
	EXCESS("EXCESO", VariableType.DOUBLE),
	EVERYTHING("TODO", VariableType.DOUBLE),

	IT_START("INICIO_IT", VariableType.DATE),
	IT_END("FIN_IT", VariableType.DATE),
	IT_LENGTH("DURACION_IT", VariableType.INTEGER),

	PROFESSION("PROFESION", VariableType.STRING), ENTERPRISE_SITE_DATE("VISIONADOE", VariableType.DATE),

	PAYMENT("_P", VariableType.DOUBLE),

	NULL("NADA", VariableType.UNKNOWN), UNFAIR("IMPROCEDENTE", VariableType.UNKNOWN),
	OBJECTIVE("PROCEDENTE", VariableType.UNKNOWN), CONTRACT_COMPLETE("FIN", VariableType.UNKNOWN),
	WORK_COMPLETE("FIN_OBRA", VariableType.UNKNOWN), TEMP_COMPLETE("FIN_TEMPORAL", VariableType.UNKNOWN),
	CONDITIONS_CHANGE("CAMBIO_CONDICIONES", VariableType.UNKNOWN),
	NOT_PASS_TRIAL_PERIOD("BAJA_PERIODO_PRUEBA", VariableType.UNKNOWN),
	DEATH_OF_EMPLOYEE("FALLECIMIENTO_TRABAJADOR", VariableType.UNKNOWN),
	VOLUNTARY_END("BAJA_VOLUNTARIA", VariableType.UNKNOWN),
	RETIREMENT("JUBILACION", VariableType.UNKNOWN),

	NON_WORKING("NO_LABORABLE", VariableType.UNKNOWN),

//	PREST_IT("PREST_IT", VariableType.DOUBLE),
	DIRECT_PAY("PAGO_DIRECTO", VariableType.BOOLEAN),
	LACK_PERIOD("PERIODO_CARENCIA", VariableType.BOOLEAN),

	MONTHLY_SALARY("MODALIDAD_MENSUAL", VariableType.BOOLEAN, false),

	ON_ACCOUNT_AGREEMENT("A_CUENTA_CONVENIO", VariableType.UNKNOWN),

	REGIME("REGIMEN", VariableType.UNKNOWN),
	HOME("HOGAR", VariableType.UNKNOWN),
	GENERAL("GENERAL", VariableType.UNKNOWN),
	ARTISTS("ARTISTAS", VariableType.UNKNOWN),
	AGRARIAN("AGRARIO", VariableType.UNKNOWN),
	FELLOWS("BECARIOS", VariableType.UNKNOWN),
	TRAINING("FORMACION", VariableType.UNKNOWN),
	LEARNING("APRENDIZAJE", VariableType.UNKNOWN),
	ASSIMILATE("ASIMILADOS", VariableType.UNKNOWN),
	REPRESENTATIVES("REPRESENTANTES", VariableType.UNKNOWN),
	
	CCC_TYPE("CCC_TYPE", VariableType.INTEGER),
	
	SLD_C737("BONIFICACION_TUTORIA", VariableType.DOUBLE, false),
	SLD_H06("HORAS_TUTORIA", VariableType.INTEGER, false),
	SLD_H03("HORAS_FORMACION_PRESENCIAL", VariableType.INTEGER, false),
	SLD_H04("HORAS_FORMACION_DISTANCIA", VariableType.INTEGER, false),
	SLD_C763("BONIFICACION_FORMACION_CONTINUA", VariableType.DOUBLE, false),

	
	DO_DAYS("JORNADAS_REALES", VariableType.DOUBLE, false),
	IF_DAYS("JORNADAS_TEORICAS", VariableType.DOUBLE, false),

	
	TOTAL_WORKED_DAYS("DIAS_TRABAJADOS_TOTALES", VariableType.DOUBLE, false),
	TOTAL_DAYS("DIAS_TOTALES", VariableType.DOUBLE, false),
	TOTAL_DO_DAYS("JORNADAS_REALES_TOTALES", VariableType.DOUBLE, false),

	EFECTIVE_START("INICIO_EFECTIVO", VariableType.DATE, false),
	EFECTIVE_END("FIN_EFECTIVO", VariableType.DATE, false),
	
	IN_KIND("EN_ESPECIE",  VariableType.DOUBLE, false),
	TMP_IN_KIND("_EN_ESPECIE",  VariableType.DOUBLE, false),
	IRPF_CTA_ESP("IRPF_CTA_ESP",  VariableType.DOUBLE, false),
	BASE_CTA_ESP("BASE_CTA_ESP",  VariableType.DOUBLE, false),

	BASE_PPE("BASE_PPE",  VariableType.DOUBLE, false),

	// Percentages
	CGC_EMPLOYEE_PERCENT("PORCENTAJE_CGC", VariableType.DOUBLE,false), 
	MEI_EMPLOYEE_PERCENT("PORCENTAJE_MEI", VariableType.DOUBLE,false), 
	CGC_ENTERPRISE_PERCENT("PORCENTAJE_CGC_E", VariableType.DOUBLE,false),
	MEI_ENTERPRISE_PERCENT("PORCENTAJE_MEI_E", VariableType.DOUBLE,false),
	IT_ENTERPRISE_PERCENT("TARIFA_IT", VariableType.DOUBLE,false), 
	IMS_ENTERPRISE_PERCENT("TARIFA_IMS", VariableType.DOUBLE,false),
	FP_EMPLOYEE_PERCENT("PORCENTAJE_FP", VariableType.DOUBLE,false), 
	FP_ENTERPRISE_PERCENT("PORCENTAJE_FP_E", VariableType.DOUBLE,false),
	UNEMPLOY_EMPLOYEE_PERCENT("PORCENTAJE_DESMPL", VariableType.DOUBLE, false), 
	UNEMPLOY_ENTERPRISE_PERCENT("PORCENTAJE_DESMPL_E", VariableType.DOUBLE, false),
	FOGASA_ENTERPRISE_PERCENT("PORCENTAJE_FOGASA", VariableType.DOUBLE, false), 
	SOLIDARITY_EMPLOYEE_PERCENT("PORCENTAJE_SOLIDARIDAD", VariableType.DOUBLE,false), 
	SOLIDARITY_ENTERPRISE_PERCENT("PORCENTAJE_SOLIDARIDAD_E", VariableType.DOUBLE,false), 
	NON_STRUCTURAL_OVERTIME_EMPLOYEE_PERCENT("PORCENTAJE_NEXTR", VariableType.DOUBLE,false), 
	NON_STRUCTURAL_OVERTIME_ENTERPRISE_PERCENT("PORCENTAJE_NEXTR", VariableType.DOUBLE,false), 
	SOLIDARITY_FIRST_EMPLOYEE_PERCENT("PORCENTAJE_SOLIDARIDAD_I", VariableType.DOUBLE,false), 
	SOLIDARITY_FIRST_ENTERPRISE_PERCENT("PORCENTAJE_SOLIDARIDAD_I_E", VariableType.DOUBLE,false), 
	SOLIDARITY_SECOND_EMPLOYEE_PERCENT("PORCENTAJE_SOLIDARIDAD_II", VariableType.DOUBLE,false), 
	SOLIDARITY_SECOND_ENTERPRISE_PERCENT("PORCENTAJE_SOLIDARIDAD_II_E", VariableType.DOUBLE,false), 
	SOLIDARITY_THIRD_EMPLOYEE_PERCENT("PORCENTAJE_SOLIDARIDAD_III", VariableType.DOUBLE,false), 
	SOLIDARITY_THIRD_ENTERPRISE_PERCENT("PORCENTAJE_SOLIDARIDAD_III_E", VariableType.DOUBLE,false), 
	
	DELAY_AMOUNT("ATRASO", VariableType.DOUBLE, false ),
	DELAY_QUOTE("COTIZACION_ATRASO", VariableType.DOUBLE, false ),
	DELAY_EXTRA_HOURS("HORAS_EXTRA_ATRASO", VariableType.DOUBLE, false ),
	
	JANUARY("ENERO", VariableType.INTEGER),
	FEBRUARY("FEBRERO", VariableType.INTEGER),
	MARCH("MARZO", VariableType.INTEGER),
	APRIL("ABRIL", VariableType.INTEGER),
	MAY("MAYO", VariableType.INTEGER),
	JUNE("JUNIO", VariableType.INTEGER),
	JULY("JULIO", VariableType.INTEGER),
	AUGUST("AGOSTO", VariableType.INTEGER),
	SEPTEMBER("SEPTIEMBRE", VariableType.INTEGER),
	OCTOBER("OCTUBRE", VariableType.INTEGER),
	NOVEMBER("NOVIEMBRE", VariableType.INTEGER),
	DECEMBER("DICIEMBRE", VariableType.INTEGER),
	
	DELAY_CAUSE("CAUSA_ATRASO", VariableType.INTEGER, false),
	CRA_0009("CRA_0009", VariableType.INTEGER),
	CRA_0011("CRA_0011", VariableType.INTEGER),
	CRA_0012("CRA_0012", VariableType.INTEGER),
	CRA_0010("CRA_0010", VariableType.INTEGER),
	CRA_0008("CRA_0008", VariableType.INTEGER),
	CRA_0033("CRA_0033", VariableType.INTEGER),

	ADDITIONAL("HORAS_COMPL", VariableType.DOUBLE, false),

	HOURLY_BASE("BASE_HORARIA", VariableType.BOOLEAN, false),
	
	PAY_PRORRATED("PAGAS_PRORRATEADAS", VariableType.BOOLEAN),
	
	MONTHLY_ADJUST("AJUSTE_MENSUAL", VariableType.BOOLEAN),
	
	EXTRA_PAYMENT("EXTRA_DEVENGADO", VariableType.DOUBLE),

	IRPF_START("INICIO_IRPF", VariableType.DATE, false),
	
	BOE_A_2024_26917_START("INICIO_BOE_A_2024_26917", VariableType.DATE, false),
	
	
	MONTHLY("COTIZACION_MENSUAL", VariableType.BOOLEAN, false),
	
	PLUS_BASE("BASE_ADICIONAL", VariableType.DOUBLE),
	
	NEW_DELAY("NUEVO_ATRASO", VariableType.BOOLEAN, false),
	;

	public static final String ALL = "_P";
	public static final String SELF = "SELF";
	public static final String HIDE = "HIDE";
	public static final String READ = "READ";
	public static final String REMOVE = "REMOVE";
	public static final String DISABLE = "DISABLE";
	public static final String CONTEXT = "CONTEXT";

	public static final String BR = "BR";
	public static final String SUM = "SUM";
	public static final String GROSS = "BRUTO";
	public static final String LIQUID = "NETO";
	public static final String SYSTEM = "SISTEMA";
	public static final String GUARANTEE = "GTZDO";
	public static final String AGREEMENT = "CONVENIO";
	public static final String DELAY_PREVENT = "ANTICIPO_ATRASOS";

	public static final String PPE_DELAYS = "ATRASOS_PPE";
	
	// Old
	public static final String CHECK = "CHECK";
	public static final String INPUT = "INPUT";
	public static final String MONTHS = "MESES";
	public static final String SECTION = "TRAMO";
	public static final String WARNING = "AVISO";
	public static final String SCOPE = "AMBITO";
	public static final String ISDEF = "DEFINIDA";
	public static final String ISREAD = "UTILIZADA";
	public static final String IFNDEF = "IFNDEF";
	public static final String SUMIFDEF = "SUMIFDEF";
	public static final String CHECK_DEF = "CHECK_DEF";
	public static final String CHECK_VAR = "CHECK_VAR";
	public static final String REDEFINE = "REDEFINE";
	public static final String PRORATION = "PRORRATEAR";
	public static final String FRACTIONATE = "FRACCIONAR";
	public static final String IS_MONTHLY_DAILY = "MENSUAL_DIARIO";

	public static final String INFO = "INFO";
	public static final String NOTE = "NOTA";
	public static final String CAUTION = "ADVERTENCIA";
	
	public static final String MEDICAL_INSURANCE = "SEGURO_MEDICO";
	
	public static final String FLEXIBLE = "FLEXIBLE";
	public static final String FLEXIBLE_FOOD = "FLEXIBLE_COMIDA";
	public static final String FLEXIBLE_DAYCARE = "FLEXIBLE_GUARDERIA";
	public static final String FLEXIBLE_INSURANCE = "FLEXIBLE_SEGURO";
	public static final String FLEXIBLE_TRAINING = "FLEXIBLE_FORMACION";
	public static final String FLEXIBLE_TRANSPORT = "FLEXIBLE_TRANSPORTE";
	public static final String FLEXIBLE_PREMIUM_EMPLOYEE = "FLEXIBLE_PRIMA_TRABAJ";
	public static final String FLEXIBLE_PREMIUM_FAMILY = "FLEXIBLE_PRIMA_FAMILIAR";
	
	public static final String PPE = "PPE";
	public static final String GEROA = "GEROA";
	public static final String OLDD = "ANTIGUEDAD";
	public static final String PREST_IT = "PREST_IT";
	public static final String BASE_SALARY = "SALARIO_BASE";
	public static final String IMPROVEMENT = "MEJORA";
	public static final String GUARENTEED = "GARANTIZADO";
	public static final String GUARENTEE_IT = "GARANTIZADO_IT";
	public static final String MONTHLY_PAYMENTS = "MENSUALIDAD";
	public static final String TEMP_PAYMENT = "DEVENGO_TEMPORAL";
	public static final String FLEXIBLE_DISCOUNT = "DTO_FLEXIBLE";
	
	public static final String  SUBTRACT_IT_DAY = "RESTAR_DIA_IT";
	

	public static final String PAYMENT_VARIABLE = "CONCEPTO";
	
	public static final String HIDE_BASE_CGC_MIN = "HIDE_BASE_CGC_MIN";
	
	
	private static final List<String> NAMES =
	Arrays.asList(new String []{
			ALL,
			SELF,
			HIDE,
			REMOVE,
			DISABLE,
			CONTEXT,
			BR,
			SUM,
			GROSS,
			LIQUID,
			SYSTEM,
			GUARANTEE,
			AGREEMENT,
			DELAY_PREVENT,
			CHECK,
			INPUT,
			MONTHS,
			SECTION,
			WARNING,
			ISDEF,
			ISREAD,
			CHECK_VAR,
			REDEFINE,
			PRORATION,
			FRACTIONATE,
			IS_MONTHLY_DAILY,
			GEROA,
			OLDD,
			PREST_IT,
			BASE_SALARY,
			GUARENTEED,
			GUARENTEE_IT,
	}); 

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public static ContextVariable [] ERES  =
	Arrays.stream(ContextVariable.values())
	.filter(v->v.getName().startsWith(ERE.getName()))
	.toArray(ContextVariable[]::new);
	
	public static ContextVariable [] ERE_BASES  =
	Arrays.stream(ContextVariable.values())
	.filter(v->v.getName().startsWith(ERE_BASE.getName()))
	.toArray(ContextVariable[]::new);

	public static ContextVariable [] SOLIDARITY_BASES  = {
		SOLIDARITY_BASE_FIRST,
		SOLIDARITY_BASE_SECOND,
		SOLIDARITY_BASE_THIRD
	};

	public static ContextVariable [] ERE_DAYSS  =
	Arrays.stream(ContextVariable.values())
	.filter(v->v.getName().startsWith(ERE_DAYS.getName()))
	.toArray(ContextVariable[]::new);
	
	public static ContextVariable [] ERE_FACTORS  =
	Arrays.stream(ContextVariable.values())
	.filter(v->v.getName().startsWith(ERE_FACTOR.getName()))
	.toArray(ContextVariable[]::new);

	public static String [] ERE_FACTORS_NAMES  =
	Arrays.stream(ContextVariable.values())
	.map( v -> v.getName())
	.filter(s -> s.startsWith(ERE_FACTOR.getName()))
	.toArray(String[]::new);

	public static String [] LOGS  = { CAUTION, NOTE, INFO};
	
	public static ContextVariable [] FREES  =
	new ContextVariable [] { UNPAID, DIRECT_PAY, MATERNITY, LACK_PERIOD} ;

	public static ContextVariable [] FREE_BASES  =
	new ContextVariable [] { UNPAID_BASE, DIRECT_BASE, MATERNITY_BASE, LACK_BASE} ;

	public static final Set<String> FLEXIBLES = Stream
			.of(FLEXIBLE, FLEXIBLE_FOOD, FLEXIBLE_DAYCARE, FLEXIBLE_TRAINING, FLEXIBLE_INSURANCE, FLEXIBLE_PREMIUM_EMPLOYEE, FLEXIBLE_PREMIUM_FAMILY, FLEXIBLE_TRANSPORT, FLEXIBLE_DISCOUNT)
			.collect(Collectors.toSet());

	private final String name;
	private VariableType type;
	private final boolean internal;

	private ContextVariable(String name, VariableType type) {
		this(name, type, true);
	}

	private ContextVariable(String name, VariableType type, boolean internal) {
		this.name = name;
		this.type = type;
		this.internal = internal;
	}

	public String getName() {
		return name;
	}

	public VariableType getType() {
		return type;
	}

	public boolean isInternal() {
		return internal;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns a <code>String</code> with the transalation <code>Locale</code> for
	 * the locale.
	 * 
	 * @param locale Required Locale.
	 * 
	 * @return String a <code>String</code>.
	 */
	@Override
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + super.toString());
	}

	public String getDescription() {
		return getDescription(new Locale("es", "ES"));
	}

	public String getDescription(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(EXT_MSG_KEY_PREFIX + super.toString());
	}

	/** Message key prefix. */
	private static final String MSG_KEY_PREFIX = "aon_enum_context_variable_";
	private static final String EXT_MSG_KEY_PREFIX = "aon_enum_context_variable_desc_";

	public static ContextVariable getVariableByName(String name) {
		for (ContextVariable cv : values()) {
			if (cv.getName().equals(name)) {
				return cv;
			}
		}
		return null;
	}

	public static boolean isContextVariable(String name) {
		return getVariableByName(name) != null  || NAMES.contains(name);
	}
	
	public static List<String> getNames() {
		return NAMES;
	}

	public static Date parse(String str) throws ParseException {
		return StringUtils.isBlank(str) ? null : DATE_FORMAT.parse(str);
	}

	public static double br() {
		return 666;
	}

	public static String getDecimalNameFor(Double round) {
	    return String.format(Locale.ROOT, "DECIMAL_%.2f", round);
	}

}
