package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractVariables implements IResourceable{
	
	YEAR_DAYS("DIAS_AÑO"),
	MONTH_DAYS("DIAS_MES"),
	HOLIDAYS("DIAS_VACACIONES"),
	WORKED_DAYS("DIAS_TRABAJADOS"),
	QUOTE_DAYS("DIAS_TRABAJADOS_INC_VAC"),
	ACTUAL_DAYS("DIAS_EFECTIVOS"),
	LEAVE_DAYS("DIAS_IT"),
	GUARANTEED_DAYS("DIAS_GARANTIZADOS"),
	SPECIAL_DAYS("DIAS_ESPECIALES"),
	MATERNITY_DAYS("DIAS_MATERNIDAD"),
	COMMON_DISEASE_DAYS("DIAS_ENFERMEDAD_COMUN"),
	OCCUPATIONAL_DISEASE_DAYS("DIAS_ENFERMEDAD_PROFESIONAL"),
	
	SENIOR_BASE("BASE_ANTIGUEDAD"),
	
	CGC_BASE("BASE_CGC"),
	CGP_BASE("BASE_CGP"),
	STRUCTURAL_OVERTIME_BASE("BASE_ESTR"),
	NON_STRUCTURAL_OVERTIME_BASE("BASE_NESTR"),
	REGULATORY_BASE("BASE_REGULADORA"),
	IRPF_BASE("BASE_IRPF"),

	TC2("TC2"),
	CATEGORY("CATEGORIA"),
	GUARANTEED("GARANTIZADO"),
	IRPF_PERCENT("PORCENTAJE_IRPF"),
	QUOTE_GROUP("GRUPO_COTIZACION"),
	
	QUOTE_IT("COTIZACION_IT"),
	TOTAL_BENEFITS_IT("TOTAL_PRESTACIONES_IT"),

	INDEFINITE("INDEFINIDO");
	
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
