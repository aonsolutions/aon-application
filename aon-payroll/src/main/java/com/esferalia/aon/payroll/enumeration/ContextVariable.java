package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContextVariable implements IResourceable{
	

	START("FECHA_INICIO", VariableType.DATE),
	END("FECHA_FINAL", VariableType.DATE),
	SALARY_START("INICIO_NOMINA", VariableType.DATE),
	SALARY_END("FIN_NOMINA", VariableType.DATE),
	
	// Datos de la persona
	AGE("EDAD", VariableType.INTEGER, false),
	GENDER("SEXO", VariableType.UNKNOWN, false),
	MALE("HOMBRE", VariableType.UNKNOWN, false),
	FEMALE("MUJER", VariableType.UNKNOWN, false),

	// Dias
	YEAR_DAYS("DIAS_AÑO", VariableType.INTEGER, false),
	MONTH_DAYS("DIAS_MES", VariableType.INTEGER, false),
	HOLIDAYS("DIAS_VACACIONES", VariableType.INTEGER, false),
	NO_HOLIDAYS("DIAS_VACACIONES_NO_DISFRUTADOS", VariableType.INTEGER, false),
	WORKED_DAYS("DIAS_TRABAJADOS", VariableType.INTEGER, false),
	WEEK_DAYS("DIAS_SEMANA", VariableType.INTEGER, false),
	CONTRACT_DAYS("DIAS_CANONTRATO", VariableType.INTEGER, false),
	SALARY_DAYS("DIAS_NOMINA", VariableType.INTEGER, false),
	PAY_DAYS("DIAS_PAGA", VariableType.INTEGER, false),
	EXTRA_DAYS("DIAS_PAGA", VariableType.INTEGER, false),
	BONUS_DAYS("DIAS_BONIFICACION", VariableType.INTEGER, false),
	QUOTE_DAYS("DIAS_COTIZADOS", VariableType.INTEGER, false),
	ACTUAL_DAYS("DIAS_EFECTIVOS", VariableType.INTEGER, false),
	LEAVE_DAYS("DIAS_IT", VariableType.INTEGER, false),
	GUARANTEED_DAYS("DIAS_GARANTIZADOS", VariableType.INTEGER, false),
	SPECIAL_DAYS("DIAS_ESPECIALES", VariableType.INTEGER, false),
	MATERNITY_DAYS("DIAS_MATERNIDAD", VariableType.INTEGER, false),
	COMMON_DISEASE_DAYS("DIAS_ENFERMEDAD_COMUN", VariableType.INTEGER, false),
	OCCUPATIONAL_DISEASE_DAYS("DIAS_ENFERMEDAD_PROFESIONAL", VariableType.INTEGER, false),
	PAYMENTS("NUM_PAGAS", VariableType.INTEGER, false),

	ADVANCE_NOTICE_DAYS("DIAS_PREAVISO", VariableType.INTEGER, false),
	COMPENSATION_DAYS("DIAS_INDEMNIZACION", VariableType.INTEGER, false),
	WORKED_YEARS("AÑOS_TRABAJADOS", VariableType.INTEGER, false),

	SALARY_MONTHS("MESES_NOMINA", VariableType.INTEGER, false),
	PAY_MONTHS("MESES_PAGA", VariableType.INTEGER, false),
	WORKED_WEEKS("SEMANAS_TRABAJADAS", VariableType.INTEGER, false),
	SALARY_WEEKS("SEMANAS_NOMINA", VariableType.INTEGER, false),
	PAY_WEEKS("SEMANAS_PAGA", VariableType.INTEGER, false),
	
	// Horas ( contratos  a tiempo parcial )
	WEEK_HOURS("HORAS_SEMANA", VariableType.DOUBLE, false),
	SALARY_HOURS("HORAS_NOMINA", VariableType.DOUBLE, false),

	NIGHT("NOCTURNO", VariableType.BOOLEAN, false),
	//NIGHT_HOURS("HORAS_NOCHE", VariableType.INTEGER, false),

	HOLIDAY_AMOUNT("IMPORTE_DIA_VACACIONES", VariableType.DOUBLE, false),
	COMPENSATION_AMOUNT("IMPORTE_INDEMNIZACION", VariableType.DOUBLE, false),

	// Bases 
	CGC_BASE("BASE_CGC", VariableType.DOUBLE),
	CGP_BASE("BASE_CGP", VariableType.DOUBLE),
	IRPF_BASE("BASE_IRPF", VariableType.DOUBLE),
	IPREM_BASE("BASE_IPREM", VariableType.DOUBLE),
	IPREM_BASE_SHORT("BIPREM", VariableType.DOUBLE),
	CGC_BASE_MIN("BASE_CGC_MIN", VariableType.DOUBLE),
	CGC_BASE_MAX("BASE_CGC_MAX", VariableType.DOUBLE),
	
	CGP_BASE_MIN("BASE_CGP_MIN", VariableType.DOUBLE),
	CGP_BASE_MAX("BASE_CGP_MAX", VariableType.DOUBLE),
	SENIOR_BASE("BASE_ANTIGUEDAD", VariableType.DOUBLE),
	REGULATORY_BASE("BASE_REGULADORA", VariableType.DOUBLE),
	STRUCTURAL_OVERTIME_BASE("BASE_ESTR", VariableType.DOUBLE),
	NON_STRUCTURAL_OVERTIME_BASE("BASE_NESTR", VariableType.DOUBLE),
	MATERNITY_BASE("BASE_MTNAD", VariableType.DOUBLE),
	
	// Cuotas
	CGC_EMPLOYEE("CGC", VariableType.DOUBLE),
	CGC_ENTERPRISE("CGC_E", VariableType.DOUBLE),
	IT_ENTERPRISE("IT_E", VariableType.DOUBLE),
	IMS_ENTERPRISE("IT_E", VariableType.DOUBLE),
	FP_EMPLOYEE("FP", VariableType.DOUBLE),
	FP_ENTERPRISE("FP_E", VariableType.DOUBLE),
	UNEMPLOY_EMPLOYEE("DESMPL", VariableType.DOUBLE),
	UNEMPLOY_ENTERPRISE("DESMPL_E", VariableType.DOUBLE),
	FOGASA_ENTERPRISE("FOGASA_E", VariableType.DOUBLE),
	ENTERPRISE_QUOTA("CUOTA_EMPRESARIAL", VariableType.DOUBLE),
	EMPLOYEE_QUOTA("CUOTA_TRABAJADOR", VariableType.DOUBLE),



	// Datos 'temporales' del contrato
	TC2("TC2", VariableType.TC2_DROP, false),
	CNO("CNO", VariableType.CNO_LOOKUP, false),
	IPREM("IPREM", VariableType.DOUBLE, false),
	CATEGORY("CATEGORIA", VariableType.STRING, false),
	INDEFINITE("INDEFINIDO", VariableType.BOOLEAN, false),
	OCCUPATION("OCUPACION", VariableType.OCCUPATION_DROP, false),
	GUARANTEED("GARANTIZADO", VariableType.DOUBLE, false),
	IRREGULAR("IRREGULAR", VariableType.BOOLEAN, false),
	FULL_TIME("TIEMPO_COMPLETO", VariableType.BOOLEAN, false),
	IRPF_PERCENT("PORCENTAJE_IRPF", VariableType.DOUBLE, false),
	QUOTE_GROUP("GRUPO_COTIZACION", VariableType.QUOTE_GROUP_DROP, false),
	FREE_IPREM("EXENTO_IPREM", VariableType.DOUBLE, false),
	FREE_IPREM_SHORT("XIPREM", VariableType.DOUBLE, false),
	IT_RATE("TARIFA_IT", VariableType.DOUBLE, false),
	IMS_RATE("TARIFA_IMS", VariableType.DOUBLE, false),
	SHORT_CONTRACT("CONTRATO_CORTA_DURACION", VariableType.BOOLEAN, false),
	SENIORITY("AÑOS_ANTIGUEDAD", VariableType.DOUBLE, false),
	QUOTE_PECULIARITY_COLLECTIVE("COLECT_PECULIAR_COTIZACION", VariableType.INTEGER, false),
	CONTRACT_END_CODE("COD_FIN_CONTRATO", VariableType.INTEGER, false),
	CONTRACT_END_DESC("DESC_FIN_CONTRATO", VariableType.STRING, false),
	
	// Bajas, Incapacidad Temporal
	MATERNITY("MTNAD", VariableType.BOOLEAN),
	QUOTE_IT("COTIZACION_IT", VariableType.QUOTE_IT_DROP),
	TOTAL_BENEFITS_IT("TOTAL_PRESTACIONES_IT", VariableType.DOUBLE),
	
	// Régimenes, cotizacion 
	MORE_THAN_65("MAYOR_65", VariableType.BOOLEAN, false),
	ASSIMILATED ("ASIMILADO_REGIMEN_GRAL", VariableType.BOOLEAN, false),
	ENTRY_BY_COMPANY_ACCOUNT("INGRESO_AC_EMPRESA", VariableType.BOOLEAN, false),
	
	// Embargos 
	EMBARGO_LEFT("PENDIENTE", VariableType.DOUBLE, false),
	EMBARGO_PAID("EMBARGADO", VariableType.DOUBLE, false),
	EMBARGO_LIMIT("EMBARGABLE", VariableType.DOUBLE, false),
	EMBARGO_MAX("MAX_EMBARGABLE", VariableType.DOUBLE, false),
	
	// Tipo de nomina
	SALARY("NOMINA", VariableType.BOOLEAN),
	DELAY("ATRASOS", VariableType.BOOLEAN),
	SETTLE("FINIQUITO", VariableType.BOOLEAN),
	EXTRA_PAY("EXTRA", VariableType.BOOLEAN),
	
	
	// Resultados
	TOTAL_LIQUID("TOTAL_LIQUIDO", VariableType.DOUBLE),
	TOTAL_PAYMENT("TOTAL_DEVENGADO", VariableType.DOUBLE),
	
	//
	CURRENT("ACTUAL", VariableType.UNKNOWN),

	// Datos de las bonificaciones
	BONUS_AGE("DURACION", VariableType.DATE, false),
	BONUS_START("INICIO", VariableType.DATE, false),
	SUBSIDIZED("BONIFICADO", VariableType.BOOLEAN),
	
	// Excel
	OR("O", VariableType.BOOLEAN ),
	AND("Y", VariableType.BOOLEAN ),
	IF("SI", VariableType.BOOLEAN ),
	NOT("NO", VariableType.BOOLEAN ),
	FALSE("FALSO", VariableType.BOOLEAN ),
	TRUE("VERDADERO", VariableType.BOOLEAN ),
	ABS("ABS", VariableType.DOUBLE ),
	POW("POTENCIA", VariableType.DOUBLE ),
	SQRT("RAIZ", VariableType.DOUBLE ),
	INTEGER("ENTERO", VariableType.INTEGER ),
	QUOTIENT("COCIENTE", VariableType.INTEGER ),
	DAYS("DIAS", VariableType.INTEGER ),

	// AON's
	YEAR("AÑO", VariableType.UNKNOWN ),
	TWO("BIENIO", VariableType.UNKNOWN ),
	THREE("TRIENIO", VariableType.UNKNOWN ),
	FOUR("CUATRIENIO", VariableType.UNKNOWN ),
	FIVE("QUINQUENIO", VariableType.UNKNOWN ),
	SIX("SEXENIO", VariableType.UNKNOWN ),
	SEVEN("SEPTENIO", VariableType.UNKNOWN ),
	
	OLD("ANTIGÜEDAD", VariableType.DOUBLE ),
	EXCESS("EXCESO", VariableType.DOUBLE ),
	
	IT_START("INICIO_IT", VariableType.DATE),

	PROFESSION("PROFESION", VariableType.STRING),
	ENTERPRISE_SITE_DATE("VISIONADOE", VariableType.DATE),
	
	PAYMENT("_P", VariableType.DOUBLE)
	
	;
	
	public static final String ALL = "_P";
	public static final String SELF = "SELF";
	public static final String REMOVE = "REMOVE";
	public static final String CONTEXT = "CONTEXT";

	public static final String BR = "BR";
	public static final String GROSS = "BRUTO";
	public static final String LIQUID = "NETO";
	public static final String SYSTEM = "SISTEMA";
	public static final String GUARANTEE = "GTZDO";
	public static final String AGREEMENT = "CONVENIO";
	public static final String DELAY_PREVENT = "ANTICIPO_ATRASOS";

	// Old 
	public static final String CHECK = "CHECK";
	public static final String MONTHS = "MESES";
	public static final String WARNING = "AVISO";
	public static final String ISDEF = "DEFINIDA";
	public static final String CHECK_VAR = "CHECK_VAR";
	
	public static final String OLDD = "ANTIGUEDAD";
	public static final String BASE_SALARY = "SALARIO_BASE";
	public static final String GUARENTEE_IT = "GARANTIZADO_IT";
	
	private final String name;
	private VariableType type;
	private final boolean internal;
	
	private ContextVariable(String name, VariableType type){
		this(name, type, true);
	}

	private ContextVariable(String name, VariableType type, boolean internal){
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
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
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
    public String getDescription(Locale locale) {
    	ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
    	return bundle.getString(EXT_MSG_KEY_PREFIX + super.toString());
    }
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_context_variable_";
    private static final String EXT_MSG_KEY_PREFIX = "aon_enum_context_variable_desc_";

    
    public static ContextVariable getVariableByName(String name){
    	for(ContextVariable cv: values()){
    		if(cv.getName().equals(name)){
    			return cv;
    		}
    	}
    	return null;
    }
	

}
