package com.esferalia.aon.payroll.enumeration;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_BASE;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.IResourceable;

public enum ContextVariable implements IResourceable {

	START("INICIO", VariableType.DATE), END("FIN", VariableType.DATE), SALARY_START("INICIO_NOMINA", VariableType.DATE),
	SALARY_END("FIN_NOMINA", VariableType.DATE), CONTRACT_START("INICIO_CONTRATO", VariableType.DATE),
	CONTRACT_END("FIN_CONTRATO", VariableType.DATE), SENIORITY_START("INICIO_ANTIGUEDAD", VariableType.DATE),
	DIRECT_PAY_START("INICIO_PAGO_DIRECTO", VariableType.DATE, false),

	// Datos de la persona
	AGE("EDAD", VariableType.INTEGER, false), GENDER("SEXO", VariableType.UNKNOWN, false),
	MALE("HOMBRE", VariableType.UNKNOWN, false), FEMALE("MUJER", VariableType.UNKNOWN, false),

	// Dias
	YEAR_DAYS("DIAS_AÑO", VariableType.INTEGER, false), MONTH_DAYS("DIAS_MES", VariableType.INTEGER, false),
	NATURAL_MONTH_DAYS("DIAS_NATURALES_MES", VariableType.INTEGER, false),
	HOLIDAYS("DIAS_VACACIONES", VariableType.INTEGER, false),
	NO_HOLIDAYS("DIAS_VACACIONES_NO_DISFRUTADOS", VariableType.INTEGER, false),
	WORKED_DAYS("DIAS_TRABAJADOS", VariableType.INTEGER, false), WEEK_DAYS("DIAS_SEMANA", VariableType.INTEGER, false),
	CONTRACT_DAYS("DIAS_CANONTRATO", VariableType.INTEGER, false),
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

	// ADVANCE_NOTICE_DATE("FECHA_PREAVISO", VariableType.DATE, false),
	// ADVANCE_NOTICE_DAYS("DIAS_PREAVISO", VariableType.INTEGER, false),

	COMPENSATION_DAYS("DIAS_INDEMNIZACION", VariableType.INTEGER, false),
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
	IRPF_BASE("BASE_IRPF", VariableType.DOUBLE), IPREM_BASE("BASE_IPREM", VariableType.DOUBLE),
	IPREM_BASE_SHORT("BIPREM", VariableType.DOUBLE), CGC_BASE_MIN("BASE_CGC_MIN", VariableType.DOUBLE),
	CGC_BASE_MAX("BASE_CGC_MAX", VariableType.DOUBLE),

	CGP_BASE_MIN("BASE_CGP_MIN", VariableType.DOUBLE), CGP_BASE_MAX("BASE_CGP_MAX", VariableType.DOUBLE),
	SENIOR_BASE("BASE_ANTIGUEDAD", VariableType.DOUBLE), STRUCTURAL_OVERTIME_BASE("BASE_ESTR", VariableType.DOUBLE),
	NON_STRUCTURAL_OVERTIME_BASE("BASE_NESTR", VariableType.DOUBLE), MATERNITY_BASE("BASE_MTNAD", VariableType.DOUBLE),
	ERE_BASE("BASE_ERE", VariableType.DOUBLE), 
	ERE_BASE_FORCE("BASE_ERE_FZA", VariableType.DOUBLE), 
	ERE_BASE_FORCE_OFF("BASE_ERE_FZA_EXONERADO", VariableType.DOUBLE), 
	DIRECT_BASE("BASE_PAGO_DIRECTO", VariableType.DOUBLE),
	ADDITIONAL_BASE("BASE_HORAS_COMPL", VariableType.DOUBLE),

	CGC_BASE_RAW("BASE_CGC_BRUTA", VariableType.DOUBLE), CGP_BASE_RAW("BASE_CGP_BRUTA", VariableType.DOUBLE),
	CGC_BASE_ENTERPRISE("BASE_CGC_E", VariableType.DOUBLE, false), CGP_BASE_ENTERPRISE("BASE_CGP_E", VariableType.DOUBLE, false),

	REGULATORY_BASE("BASE_REGULADORA", VariableType.DOUBLE, false),

	// Cuotas
	CGC_EMPLOYEE("CGC", VariableType.DOUBLE), CGC_ENTERPRISE("CGC_E", VariableType.DOUBLE),
	IT_ENTERPRISE("IT_E", VariableType.DOUBLE), IMS_ENTERPRISE("IMS_E", VariableType.DOUBLE),
	FP_EMPLOYEE("FP", VariableType.DOUBLE), FP_ENTERPRISE("FP_E", VariableType.DOUBLE),
	UNEMPLOY_EMPLOYEE("DESMPL", VariableType.DOUBLE), UNEMPLOY_ENTERPRISE("DESMPL_E", VariableType.DOUBLE),
	FOGASA_ENTERPRISE("FOGASA_E", VariableType.DOUBLE), ENTERPRISE_QUOTA("CUOTA_EMPRESARIAL", VariableType.DOUBLE),
	EMPLOYEE_QUOTA("CUOTA_TRABAJADOR", VariableType.DOUBLE),

