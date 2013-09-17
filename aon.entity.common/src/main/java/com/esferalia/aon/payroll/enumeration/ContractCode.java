package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum ContractCode implements IResourceable, IStringEnum {

	C100("100", null),
	C109("109", null),
	C130("130", null),
	C139("139", null),
	C150("150", null),
	C189("189", null),
	
	C200("200", null),
	C209("209", null),
	C230("230", null),
	C239("239", null),
	C250("250", null),
	C289("289", null),
	
	C300("300", null),
	C309("309", null),
	C330("330", null),
	C350("350", null),
	C389("389", null),
	
	C401("401", null),
	C402("402", null),
	C403("403", null),
	C408("408", null),
	C410("410", null),
	C418("418", null),
	C420("420", null),
	C421("421", null),
	C430("430", null),
	C441("441", null),
	C450("450", null),
	C452("452", null),
	
	C501("501", null),
	C502("502", null),
	C503("503", null),
	C508("508", null),
	C510("510", null),
	C518("518", null),
	C520("520", null),
	C530("530", null),
	C540("540", null),
	C541("541", null),
	C550("550", null),
	C552("552", null), 

	C970("970", null),
	C980("980", null),
	C990("990", null)
	
	;

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_code_";

	
	@Override
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	
	private String value;
	private ContractModel model;
    
    ContractCode( String value, ContractModel model ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
    public ContractModel getModel(){
		return model;
	}
    public void setModel(ContractModel model){
    	this.model = model;
    }

	public static ContractCode getContractCodeByValue(String expression) {
		for( ContractCode c : ContractCode.values() ) {
    		if ( c.getValue().equals(expression) ) {
    			return c;
    		}
    	}
    	return null;
	}
	
	
}
