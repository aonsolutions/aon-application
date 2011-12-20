package com.code.aon.company.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum SaleInvoiceTemplate implements IResourceable {

	DEFAULT("saleInvoice"),
	
	TEMPLATE1("saleInvoiceTemplate1"),
	
	TEMPLATE2("saleInvoiceTemplate2"),

	TEMPLATE3("saleInvoiceTemplate3")
	
	;      
	
    private static final String BASE_NAME = "com.code.aon.company.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_saleInvoice_template_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    private String value;
    
    SaleInvoiceTemplate( String value ) {
      	this.value = value;
  	}
    
    public String getValue() {
    	return value;
    }
    
    public static SaleInvoiceTemplate getEnumByValue(String value){
    	for(SaleInvoiceTemplate o: SaleInvoiceTemplate.values()){
			if(o.getValue().equals(value)){
				return o;
			}
		}
    	return null;
    }
    
}
