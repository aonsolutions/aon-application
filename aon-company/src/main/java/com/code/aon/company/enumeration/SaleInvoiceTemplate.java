package com.code.aon.company.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum SaleInvoiceTemplate implements IResourceable {

	DEFAULT("saleInvoice"),
	
	STANDARD_01("saleInvoiceTemplate2"),
	
	STANDARD_02("saleInvoiceTemplate3"),
	
	PROFESSIONAL_01("saleInvoiceTemplate1"),
	
	PROFESSIONAL_02("saleInvoiceTemplate4"),
	
	GTA("saleInvoiceGta"),
	
	HOTEL("saleInvoiceHotel")
	
	;      
	
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
