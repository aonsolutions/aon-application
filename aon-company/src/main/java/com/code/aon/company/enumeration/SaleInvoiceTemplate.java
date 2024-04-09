package com.code.aon.company.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum SaleInvoiceTemplate implements IResourceable {

	DEFAULT("default"),
	
	@Deprecated
	STANDARD("saleInvoice"),
	
	@Deprecated
	STANDARD_01("saleInvoiceTemplate2"),
	
	@Deprecated
	STANDARD_02("saleInvoiceTemplate3"),
	
	@Deprecated
	PROFESSIONAL_01("saleInvoiceTemplate1"),
	
	@Deprecated
	PROFESSIONAL_02("saleInvoiceTemplate4"),
	
	@Deprecated
	CLASSIC_01("saleInvoiceTemplate5"),
	
	@Deprecated
	CLASSIC_02("saleInvoiceTemplate6"),
	
	@Deprecated
	CLASSIC_PROF_01("saleInvoiceTemplate7"),
	
	@Deprecated
	GTA("saleInvoiceGta"),
	
	@Deprecated
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
