package com.code.aon.company.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum FinancePaymentTemplate implements IResourceable {

	/* IPAR KUTXA */
	TEMPLATE_1("fPaymentList"),

	/* CAJA MAR */
	TEMPLATE_2("fPaymentListTemplate2"),
	
	/* LABORAL KUTXA */
	TEMPLATE_3("fPaymentListTemplate3"),
	
	;      
	
    private static final String MSG_KEY_PREFIX = "aon_enum_financePayment_template_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    private String value;
    
    FinancePaymentTemplate( String value ) {
      	this.value = value;
  	}
    
    public String getValue() {
    	return value;
    }
    
    public static FinancePaymentTemplate getEnumByValue(String value){
    	for(FinancePaymentTemplate o: FinancePaymentTemplate.values()){
			if(o.getValue().equals(value)){
				return o;
			}
		}
    	return null;
    }
    
}
