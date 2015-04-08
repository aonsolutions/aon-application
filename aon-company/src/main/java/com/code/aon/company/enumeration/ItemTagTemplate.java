package com.code.aon.company.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ItemTagTemplate implements IResourceable {

	/* Apli 525 x 297 (4 columnas) */
	TEMPLATE_1("itemTag_525x297"),

	/* Zebra 505 x 255 (1 columna) */
	TEMPLATE_2("itemTag_505x255_1_column"),
	
	;      
	
    private static final String MSG_KEY_PREFIX = "aon_enum_itemTag_template_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    private String value;
    
    ItemTagTemplate( String value ) {
      	this.value = value;
  	}
    
    public String getValue() {
    	return value;
    }
    
    public static ItemTagTemplate getEnumByValue(String value){
    	for(ItemTagTemplate o: ItemTagTemplate.values()){
			if(o.getValue().equals(value)){
				return o;
			}
		}
    	return null;
    }
    
}
