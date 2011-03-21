package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractVariables implements IResourceable{
	
	YEAR_DAYS("DIAS_AÑO"),
	MONTH_DAYS("DIAS_MES"),
	HOLIDAYS("DIAS_VACACIONES"),
	WORKED_DAYS("DIAS_TRABAJADOS"),
	ACTUAL_DAYS("DIAS_EFECTIVOS"),
	LEAVE_DAYS("DIAS_DE_BAJA"),
	SPECIAL_DAYS("DIAS_ESPECIALES"),
	SENIOR_BASE("BASE_ANTIGUEDAD"),
	
	CGC_BASE("BASE_CGC"),
	CGP_BASE("BASE_CGP"),
	STRUCTURAL_OVERTIME_BASE("BASE_ESTR"),
	NON_STRUCTURAL_OVERTIME_BASE("BASE_NESTR"),
	IRPF_BASE("BASE_IRPF"),

	IRPF_PERCENT("PORCENTAJE_IRPF"),
	QUOTE_GROUP("GRUPO_COTIZACION"),
	TC2("TC2"),
	CATEGORY("CATEGORIA"),
	
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
