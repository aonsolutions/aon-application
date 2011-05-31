package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractVariables implements IResourceable{
	
	// Dias
	YEAR_DAYS("DIAS_AÑO"),
	MONTH_DAYS("DIAS_MES"),
	HOLIDAYS("DIAS_VACACIONES"),
	WORKED_DAYS("DIAS_TRABAJADOS"),
	SALARY_DAYS("DIAS_NOMINA"),
	QUOTE_DAYS("DIAS_TRABAJADOS_INC_VAC"),
	ACTUAL_DAYS("DIAS_EFECTIVOS"),
	LEAVE_DAYS("DIAS_IT"),
	GUARANTEED_DAYS("DIAS_GARANTIZADOS"),
	SPECIAL_DAYS("DIAS_ESPECIALES"),
	MATERNITY_DAYS("DIAS_MATERNIDAD"),
	COMMON_DISEASE_DAYS("DIAS_ENFERMEDAD_COMUN"),
	OCCUPATIONAL_DISEASE_DAYS("DIAS_ENFERMEDAD_PROFESIONAL"),
	
	// Horas ( contratos  a tiempo parcial )
	WEEK_HOURS("HORAS_SEMANA"),
	SALARY_HOURS("HORAS_NOMINA"),

	// Bases 
	CGC_BASE("BASE_CGC"),
	CGP_BASE("BASE_CGP"),
	IRPF_BASE("BASE_IRPF"),
	IPREM_BASE("BASE_IPREM"),
	IPREM_BASE_SHORT("BIPREM"),
	CGC_BASE_MIN("BASE_CGC_MIN"),
	CGC_BASE_MAX("BASE_CGC_MAX"),
	CGP_BASE_MIN("BASE_CGP_MIN"),
	CGP_BASE_MAX("BASE_CGP_MAX"),
	SENIOR_BASE("BASE_ANTIGUEDAD"),
	REGULATORY_BASE("BASE_REGULADORA"),
	STRUCTURAL_OVERTIME_BASE("BASE_ESTR"),
	NON_STRUCTURAL_OVERTIME_BASE("BASE_NESTR"),

	// Datos 'temporales' del contrato
	TC2("TC2"),
	IPREM("IPREM"),
	CATEGORY("CATEGORIA"),
	INDEFINITE("INDEFINIDO"),
	GUARANTEED("GARANTIZADO"),
	FULL_TIME("TIEMPO_COMPLETO"),
	IRPF_PERCENT("PORCENTAJE_IRPF"),
	QUOTE_GROUP("GRUPO_COTIZACION"),
	FREE_IPREM("EXENTO_IPREM"),
	FREE_IPREM_SHORT("XIPREM"),
	
	// Bajas, Incapacidad Temporal
	MATERNITY("MTNAD"),
	QUOTE_IT("COTIZACION_IT"),
	TOTAL_BENEFITS_IT("TOTAL_PRESTACIONES_IT"),
	
	// Régimenes, cotizacion 
	MORE_THAN_65("MAYOR_65"),
	ASSIMILATED ("ASIMILADO_REGIMEN_GRAL"),
	ENTRY_BY_COMPANY_ACCOUNT("INGRESO_AC_EMPRESA"),
	
	// Embargos 
	EMBARGO_LEFT("PENDIENTE"),
	EMBARGO_PAID("EMBARGADO"),
	EMBARGO_LIMIT("EMBARGABLE"),
	EMBARGO_MAX("MAX_EMBARGABLE"),
	
	// Tipo de nomina
	SALARY("NOMINA"),
	DELAY("ATRASOS"),
	SETTLE("FINIQUITO"),
	EXTRA_PAY("PAGA_EXTRA"),
	
	
	// Resultados
	TOTAL_LIQUID("TOTAL_LIQUIDO"),
	TOTAL_PAYMENT("TOTAL_DEVENGADO"),
	
	// TODO se pone en ultimo lugar para que no afecte al orden existente en la base de datos
	NO_HOLIDAYS("DIAS_VACACIONES_NO_DISFRUTADOS"),
	CONTRACT_DAYS("DIAS_CONTRATO"),
	WEEK_DAYS("DIAS_SEMANA"),
	IRREGULAR("IRREGULAR"),
	CNO("CNO"),
	;
	
	private final String name;
	
	private ContractVariables(String name){
		this.name= name;
	}
	
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_variables_";

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
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
	
}
