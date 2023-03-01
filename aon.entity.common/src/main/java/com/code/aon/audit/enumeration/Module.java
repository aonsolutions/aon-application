package com.code.aon.audit.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;


/**
 * Enummeration to identify the modules of an application.
 * 
 * @author esferalia Networks. Aimar Tellitu - 28-abr-2012
 * @since 1.0
 * @version 1.0
 */
public enum Module implements IResourceable {

	MARKETING( "marketing" ),
	CRM( "crm" ),
	MANAGEMENT( "management" ),
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
	AON_FINANCE( "aonFinance" ),
	SUITE_PORTAL("suite_portal");

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_module_";
    
	private String name;
	
	private Module(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale Required Locale.
     * 
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
	

    /**
     * Return the Module.
     * 
     * @param name Module name.
     * @return The Module.
     */
	public static Module get(String name) {
    	for( Module module : Module.values() ) {
    		if ( module.getName().equals(name) ) {
    			return module;
    		}
    	}
    	return null;
	}
    
}