	// Datos 'temporales' del contrato
	
	TC2("TC2", VariableType.TC2_DROP, false), CNO("CNO", VariableType.CNO_LOOKUP, false),
	IPREM("IPREM", VariableType.DOUBLE, false), CATEGORY("CATEGORIA", VariableType.STRING, false),
	INDEFINITE("INDEFINIDO", VariableType.BOOLEAN, false), OCCUPATION("OCUPACION", VariableType.OCCUPATION_DROP, false),
//	GUARANTEED("GARANTIZADO", VariableType.DOUBLE, false),
	IRREGULAR("IRREGULAR", VariableType.BOOLEAN, false), 
	FULL_TIME("TIEMPO_COMPLETO", VariableType.BOOLEAN, false),
	IRPF_PERCENT("PORCENTAJE_IRPF", VariableType.DOUBLE, false),
	QUOTE_GROUP("GRUPO_COTIZACION", VariableType.QUOTE_GROUP_DROP, false),
	FREE_IPREM("EXENTO_IPREM", VariableType.DOUBLE, false), FREE_IPREM_SHORT("XIPREM", VariableType.DOUBLE, false),
	IT_RATE("TARIFA_IT", VariableType.DOUBLE, false), IMS_RATE("TARIFA_IMS", VariableType.DOUBLE, false),
	SHORT_CONTRACT("CONTRATO_CORTA_DURACION", VariableType.BOOLEAN, false),
	SENIORITY("AÑOS_ANTIGUEDAD", VariableType.DOUBLE, false),
	QUOTE_PECULIARITY_COLLECTIVE("COLECT_PECULIAR_COTIZACION", VariableType.INTEGER, false),
	CONTRACT_END_CODE("COD_FIN_CONTRATO", VariableType.INTEGER, false),
	CONTRACT_END_DESC("DESC_FIN_CONTRATO", VariableType.STRING, false),
	PARTIAL_FACTOR("COEFICIENTE_PARCIALIDAD", VariableType.STRING, false),
	ERE_FACTOR("COEFICIENTE_ERE", VariableType.INTEGER, false),
	ERE_FACTOR_FORCE("COEFICIENTE_ERE_FZA", VariableType.INTEGER, false),
	ERE_FACTOR_FORCE_OFF("COEFICIENTE_ERE_FZA_EXONERADO", VariableType.INTEGER, false),
	STRIKE_FACTOR("COEFICIENTE_HUELGA", VariableType.INTEGER, false),
	PATERNITY_FACTOR("COEFICIENTE_PATERNIDAD", VariableType.DOUBLE, false),
	MATERNITY_FACTOR("COEFICIENTE_MATERNIDAD", VariableType.DOUBLE, false),

	// Bajas, Incapacidad Temporal
	ERE("ERE", VariableType.BOOLEAN), 
	ERE_FORCE("ERE_FZA", VariableType.BOOLEAN), 
	ERE_FORCE_OFF("ERE_FZA_EXONERADO", VariableType.BOOLEAN), 
	MATERNITY("MTNAD", VariableType.BOOLEAN),
	QUOTE_IT("COTIZACION_IT", VariableType.QUOTE_IT_DROP),

	// Quote Regime
	MORE_THAN_65("MAYOR_65", VariableType.BOOLEAN, false),
	ASSIMILATED("ASIMILADO_REGIMEN_GRAL", VariableType.BOOLEAN, false),
	ENTRY_BY_COMPANY_ACCOUNT("INGRESO_AC_EMPRESA", VariableType.BOOLEAN, false),

	// Embargos
	EMBARGO_LEFT("PENDIENTE", VariableType.DOUBLE, false), EMBARGO_PAID("EMBARGADO", VariableType.DOUBLE, false),
	EMBARGO_LIMIT("EMBARGABLE", VariableType.DOUBLE, false), EMBARGO_MAX("MAX_EMBARGABLE", VariableType.DOUBLE, false),

	// Salary Type
	SALARY("NOMINA", VariableType.BOOLEAN), DELAY("ATRASOS", VariableType.BOOLEAN),
	SETTLE("FINIQUITO", VariableType.BOOLEAN), EXTRA_PAY("EXTRA", VariableType.BOOLEAN),

	// Results
	TOTAL_LIQUID("TOTAL_LIQUIDO", VariableType.DOUBLE), 
	TOTAL_PAYMENT("TOTAL_DEVENGADO", VariableType.DOUBLE),

	//
	CURRENT("ACTUAL", VariableType.UNKNOWN),

	// Datos de las bonificaciones
	BONUS_AGE("DURACION", VariableType.DATE, false), SUBSIDIZED("BONIFICADO", VariableType.BOOLEAN),
	BONUS_START("INICIO_BONIFICACION", VariableType.DATE, false),

