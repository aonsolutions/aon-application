package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractVariables implements IResourceable{
	
	// Dias
	YEAR_DAYS("DIAS_AÑO", VariableType.INTEGER),
	MONTH_DAYS("DIAS_MES", VariableType.INTEGER),
	HOLIDAYS("DIAS_VACACIONES", VariableType.INTEGER),
	NO_HOLIDAYS("DIAS_VACACIONES_NO_DISFRUTADOS", VariableType.INTEGER),
	WORKED_DAYS("DIAS_TRABAJADOS", VariableType.INTEGER),
	WEEK_DAYS("DIAS_SEMANA", VariableType.INTEGER),
	CONTRACT_DAYS("DIAS_CONTRATO", VariableType.INTEGER),
	SALARY_DAYS("DIAS_NOMINA", VariableType.INTEGER),
	PAY_DAYS("DIAS_PAGA", VariableType.INTEGER),
	EXTRA_DAYS("DIAS_PAGA", VariableType.INTEGER),
	BONUS_DAYS("DIAS_BONIFICACION", VariableType.INTEGER),
	QUOTE_DAYS("DIAS_TRABAJADOS_INC_VAC", VariableType.INTEGER),
	ACTUAL_DAYS("DIAS_EFECTIVOS", VariableType.INTEGER),
	LEAVE_DAYS("DIAS_IT", VariableType.INTEGER),
	GUARANTEED_DAYS("DIAS_GARANTIZADOS", VariableType.INTEGER),
	SPECIAL_DAYS("DIAS_ESPECIALES", VariableType.INTEGER),
	MATERNITY_DAYS("DIAS_MATERNIDAD", VariableType.INTEGER),
	COMMON_DISEASE_DAYS("DIAS_ENFERMEDAD_COMUN", VariableType.INTEGER),
	OCCUPATIONAL_DISEASE_DAYS("DIAS_ENFERMEDAD_PROFESIONAL", VariableType.INTEGER),
	PAYMENTS("NUM_PAGAS", VariableType.INTEGER),

	WORKED_MONTHS("MESES_TRABAJADOS", VariableType.INTEGER),
	SALARY_MONTHS("MESES_NOMINA", VariableType.INTEGER),
	PAY_MONTHS("MESES_PAGA", VariableType.INTEGER),
	WORKED_WEEKS("SEMANAS_TRABAJADAS", VariableType.INTEGER),
	SALARY_WEEKS("SEMANAS_NOMINA", VariableType.INTEGER),
	PAY_WEEKS("SEMANAS_PAGA", VariableType.INTEGER),
	
	// Horas ( contratos  a tiempo parcial )
	WEEK_HOURS("HORAS_SEMANA", VariableType.DOUBLE),
	SALARY_HOURS("HORAS_NOMINA", VariableType.DOUBLE),

	HOLIDAY_AMOUNT("IMPORTE_DIA_VACACIONES", VariableType.DOUBLE),
	COMPENSATION_AMOUNT("IMPORTE_INDEMNIZACION", VariableType.DOUBLE),

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
	TC2("TC2", VariableType.TC2_DROP),
	CNO("CNO", VariableType.CNO_DROP),
	IPREM("IPREM", VariableType.DOUBLE),
	CATEGORY("CATEGORIA", VariableType.CATEGORY_DROP),
	INDEFINITE("INDEFINIDO", VariableType.BOOLEAN),
	OCCUPATION("OCUPACION", VariableType.STRING),
	GUARANTEED("GARANTIZADO", VariableType.DOUBLE),
	IRREGULAR("IRREGULAR", VariableType.BOOLEAN),
	FULL_TIME("TIEMPO_COMPLETO", VariableType.BOOLEAN),
	IRPF_PERCENT("PORCENTAJE_IRPF", VariableType.DOUBLE),
	QUOTE_GROUP("GRUPO_COTIZACION", VariableType.QUOTE_GROUP_DROP),
	FREE_IPREM("EXENTO_IPREM", VariableType.DOUBLE),
	FREE_IPREM_SHORT("XIPREM", VariableType.DOUBLE),
	IT_RATE("TARIFA_IT", VariableType.DOUBLE),
	IMS_RATE("TARIFA_IMS", VariableType.DOUBLE),
	SHORT_CONTRACT("CONTRATO_CORTA_DURACION", VariableType.BOOLEAN),
	
	// Bajas, Incapacidad Temporal
	MATERNITY("MTNAD", VariableType.BOOLEAN),
	QUOTE_IT("COTIZACION_IT", VariableType.DOUBLE),
	TOTAL_BENEFITS_IT("TOTAL_PRESTACIONES_IT", VariableType.DOUBLE),
	
	// Régimenes, cotizacion 
	MORE_THAN_65("MAYOR_65", VariableType.BOOLEAN),
	ASSIMILATED ("ASIMILADO_REGIMEN_GRAL", VariableType.BOOLEAN),
	ENTRY_BY_COMPANY_ACCOUNT("INGRESO_AC_EMPRESA", VariableType.BOOLEAN),
	
	// Embargos 
	EMBARGO_LEFT("PENDIENTE", VariableType.DOUBLE),
	EMBARGO_PAID("EMBARGADO", VariableType.DOUBLE),
	EMBARGO_LIMIT("EMBARGABLE", VariableType.DOUBLE),
	EMBARGO_MAX("MAX_EMBARGABLE", VariableType.DOUBLE),
	
	// Tipo de nomina
	SALARY("NOMINA", VariableType.BOOLEAN),
	DELAY("ATRASOS", VariableType.BOOLEAN),
	SETTLE("FINIQUITO", VariableType.BOOLEAN),
	EXTRA_PAY("PAGA_EXTRA", VariableType.BOOLEAN),
	
	
	// Resultados
	TOTAL_LIQUID("TOTAL_LIQUIDO", VariableType.DOUBLE),
	TOTAL_PAYMENT("TOTAL_DEVENGADO", VariableType.DOUBLE),
	
	//
	CURRENT("ACTUAL", VariableType.UNKNOWN),
	
	;
	
	private final String name;
	private VariableType type;
	
	private ContractVariables(String name, VariableType type){
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public VariableType getType() {
		return type;
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

    /** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_variables_";
    private static final String EXT_MSG_KEY_PREFIX = "aon_enum_contract_variables_desc_";

    
    public static ContractVariables getVariable(String name){
    	for(ContractVariables cv: values()){
    		if(cv.getName().equals(name)){
    			return cv;
    		}
    	}
    	return null;
    }
	
}
