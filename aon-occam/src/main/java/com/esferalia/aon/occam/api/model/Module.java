package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;

public enum Module {

	@Deprecated
	MARKETING( "marketing" ),
	CRM( "crm" ),
	MANAGEMENT( "management" ),
	@Deprecated
	TREASURY( "treasury" ),
	WAREHOUSE( "warehouse" ),
	GROUPWARE( "groupware" ),
	ACCOUNTING( "accounting" ),
	FISCAL( "fiscal" ),
	PAYROLL( "payroll" ),
	DOCUMENT( "document" ),
	GARAGE( "garage" ),
	ACADEMY( "academy" ),
	HOTEL( "hotel" ),
	INFOWEB( "infoweb" ),
	PAYROLL_PORTAL( "payroll_portal" ),
	DOCUMENT_PORTAL( "document_portal" ),
	POS( "pos" ),
	CONTRATA( "contrata" ),
	CONFIGURATION( "configuration" ),
	AON_ONE( "aonOne" ),
	ECOMMERCE( "eCommerce" ),
	CALL_CENTER( "call_center" ),
	FINANCE_PORTAL( "finance_portal" ),
	AON_FINANCE( "aonFinance" );

    
	private String name;
	
	private Module(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Module get(String name) {
    	for( Module module : Module.values() ) {
    		if ( module.getName().equals(name) ) {
    			return module;
    		}
    	}
    	return null;
	}
   
	public static Module safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= Module.values().length) return null;
		return Module.values()[i];
	}
	
	
}