	// Excel
	OR("O", VariableType.BOOLEAN), AND("Y", VariableType.BOOLEAN), IF("SI", VariableType.BOOLEAN),
	NOT("NO", VariableType.BOOLEAN), FALSE("FALSO", VariableType.BOOLEAN), TRUE("VERDADERO", VariableType.BOOLEAN),
	ABS("ABS", VariableType.DOUBLE), POW("POTENCIA", VariableType.DOUBLE), SQRT("RAIZ", VariableType.DOUBLE),
	INTEGER("ENTERO", VariableType.INTEGER), QUOTIENT("COCIENTE", VariableType.INTEGER),
	DAYS("DIAS", VariableType.INTEGER), DATE("FECHA", VariableType.DATE), MONTH("MES", VariableType.INTEGER),
	YEAR("AÑO", VariableType.INTEGER), DAY("DIA", VariableType.INTEGER), ROUND("ROUND", VariableType.DOUBLE),
	FLOOR("FLOOR", VariableType.INTEGER),

	// AON's
	MIN("MIN", VariableType.UNKNOWN), MAX("MAX", VariableType.UNKNOWN), ONE("ANUAL", VariableType.UNKNOWN),
	TWO("BIENIO", VariableType.UNKNOWN), THREE("TRIENIO", VariableType.UNKNOWN, true),
	FOUR("CUATRIENIO", VariableType.UNKNOWN, true), FIVE("QUINQUENIO", VariableType.UNKNOWN, true),
	SIX("SEXENIO", VariableType.UNKNOWN, true), SEVEN("SEPTENIO", VariableType.UNKNOWN, true),
	UNDEFINED("UNDEFINED", VariableType.UNKNOWN), TRACE("TRACE", VariableType.UNKNOWN),

	OLD("ANTIGÜEDAD", VariableType.DOUBLE), EXCESS("EXCESO", VariableType.DOUBLE),
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

	NON_WORKING("NO_LABORABLE", VariableType.UNKNOWN),

//	PREST_IT("PREST_IT", VariableType.DOUBLE),
	DIRECT_PAY("PAGO_DIRECTO", VariableType.BOOLEAN),

	MONTHLY_SALARY("MODALIDAD_MENSUAL", VariableType.BOOLEAN),

	ON_ACCOUNT_AGREEMENT("A_CUENTA_CONVENIO", VariableType.UNKNOWN),

	CCC_TYPE("CCC_TYPE", VariableType.INTEGER)	
	;

	public static final String ALL = "_P";
	public static final String SELF = "SELF";
	public static final String HIDE = "HIDE";
	public static final String REMOVE = "REMOVE";
	public static final String CONTEXT = "CONTEXT";

	public static final String BR = "BR";
	public static final String SUM = "SUM";
	public static final String GROSS = "BRUTO";
	public static final String LIQUID = "NETO";
	public static final String SYSTEM = "SISTEMA";
	public static final String GUARANTEE = "GTZDO";
	public static final String AGREEMENT = "CONVENIO";
	public static final String DELAY_PREVENT = "ANTICIPO_ATRASOS";

	// Old
	public static final String CHECK = "CHECK";
	public static final String INPUT = "INPUT";
	public static final String MONTHS = "MESES";
	public static final String SECTION = "TRAMO";
	public static final String WARNING = "AVISO";
	public static final String ISDEF = "DEFINIDA";
	public static final String ISREAD = "UTILIZADA";
	public static final String CHECK_VAR = "CHECK_VAR";
	public static final String REDEFINE = "REDEFINE";
	public static final String PRORATION = "PRORRATEAR";
	public static final String FRACTIONATE = "FRACCIONAR";
	public static final String IS_MONTHLY_DAILY = "MENSUAL_DIARIO";

	public static final String GEROA = "GEROA";
	public static final String OLDD = "ANTIGUEDAD";
	public static final String PREST_IT = "PREST_IT";
	public static final String BASE_SALARY = "SALARIO_BASE";
	public static final String GUARENTEED = "GARANTIZADO";
	public static final String GUARENTEE_IT = "GARANTIZADO_IT";
	public static final String MONTHLY_PAYMENTS = "MENSUALIDAD";
	public static final String TEMP_PAYMENT = "DEVENGO_TEMPORAL";
	

	public static final String PAYMENT_VARIABLE = "CONCEPTO";
	
	private static final List<String> NAMES =
	Arrays.asList(new String []{
			ALL,
			SELF,
			HIDE,
			REMOVE,
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
	
	public static ContextVariable [] ERE_DAYSS  =
	Arrays.stream(ContextVariable.values())
	.filter(v->v.getName().startsWith(ERE_DAYS.getName()))
	.toArray(ContextVariable[]::new);
	
	public static ContextVariable [] ERE_FACTORS  =
	Arrays.stream(ContextVariable.values())
	.filter(v->v.getName().startsWith(ERE_FACTOR.getName()))
	.toArray(ContextVariable[]::new);

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

	public static Date parse(String str) throws ParseException {
		return StringUtils.isBlank(str) ? null : DATE_FORMAT.parse(str);
	}

	public static double br() {
		return 666;
	}

}
