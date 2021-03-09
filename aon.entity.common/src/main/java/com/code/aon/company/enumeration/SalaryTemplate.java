package com.code.aon.company.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum SalaryTemplate implements IResourceable {

	DEFAULT("salary")
	
	,NOMINASTA("nominasta")

	,NOMINASTA_CODINT("nominasta_codint")

	,NOMINASTA_CONDDIAS("nominasta_condias")

	,NOMINASTA_LDH("nominasta_ldh")
	
	,IDAZKIAK_ES("idazkiak_es")
	
	,STANDARD_DUAL_COLUMN("salary_dualColumn")

	,INVOICE_SIMPLE("salary_invoiceSimple")
	
	,INVOICE_CRA_GROUP("salary_invoiceCraGroup")

	,AON_SOLUTIONS_MACLEOD("salary_connorMacleod")
	
	;      
	
    private static final String MSG_KEY_PREFIX = "aon_enum_salary_template_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    private String value;
    
    SalaryTemplate( String value ) {
      	this.value = value;
  	}
    
    public String getValue() {
    	return value;
    }
    
    
}
